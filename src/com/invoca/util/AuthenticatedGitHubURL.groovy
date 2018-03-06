package com.invoca.util

import java.net.URI

class AuthenticatedGitHubURL {
  static final SCHEME = "https"
  static final HOST = "github.com"

  def static generate(owner, repository, username, password) {
    new URI(SCHEME, "${username}:${password}", HOST, -1, "/${owner}/${repository}.git", null, null).toString()
  }
}