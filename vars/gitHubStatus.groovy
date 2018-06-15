import com.invoca.github.GitHubStatus

def call(Closure body) {
  def config = [:]

  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  GitHubStatus.update(config)
}
