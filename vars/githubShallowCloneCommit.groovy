/**
 * Helper method for doing a quick and clean clone of a single commit on GitHub
 *
 * @param repo String The repository to pull the commit from (ex. Invoca/web)
 * @param commit String The sha or branch to pull down from GitHub
 * @param githubToken String The GitHub Access Token to use for authenticating with GitHub
 */

void call(String repo, String commit, String githubToken) {
  echo "Checking out ${commit}"
  sh "git init"
  sh "git remote add origin https://${githubToken}:x-oauth-basic@github.com/${repo}"
  sh "git fetch --depth 1 origin ${commit}"
  sh "git checkout ${commit}"
  sh "git rev-parse HEAD"
}
