import com.invoca.ci.DeprecationWarnings

def call(String script, Map<String, Object> githubStatusConfig) {
  def testOutput = sh(returnStdout: true, script: "${script} 2>&1")
  echo testOutput

  githubStatusConfig.script = this
  DeprecationWarnings.checkAndUpdateGithub(this, testOutput, githubStatusConfig)
}
