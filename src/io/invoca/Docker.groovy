#!/usr/bin/groovy
package io.invoca;

/* args
    dockerfile  string
    image_name  string
*/

def buildCommand(Map args, List build_args) {
    println "buildCommand: args = ${args}"

    def String build_args_str = ""
    def String cmd
    
    // http://label-schema.org/rc1/
    cmd = "docker build -t ${args.image_name}:${env.GIT_COMMIT} \
            --label org.label-schema.schema-version=1.0 \
            --label org.label-schema.build-date=`date -u +\"%Y-%m-%dT%H:%M:%SZ\"` \
            --label org.label-schema.vcs-url=${env.GIT_URL}"
    
    for (ba in build_args) {
        build_args_str += "--build-arg ${ba} "
    }
    if (build_args_str) {
        cmd += " ${build_args_str}"
    }

    cmd += " ${args.dockerfile}"
    return cmd
}

def imageBuild(Map args, List build_args = []) {
    sh buildCommand(args, build_args)
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
