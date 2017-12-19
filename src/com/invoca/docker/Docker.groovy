package com.invoca.docker

def hubLogin(String username, String password) {
  sh "docker login -u ${username} -p ${password}"
}
