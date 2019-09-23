#!/usr/bin/groovy
package com.invoca.util

class FileUtil {
  private Script script

  public FileUtil(Script script) {
    this.script = script
  }

  def sha256sum(ArrayList filePaths) {
    return this.script.sh(script: "cat ${filePaths.join(" ")} | sha256sum -b | cut -d ' ' -f 1", returnStdout: true).trim()
  }
}
