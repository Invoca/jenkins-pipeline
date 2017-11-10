#!/usr/bin/groovy
package io.invoca;

/* args
    build_args  list (optional)
    dockerfile  string
    image_name  string
*/

@NonCPS
def buildCommand(Map args) {
    def String build_args = ["", *args.build_args].join(' --build-arg ')
    def String cmd
    
    // http://label-schema.org/rc1/
    cmd = "docker build -t ${args.image_name}:${env.GIT_COMMIT} \
            --label org.label-schema.schema-version=1.0 \
            --label org.label-schema.build-date=`date -u +\"%Y-%m-%dT%H:%M:%SZ\"` \
            --label org.label-schema.vcs-url=${env.GIT_URL}"
    
    if (build_args) {
        cmd += " ${build_args}"
    }

    cmd += " ${args.dockerfile}"
    return cmd
}

def imageBuild(Map args) {
    sh buildCommand(args)
}

def imagePush(String image_name, String tag) {
    sh "docker push ${image_name}:${tag}"
}

def imageRemove(String image_name, String tag) {
    sh "docker rmi ${image_name}:${tag}"
}

def imageTag(String image_name, String current_tag, String new_tag) {
    sh "docker tag ${image_name}:${current_tag} ${image_name}:${new_tag}"
}

def imageTagPush(String image_name) {
    imagePush(image_name, env.GIT_COMMIT)
    
    if (env.GIT_BRANCH == 'master') {
        imageTag(image_name, env.GIT_COMMIT, 'latest')
        imagePush(image_name, 'latest')
    }

    if (env.GIT_BRANCH == 'production') {
        imageTag(image_name, env.GIT_COMMIT, 'production')
        imagePush(image_name, 'production')
    }
}

return this
