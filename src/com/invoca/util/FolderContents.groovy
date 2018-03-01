package com.invoca.util

import com.cloudbees.hudson.plugins.folder.Folder
import hudson.model.AbstractProject

class FolderContents {
  def folderName

  FolderContents(folderName) {
    this.folderName = folderName
  }

  def getProjectNames() {
    getFolder().items.collect { it.name }
  }

  def getFolder() {
    for (item in Jenkins.instance.items) {
      if (item instanceof Folder && item.name == folderName) {
        return item
      }
    }

    throw new RuntimeException("Could not locate folder ${folderName}")
  }
}