#!/usr/bin/groovy
package io.invoca;

/* args
    build_args  list (optional)
    dockerfile  string
    image       string
    org         string
*/

def buildCommand(Map args) {
    def String build_args
    def String cmd
    def String image_name = imageName(args.org, args.image)
    
    cmd = "docker build -t ${image_name}:${env.GIT_COMMIT} \
            --label org.label-schema.schema-version=1.0 \
            --label org.label-schema.build-date=`date -u +\"%Y-%m-%dT%H:%M:%SZ\"` \
            --label org.label-schema.vcs-url=${env.GIT_URL}"
    
    for (ba in args.build_args) {
        build_args += "--build-arg ${ba} "
    }
    if (build_args) {
        cmd += " ${build_args}"
    }

    cmd += " ${args.dockerfile}"
    return cmd
}

def containerBuildPush(Map args) {
    def String image_name = imageName(args.org, args.image)

    println "Docker Build: ${image_name}:${env.GIT_COMMIT}"
    sh buildCommand(args)
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

def imageName(String org, String image) {
    return "${org}/${image}"
}

def imagePush(String image_name, String tag) {
    println "Docker Push: ${image_name}:${tag}"
    sh "docker push ${image_name}:${tag}"
}

def imageTag(String image_name, String current_tag, String new_tag) {
    println "Docker Tag: ${image_name}:${current_tag} --> ${new_tag}"
    sh "docker tag ${image_name}:${current_tag} ${image_name}:${new_tag}"
}

return this
