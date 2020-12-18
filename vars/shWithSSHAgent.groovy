/**
 * Helper method for running a sh command after having added an SSH agent.
 * Requires a valid GITHUB_KEY environment variable.
 *
 * @param command The sh command to run
*/

void call(command) {
  sh """
    # Get SSH setup inside the container
    eval `ssh-agent -s`
    echo "$GITHUB_KEY" | ssh-add -
    mkdir -p /root/.ssh
    ssh-keyscan -t rsa github.com > /root/.ssh/known_hosts
    $command
  """
}