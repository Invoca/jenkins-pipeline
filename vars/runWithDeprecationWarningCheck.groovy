import com.invoca.ci.DeprecationWarnings

def call(String script, Map<String, Object> githubStatusConfig) {
  def testOutput = sh(returnStdout: true, script: "${script} 2>&1")
  echo testOutput

  writeFile file: 'deprecation_warnings.txt', text: testOutput
  archiveArtifacts 'deprecation_warnings.txt'

  githubStatusConfig.script = this
  DeprecationWarnings.checkAndUpdateGithub(testOutput, githubStatusConfig)
}
