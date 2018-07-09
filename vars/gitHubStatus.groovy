import com.invoca.github.GitHubStatus

def call(Map<String, Object> config) {
  def github = new GitHubStatus(this, config.repoSlug, config.sha, config.targetURL, config.token)
  github.setStatus(config.context, config.status, config.description)
}
