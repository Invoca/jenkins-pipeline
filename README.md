# Jenkins Pipeline

Jenkins Pipeline Shared Library, contains helper functions to be used with the Jenkins Pipeline Plugin.

## Configuration

The `Docker` library assumes a `Map` of specific values be passed to `imageBuild()`

| Argument   | Type   | Required | Description                                       |
|------------|--------|:--------:|---------------------------------------------------|
| build_args | List   |     N    | `foo=bar` pairings for `docker build --build-arg` |
| dockerfile | String |     Y    | Location of the Dockerfile to be built            |
| image_name | String |     Y    | e.g. `invocaops/ruby_mysql`                       |

### Environment

In addition to the included Git environment variables, we currently assume access to credentials for DockerHub. You'll need to explicitly set these in your environment.

| Variable           | Available By Default | Description                          |
|--------------------|:--------------------:|--------------------------------------|
| DOCKERHUB_USER     |           N          | Username for DockerHub               |
| DOCKERHUB_PASSWORD |           N          | Password for DockerHub               |
| GIT_COMMIT         |           Y          | SHA of current build                 |
| GIT_URL            |           Y          | URL of GitHub repository being built |

## Getting Started

To use this library, start your `Jenkinsfile` with:

```groovy
@Library('github.com/invoca/jenkins-pipeline@master')
```

After, parts of the library can be imported and used as follows:

```groovy
def docker = new io.invoca.Docker()

pipeline {
    agent any
    stages {
        stage('Setup') {
            environment {
                DOCKERHUB_USER = credentials('dockerhub_user')
                DOCKERHUB_PASSWORD = credentials('dockerhub_password')
            }
            steps {
                script {
                    imageArgs = [
                        dockerfile: '.',
                        image_name: 'org/name',
                    ]
                }
            }
        }
        stage('Build') {
            steps {
                script { docker.imageBuild(imageArgs) }
            }
        }
        stage('Push') {
            steps {
                script { docker.imageTagPush(imageArgs.image_name) }
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

Please read the more about libraries in the [Jenkins documentation](https://jenkins.io/doc/book/pipeline/shared-libraries/).
