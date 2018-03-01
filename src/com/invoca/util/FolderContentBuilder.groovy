package com.invoca.util

import com.cloudbees.hudson.plugins.folder.Folder

class FolderContentBuilder {
  def script
  def folderName
  def folder

  public FolderContentBuilder(script, folderName) {
    this.script = script
    this.folderName = folderName
  }

  @NonCPS
  public void buildInParallel() {
    def folder = findFolder()
    def jobs = [:]

    for (i = 0; i < folder.items.size(); i++) {
      def projectName = folder.items[i].name
      def jobName = jobName(projectName)
      jobs["${projectName}"] = { script.build job: jobName, propagate: true, quietPeriod: 2 }
    }

    script.parallel jobs
  }

  private Map jobMap() {
    def folder = findFolder()
    def map = [:]

    for (project in folder.items) {
      def projectName = project.name
      //map[projectName] = script.build job: jobName(projectName), propagate: true, wait: true, quietPeriod: 5
    }

    map["invocaops_lib_aws"] = { script.build job: "cookbooks/invocaops_lib_aws/master", propagate: true, wait: false, quietPeriod: 5 }
    map
  }

  private Folder findFolder() {
    for (item in Jenkins.instance.items) {
      if (item instanceof Folder && item.name == folderName) {
        return item
      }
    }

    throw new RuntimeException("Could not locate folder ${folderName}")
  }

  private String jobName(projectName) {
    "${folderName}/${projectName}/master"
  }
}
