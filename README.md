# Jenkins Pipeline

Jenkins Pipeline Shared Library.  Contains helper functions to be used with the Jenkins Pipeline Plugin.

## Image Class

The Docker `Image` class expects 3 or 4 arguments in its constructor call

| # | Type     | Required  | Description |
|---|----------|:---------:|-------------|
| 1 | Script   |     Y     | A reference to the Script object, always `this` when instantiated from the Jenkinsfile. |
| 2 | String   |     Y     | The name of the image, including the Docker Hub organization.  i.e. `invocaops/ruby`. |
| 3 | String[] |     Y     | An array of tags to apply to the image. |
| 4 | String   |     N     | The directory that the Dockerfile is in.  Useful when multiple versions of the image need to be built.  Defaults to the directory the Jenkinsfile is in. |

#### Example
Example for Ruby 2.4.2, which is in a directory named `2.4.2` and being built from the master branch with SHA `12345`:
```groovy
image = new Image(this, "invocaops/ruby", ["2.4.2-12345", "2.4.2-master"], "2.4.2")
```

### Usage

The `Image` class has 4 main methods to perform operations

| Method  | Arguments                      | Action                                       |
|---------|--------------------------------|----------------------------------------------|
| build() | [buildArgs (Map)](#build-args) | Buils the Docker image.                      |
| tag()   | None                           | Tags the image.                              |
| push()  | None                           | Pushes the image and its tags to Docker Hub. |

Each method returns a reference to the `Image` object, so chaining is possible.

### Build Args

The `Image#build` method takes a `Map` of build arguments.

| Argument   | Type   | Required | Description                                          |
|------------|--------|:--------:|------------------------------------------------------|
| gitUrl     | String |     Y    | URL to remote Git repository.  Set to `env.GIT_URL`. | 
| buildArgs  | Map    |     N    | `foo=bar` pairings for `docker build --build-arg`.   |
| dockerFile | String |     N    | Name of `Dockerfile` file, defaults to `Dockerfile`. |

### Environment

In addition to the included Git environment variables, we currently assume access to credentials for DockerHub. You'll need to explicitly set these in your environment.

| Variable           | Available By Default | Description                           |
|--------------------|:--------------------:|---------------------------------------|
| DOCKERHUB_USER     |           N          | Username for DockerHub.               |
| DOCKERHUB_PASSWORD |           N          | Password for DockerHub.               |
| GIT_COMMIT         |           Y          | SHA of current build.                 |
| GIT_URL            |           Y          | URL of GitHub repository being built. |
| GIT_BRANCH         |           Y          | The name of the checked out branch.   |

## Getting Started

To use this library, start your `Jenkinsfile` with:

```groovy
@Library('github.com/invoca/jenkins-pipeline@v0.1.0')
```

After, parts of the library can be imported and used.  Below is an example of a `Jenkinsfile` that builds multiple versions of the Ruby image.

```groovy
@Library('github.com/invoca/jenkins-pipeline@v0.1.0')

import com.invoca.docker.*;

pipeline {
  agent any
  stages {
    stage('Setup') {
      steps {
        script {
          def imageName = "invocaops/ruby"
          def directories = sh(script: 'ls **/Dockerfile | while read dir; do echo $(dirname $dir); done', returnStdout: true).split("\n")
          def sha = env.GIT_COMMIT
          def branchName = env.GIT_BRANCH

          images = directories.collect {
            String[] tags = ["${it}-${branchName}", "${it}-${sha}"]
            new Image(this, imageName, tags, it)
          }
        }
      }
    }

    stage('Build') {
      steps {
        script {
          for (Image image : images) {
            image.build(gitUrl: env.GIT_URL).tag()
          }
        }
      }
    }

    stage('Push') {
      environment {
        DOCKERHUB_USER = credentials('dockerhub_user')
        DOCKERHUB_PASSWORD = credentials('dockerhub_password')
      }
      steps {
        script {
          new Docker().hubLogin(env.DOCKERHUB_USER, env.DOCKERHUB_PASSWORD)
          for (Image image : images) {
            image.push()
          }
        }
      }
    }
  }

  post {
    always {
      notifySlack(currentBuild.result)
    }
  }
}
```

Please read more about libraries in the [Jenkins documentation](https://jenkins.io/doc/book/pipeline/shared-libraries/).
