import com.invoca.github.GitHubStatus

def call(Closure body) {
  def config = [script: this]

  body.delegate = config
  body()

  GitHubStatus.update(config)
}
