def call(body) {
  def pipelineParams= [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = pipelineParams
  body()

  pipeline {
    agent none

    environment {
      SSH_KEY = credentials('github-ssh-key')
    }
    stages {
      stage('Run tests') {
        parallel {
          stage('Run unit tests') {
            agent {
              kubernetes {
                label 'leroy-unit'
                containerTemplate {
                  name 'ruby'
                  image 'ruby:2.3-jessie'
                  ttyEnabled true
                  command 'cat'
                }
              }
            }
            steps {
              container('ruby') {
                sh """
                  eval `ssh-agent -s`
                  echo "$SSH_KEY" | ssh-add -
                  mkdir -p /root/.ssh
                  ssh-keyscan -t rsa github.com > /root/.ssh/known_hosts
                  bundle install
                  bundle exec berks install
                  """
                sh "bundle exec rake jenkins:unit"
              }
            }
          }
          stage('Run integration tests') {
            agent {
              kubernetes {
                label 'leroy-integration'
                containerTemplate {
                  name 'ruby'
                  image 'ruby:2.3-jessie'
                  ttyEnabled true
                  command 'cat'
                }
              }
            }
            steps {
              container('ruby') {
                sh """
                  eval `ssh-agent -s`
                  echo "$SSH_KEY" | ssh-add -
                  mkdir -p /root/.ssh
                  ssh-keyscan -t rsa github.com > /root/.ssh/known_hosts
                  bundle install
                  bundle exec berks install
                  """
                sh "bundle exec rake jenkins:integration"
              }
            }
          }
        }
      }
    }
  }
}
