package com.invoca.util

import com.cloudbees.hudson.plugins.folder.Folder

class FolderContentBuilder {
  def script
  def folderName

  public FolderContentBuilder(script, folderName) {
    this.script = script
    this.folderName = folderName
  }

  public void buildAll() {
    def folder = findFolder()
    for (project in folder.items) {
      script.build job: project.name, wait: false
    }
  }

  private Folder findFolder() {
    for (folder in Jenkins.instance.items) {
      if (folder.name == this.folderName) {
        return folder
      }
    }

    throw new RuntimeException("Could not locate folder ${folderName}")
  }
}
