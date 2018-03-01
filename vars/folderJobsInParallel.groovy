def call(body) {
  def pipelineParams = [:]

  // body.resolveStrategy = Closure.DELEGATE_FIRST
  // body.delegate = pipelineParams
  // body()

  // if (pipelineParams.folderName == null) {
  //   throw new RuntimeError("folderName must be provided")
  // }

  def folderName = "cookbooks"
  def folderContents = new FolderContents(folderName)
  def projects = folderContents.allProjects()
  def jobs = [:]

  for (i = 0; i < projects.size(); i += 1) {
    def projectName = projects[i].name
    jobs[projectName] = { build job: "${folderName}/${projectName}/master", quietPeriod: 2 }
  }

  print jobs
}