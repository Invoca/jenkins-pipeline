import com.invoca.github.GitHubStatus

GitHubStatus call(Map<String, Object> config) {
  config.script = this
  def github_status = new GitHubStatus(config)
  if (config.status != null) {
    github_status.update(config.status)
  }
  return github_status
}
