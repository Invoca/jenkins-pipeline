#!/usr/bin/groovy

package com.invoca.ci

import com.invoca.github.GitHubStatus

class DeprecationWarnings {
  static void checkAndUpdateGithub(Script script, String testOutput, Map<String, Object> githubStatusConfig) {
    (new DeprecationWarnings(script, testOutput, githubStatusConfig)).checkAndUpdateGithub()
  }

  public static String GITHUB_STATUS_CONTEXT         = 'deprecation-warning-check'
  public static String GITHUB_STATUS_FAILURE_MESSAGE = 'Unexpected deprecation warnings encountered'
  public static String GITHUB_STATUS_SUCCESS_MESSAGE = 'No unexpected deprecation warnings encountered'
  public static String WARNINGS_ARCHIVE_FILE_NAME    = 'deprecation_warnings.txt'
  public static String DEPRECATION_WARNING_PREFIX    = 'DEPRECATION WARNING'
  public static String UNEXPECTED_DEPRECATIONS_START = 'Unexpected Deprecation Warnings Encountered'
  public static String UNEXPECTED_DEPRECATIONS_END   = '====='

  private Script script
  private String testOutput
  private Map<String, Object> githubStatusConfig

  public DeprecationWarnings(Script script, String testOutput, Map<String, Object> githubStatusConfig) {
    this.script             = script
    this.testOutput         = testOutput
    this.githubStatusConfig = githubStatusConfig
  }

  public void checkAndUpdateGithub() {
    Map<String, Object> githubStatusConfig = this.githubStatusConfig

    githubStatusConfig.context = GITHUB_STATUS_CONTEXT

    if (this.warningsExist()) {
      githubStatusConfig.targetURL   = this.archiveWarnings()
      githubStatusConfig.status      = 'failure'
      githubStatusConfig.description = GITHUB_STATUS_FAILURE_MESSAGE
    } else {
      githubStatusConfig.targetURL   = this.script.env.RUN_DISPLAY_URL
      githubStatusConfig.status      = 'success'
      githubStatusConfig.description = GITHUB_STATUS_SUCCESS_MESSAGE
    }

    GitHubStatus.update(githubStatusConfig)
  }

  public boolean warningsExist() {
    return this.testOutput.contains(UNEXPECTED_DEPRECATIONS_START) || this.testOutput.contains(DEPRECATION_WARNING_PREFIX)
  }

  private String extractWarnings() {
    List<String> warnings = new ArrayList<String>();
    boolean withinUnexpectedDeprecationOutput = false

    this.testOutput.split("\n").each {
      if (it == UNEXPECTED_DEPRECATIONS_START) {
        warnings.add("=====\n" + UNEXPECTED_DEPRECATIONS_START)
        withinUnexpectedDeprecationOutput = true
      } else if (withinUnexpectedDeprecationOutput && it == UNEXPECTED_DEPRECATIONS_END) {
        warnings.add(it)
        withinUnexpectedDeprecationOutput = false
      } else if (withinUnexpectedDeprecationOutput || it.contains(DEPRECATION_WARNING_PREFIX)) {
        warnings.add(it)
      }
    }

    return warnings.join("\n")
  }

  // Archives the extracted warnings and returns the URL for the artifact
  private String archiveWarnings() {
    this.script.writeFile(file: WARNINGS_ARCHIVE_FILE_NAME, text: this.extractWarnings())
    this.script.archiveArtifacts(WARNINGS_ARCHIVE_FILE_NAME)

    return this.script.env.BUILD_URL + "/artifact/" + WARNINGS_ARCHIVE_FILE_NAME
  }
}
