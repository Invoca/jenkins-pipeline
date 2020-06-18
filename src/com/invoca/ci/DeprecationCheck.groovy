#!/usr/bin/groovy

package com.invoca.ci

import com.invoca.github.GitHubStatus

class DeprecationCheck {
  static boolean runWithDeprecationWarningCheck(String script, Map<String, Object> githubStatusConfig) {
    def testOutput = sh(returnStdout: true, script: "${script} 2>&1")
    echo testOutput

    githubStatusConfig.context = 'deprecation-warning-check'
    githubStatusConfig.script  = this

    if (testOutput.contains("Unexpected Deprecation Warnings Encountered")) {
      githubStatusConfig.status      = 'failure'
      githubStatusConfig.description = 'Unexpected deprecation warnings encountered'
    } else {
      githubStatusConfig.status      = 'success'
      githubStatusConfig.description = 'No unexpected deprecation warnings encountered'
    }

    GitHubStatus.update(githubStatusConfig)

    return githubStatusConfig.status == 'success'
  }
}
