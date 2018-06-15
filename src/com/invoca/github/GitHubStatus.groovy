#!/usr/bin/groovy
package com.invoca.github

import java.net.HttpURLConnection
import java.net.*
import groovy.json.JsonOutput
import java.util.logging.Logger

class GitHubStatus implements Serializable {
  final static String GITHUB_API_URL_TEMPLATE = "https://api.github.com/repos/%s/statuses/%s"
  final static String LOGGER_NAMESPACE = "com.invoca.github.githubstatus"

  private String context
  private String description
  private String repoSlug
  private String sha
  private String status
  private String targetURL
  private String token
  private Script script

  static void update(Map config) {
    new GitHubStatus(config).update()
  }

  void update() {
    log("Attempting to set GitHub status to %s for %s/%s", status, repoSlug, sha)

    HttpURLConnection connection = (HttpURLConnection) buildGitHubURL().openConnection()
    connection.setRequestMethod("POST")
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Authorization", "token " + token)
    connection.setDoOutput(true)

    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8")
    writer.write(getPayload())
    writer.flush()

    log("Received response: %d %s", connection.getResponseCode(), connection.getResponseMessage())
  }

  private void log(String format, Object... args) {
    script.println(String.format(format, args))
  }

  private URL buildGitHubURL() {
    new URI(String.format(GITHUB_API_URL_TEMPLATE, repoSlug, sha)).toURL()
  }

  private String getPayload(String state) {
    def payload = [
      state: status,
      target_url: targetURL,
      description: description,
      context: context
    ]

    JsonOutput.toJson(payload)
  }
}
