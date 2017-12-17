#!/usr/bin/groovy
package com.invoca;

/* args
  buildArgs  list (optional)
  dockerfile string
  imageName  string
*/

@NonCPS
def buildCommand(Map args) {
  // http://label-schema.org/rc1/
  def cmd = "docker build -t ${args.imageName}:$GIT_COMMIT \
      --label org.label-schema.schema-version=1.0 \
      --label org.label-schema.build-date=`date -u +\"%Y-%m-%dT%H:%M:%SZ\"` \
      --label org.label-schema.vcs-url=$GIT_URL"

  if (args.buildArgs) {
    cmd += args.buildArgs.collect { " --build-arg ${it}" }.join(" ")
  }

  cmd += " ${args.dockerfile}"
  return cmd
}

def imageBuild(Map args) {
  sh buildCommand(args)
}

def imagePush(String imageName, String tag) {
  sh "docker push ${imageName}:${tag}"
}

def imageRemove(String imageName, String tag) {
  sh "docker rmi ${imageName}:${tag}"
}

def imageTag(String imageName, String currentTag, String newTag) {
  sh "docker tag ${imageName}:${currentTag} ${imageName}:${newTag}"
}

def imageTagPush(String imageName) {
  hubLogin()
  imagePush(imageName, env.GIT_COMMIT)

  if (env.GIT_BRANCH == 'master') {
    imageTag(imageName, env.GIT_COMMIT, 'latest')
    imagePush(imageName, 'latest')
  }

  if (env.GIT_BRANCH == 'production') {
    imageTag(imageName, env.GIT_COMMIT, 'production')
    imagePush(imageName, 'production')
  }
}

def hubLogin() {
  sh "docker login -u $DOCKERHUB_USER -p $DOCKERHUB_PASSWORD"
}

return this
