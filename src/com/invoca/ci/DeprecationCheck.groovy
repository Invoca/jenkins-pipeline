#!/usr/bin/groovy

package com.invoca.ci

import com.invoca.github.GitHubStatus

def deprecationCheck(String testOutput, Map<String, Object> githubStatusConfig) {
  if (testOutput.contains("Unexpected Deprecation Warnings Encountered")) {
    githubStatusConfig.status = 'failure'
  } else {
    githubStatusConfig.status = 'success'
  }

  githubStatusConfig.script = this
  GitHubStatus.update(githubStatusConfig)

  return githubStatusConfig.status == 'success'
}
