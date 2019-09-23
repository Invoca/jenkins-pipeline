package com.invoca.docker

def hubLogin(String username, String password) {
  sh "docker login -u ${username} -p ${password}"
}

def imageExistsLocally(String imageWithTag) {
  // Will return empty string if no matching image is found
  return sh(script: "docker images -q ${imageWithTag}", returnStdout: true).trim() != ""
}

def downloadImage(String imageWithTag) {
  return sh(script: "docker pull ${imageWithTag}", returnStatus: true) == 0
}

def getImage(String imageWithTag) {
  // Try downloading image from remote if not found locally.
  // Return true if image is now present locally, false otherwise.
  if (!imageExistsLocally(imageWithTag)) {
    downloadImage(imageWithTag)
  }
  return imageExistsLocally(imageWithTag)
}