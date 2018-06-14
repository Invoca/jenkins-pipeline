#!/usr/bin/groovy
package com.invoca.github

import java.net.HttpURLConnection
import java.net.URI

class GitHubStatus implements Serializable {
  final static String GITHUB_API_URL_TEMPLATE = "https://api.github.com/repos/%s/statuses/%s"

  private String repoSlug
  private String sha
  private String targetURL
  private String description
  private String context

  GitHubStatus(Map config) {
    this.repoSlug = config.repoSlug
    this.sha = config.sha
    this.targetURL = config.targetURL
    this.description = config.description
    this.context = config.context
    this.status = config.status
  }

  boolean update {
    HttpURLConnection connection = (HttpURLConnection) URL().openConnection()
    connection.setRequestMethod("POST")
    connection.setRequestProperty("Content-Type", "application/json")
    connection.getOutputStream().write(payload())

    connection.getResponseCode() == HttpURLConnection.HTTP_CREATED
  }

  private String URL() {
    new URL(String.format(GITHUB_API_URL_TEMPLATE, repoSlug, sha))
  }

  private String payload(String state) {
    payload = [
      state: status,
      target_url: targetURL,
      description: description,
      context: context
    ]

    new JSONObject(payload).toString()
  }
}