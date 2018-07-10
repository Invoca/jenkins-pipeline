import com.invoca.github.GitHubStatus

def call(Map<String, Object> config) {
  config.script = this
  GitHubStatus.update(config)
}
