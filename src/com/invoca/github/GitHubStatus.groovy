#!/usr/bin/groovy
package com.invoca.github

import java.net.HttpURLConnection
import java.net.*
import groovy.json.JsonOutput

class GitHubStatus implements Serializable {
  final static String GITHUB_API_URL_TEMPLATE = "https://api.github.com/repos/%s/statuses/%s"

  private String context
  private String description
  private String repoSlug
  private String sha
  private String status
  private String targetURL
  private String token

  boolean update() {
    HttpURLConnection connection = (HttpURLConnection) gitHubURL().openConnection()
    connection.setRequestMethod("POST")
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Authorization", "token " + token)
    connection.setDoOutput(true)

    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8")
    writer.write(payload())

    connection.getResponseCode() == HttpURLConnection.HTTP_CREATED
  }

  private URL gitHubURL() {
    new URI(String.format(GITHUB_API_URL_TEMPLATE, repoSlug, sha)).toURL()
  }

  private String payload(String state) {
    def payload = [
      state: status,
      target_url: targetURL,
      description: description,
      context: context
    ]

    JsonOutput.toJson(payload)
  }
}