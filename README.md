# Jenkins Pipeline

Jenkins Pipeline Shared Library, contains helper functions and classes to be used with the Jenkins Pipeline Plugin.

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
        stage('Build and push') {
            script {
                docker.containerBuildPush(
                    dockerfile: '.',
                    org:        'invocaops',
                    image:      'imagename'
                )
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
