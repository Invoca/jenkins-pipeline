def call(Closure body = null) {
  def uuid = UUID.randomUUID().toString()
  def pipelineParams= [:]

  if (body != null) {
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()
  }

  pipeline {
    agent none

    environment {
      GITHUB_SSH_KEY = credentials('github-ssh-key')
      TEST_KITCHEN_SSH_KEY = credentials('test-kitchen-ssh-key')
      AWS_CREDENTIALS = credentials('aws-test-kitchen')
      AWS_ACCESS_KEY_ID = "${env.AWS_CREDENTIALS_USR}"
      AWS_SECRET_ACCESS_KEY = "${env.AWS_CREDENTIALS_PSW}"
      AWS_DEFAULT_REGION = 'us-east-1'
    }
    stages {
      stage('Run tests') {
        parallel {
          stage('Run unit tests') {
            agent {
              kubernetes {
                label "leroy-unit-${uuid}"
                containerTemplate {
                  name 'ruby'
                  image 'invocaops/chef-ci:master'
                  alwaysPullImage true
                  ttyEnabled true
                  command 'cat'
                  resourceRequestCpu '500m'
                  resourceLimitMemory '1Gi'
                }
              }
            }
            steps {
              container('ruby') {
                sh """
                  eval `ssh-agent -s`
                  echo "$GITHUB_SSH_KEY" | ssh-add -
                  mkdir -p /root/.ssh
                  ssh-keyscan -t rsa github.com > /root/.ssh/known_hosts
                  bundle install
                  bundle exec berks install
                  bundle exec rake jenkins:unit
                  """
              }
            }
          }
          stage('Run integration tests') {
            agent {
              kubernetes {
                label "leroy-integration-${uuid}"
                containerTemplate {
                  name 'ruby'
                  image 'invocaops/chef-ci:master'
                  alwaysPullImage true
                  ttyEnabled true
                  command 'cat'
                  resourceRequestCpu '500m'
                  resourceLimitMemory '500Mi'
                }
              }
            }
            steps {
              retry(3) {
                container('ruby') {
                  sh """
                    eval `ssh-agent -s`
                    echo "$GITHUB_SSH_KEY" | ssh-add -
                    echo "$TEST_KITCHEN_SSH_KEY" | ssh-add -
                    mkdir -p /root/.ssh
                    ssh-keyscan -t rsa github.com > /root/.ssh/known_hosts
                    bundle install
                    bundle exec berks install
                    bundle exec rake jenkins:integration
                    """
                }
              }
            }
          }
        }
      }
    }
  }
}
