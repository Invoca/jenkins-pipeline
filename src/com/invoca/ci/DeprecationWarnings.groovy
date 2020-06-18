#!/usr/bin/groovy

package com.invoca.ci

import com.invoca.github.GitHubStatus

class DeprecationWarnings {
  static void checkAndUpdateGithub(String testOutput, Map<String, Object> githubStatusConfig) {
    githubStatusConfig.context = 'deprecation-warning-check'

    if (check(testOutput)) {
      githubStatusConfig.status      = 'failure'
      githubStatusConfig.description = 'Unexpected deprecation warnings encountered'
    } else {
      githubStatusConfig.status      = 'success'
      githubStatusConfig.description = 'No unexpected deprecation warnings encountered'
    }

    GitHubStatus.update(githubStatusConfig)
  }

  static boolean check(String testOutput) {
    return testOutput.contains("Unexpected Deprecation Warnings Encountered") || testOutput.contains("DEPRECATION WARNING")
  }
}
