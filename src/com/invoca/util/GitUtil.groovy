#!/usr/bin/groovy
package com.invoca.util

class GitUtil {
  private Script script

  public GitUtil(Script script) {
    this.script = script
  }

  def getModifiedFiles(String currentCommit, String previousCommit) {
    this.script.sh("git diff --name-only ${previousCommit} ${currentCommit}").split("\n")
  }
}
