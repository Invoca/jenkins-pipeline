#!/usr/bin/groovy
package com.invoca.docker

class Image implements Serializable {
  public static String LABEL_SCHEMA_VERSION = "org.label-schema.schema-version"
  public static String LABEL_BUILD_DATE = "org.label-schema.build-date"
  public static String LABEL_VCS_URL = "org.label-schema.vcs-url"

  private String imageName
  private String[] tags
  private String baseDir
  private Script script
  private String[] imageNameWithTags
  private String[] labels

  public Image(Script script, String imageName, String tag, String baseDir = ".") {
    this(script, imageName, [tag], baseDir)
  }

  public Image(Script script, String imageName, ArrayList tags, String baseDir = ".") {
    this(script, imageName, tags as String[], baseDir)
  }

  public Image(Script script, String imageName, String[] tags, String baseDir = ".") {
    this.script = script
    this.imageName = imageName
    this.tags = tags
    this.baseDir = baseDir
  }

  public Image build(Map args) {
    this.imageNameWithTags = this.buildImageNameWithTags()

    def gitUrl = args.gitUrl
    def buildArgs = args.buildArgs ?: [:]
    def dockerFile = args.dockerFile ?: "Dockerfile"
    def target = args.target ?: null

    sh buildCommand(gitUrl, buildArgs, "${this.baseDir}/${dockerFile}", target)
    this
  }

  public Image tag() {
    this.imageNameWithTags = this.buildImageNameWithTags()

    if (this.imageNameWithTags.size() > 1) {
      for (int i = 1; i < this.imageNameWithTags.size(); i++) {
        sh tagCommand(this.imageNameWithTags[i])
      }
    }
    this
  }

  public Image push() {
    this.imageNameWithTags.each { sh pushCommand(it) }
    this
  }

  public Image remove() {
    this.imageNameWithTags.each { sh removeCommand(it) }
    this
  }

  private void sh(String command) {
    this.script.sh(command)
  }

  private void sh(Map args) {
    this.script.sh(args)
  }

  private String buildCommand(String gitUrl, Map buildArgs, String dockerFile, String target) {
    def buildArgList = buildArgs.collect { k, v -> "--build-arg ${k}=\"${v}\"" }
    def command = [
      "docker",
      "build",
      "--pull",
      "-t ${this.imageName}:${baseTag()}",
      "-f ${dockerFile}"
    ] + getLabels(gitUrl) + buildArgList

    if (target) {
      command.add("--target ${target}")
    }

    command.add(this.baseDir)

    return command.join(" ")
  }

  private String tagCommand(String imageNameWithTag) {
    "docker tag ${this.imageName}:${baseTag()} ${imageNameWithTag}"
  }

  private String pushCommand(String imageNameWithTag) {
    "docker push ${imageNameWithTag}"
  }

  private String removeCommand(String imageNameWithTag) {
    "docker rmi ${imageNameWithTag}"
  }

  private String baseTag() {
    sanitizeTag(this.tags[0])
  }

  private String sanitizeTag(String tag) {
    tag.replaceAll("[/:]", "_")
  }

  private String[] buildImageNameWithTags() {
    this.tags.collect { "${this.imageName}:${sanitizeTag(it)}" }
  }

  private String[] getLabels(String gitUrl) {
    [
      (LABEL_SCHEMA_VERSION): "1.0",
      (LABEL_BUILD_DATE): currentDate(),
      (LABEL_VCS_URL): gitUrl
    ].collect { k, v -> "--label ${k}=${v}" }
  }

  private String currentDate() {
    sh(script: 'date -u +"%Y-%m-%dT%H:%M:%SZ"', returnStdout: true).toString().trim()
  }
}
