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
  // Check if image is present locally, or try downloading it from remote.
  // Return true if image is now present locally, false otherwise.
  return imageExistsLocally(imageWithTag) || downloadImage(imageWithTag)
}
