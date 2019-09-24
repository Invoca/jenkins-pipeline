#!/usr/bin/groovy
package com.invoca.github

import java.net.HttpURLConnection
import java.net.*
import groovy.json.JsonOutput
import hudson.AbortException

import com.invoca.github.GitHubApi

class GitHubStatus implements Serializable {
  final static String GITHUB_API_URL_TEMPLATE = "statuses/%s"

  private String context
  private String description
  private String sha
  private String targetURL
  private Script script
  private GitHubApi githubAPI

  static void update(Map config) {
    GitHubStatus.fromConfig(config).update(config.status)
  }

  static GitHubStatus fromConfig(Map config) {
    def githubApi = new GitHubApi(config.script, config.repoSlug, config.token)
    return new GitHubStatus(githubApi, config)
  }

  public GitHubStatus(GitHubApi githubAPI, Map config) {
    this.script = config.script
    this.githubAPI = githubAPI
    this.sha = config.sha
    this.context = config.context
    this.description = config.description
    this.targetURL = config.targetURL
  }

  public void update(String status) {
    log("Attempting to set GitHub status to %s='%s' for %s", context, status, sha)
    this.githubAPI.post(buildGitHubResource(), buildPayload(status))
  }

  private void log(String format, Object... args) {
    script.println(String.format(format, args))
  }

  private String buildGitHubResource() {
    String.format(GITHUB_API_URL_TEMPLATE, sha)
  }

  private String buildPayload(String status) {
    def payload = [
      state: status,
      target_url: targetURL,
      description: description,
      context: context
    ]

    JsonOutput.toJson(payload)
  }

  private void abort(String msg) {
    throw new AbortException("Failed to set GitHub status: " + msg)
  }
}
