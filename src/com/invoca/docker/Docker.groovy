package com.invoca.docker

def hubLogin(String username, String password) {
  sh "docker login -u ${username} -p ${password}"
}

def imageExistsLocally(String imageWithTag) {
  return sh(script: "docker image inspect ${imageWithTag}", returnStatus: true) == 0
}

def downloadImage(String imageWithTag) {
  return sh(script: "docker pull ${imageWithTag}", returnStatus: true) == 0
}

def getImage(String imageWithTag) {
  // Try downloading image from remote if not found locally.
  // Return true if image is now present locally, false otherwise.
  return imageExistsLocally(imageWithTag) || downloadImage(imageWithTag)
}