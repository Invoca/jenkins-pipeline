#!/usr/bin/groovy
package com.invoca.github

import java.net.HttpURLConnection
import java.net.*
import groovy.json.JsonOutput
import hudson.AbortException

class GitHubApi implements Serializable {
  final static String GITHUB_API_URL_TEMPLATE = "https://api.github.com/repos/%s/%s"

  private String repoSlug
  private String token
  private String script

  public GitHubApi(Script script, String repoSlug, String token) {
    this.script = script
    this.repoSlug = repoSlug
    this.token = token
  }

  public void post(String resource, String jsonBody) {
    log("GitHubApi POST %s to %s", jsonBody, buildGitHubURL(resource))
    
    HttpURLConnection connection = (HttpURLConnection) buildHttpConnectionForResource(resource)
    connection.setRequestMethod("POST")
    connection.setRequestProperty("Content-Type", "application/json")

    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8")
    writer.write(jsonBody)
    writer.flush()

    def responseMessage = String.format("%d %s", connection.getResponseCode(), connection.getResponseMessage())

    if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
      log("Received response: %s", responseMessage)
    } else {
      abort(responseMessage)
    }
  }

  private HttpURLConnection buildHttpConnectionForResource(String resource) {
    HttpURLConnection connection = (HttpURLConnection) buildGitHubURL(resource).openConnection()
    connection.setRequestProperty("Authorization", "token " + token)
    connection.setDoOutput(true)
    return connection
  }

  private URL buildGitHubURL(String resource) {
    new URI(String.format(GITHUB_API_URL_TEMPLATE, this.repoSlug, resource)).toURL()
  }

  private void log(String format, Object... args) {
    script.println(String.format(format, args))
  }

  private void abort(String message) {
    throw new AbortException("Failed to set GitHub status: " + message)
  }
}