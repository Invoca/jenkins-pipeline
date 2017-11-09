def call (String buildStatus, String channel = '#dev-jenkins') {
    // build status of null means successful
    buildStatus = buildStatus ?: 'SUCCESS'

    def color
    switch(buildStatus) {
        case 'SUCCESS':
            color = 'good'
            break
        case 'ABORTED':
            color = '#D3D3D3'
            break
        case 'FAILURE':
            color = 'danger'
            break
        default:
            color = 'warning'
            break
    }

    // SUCCESS: Job 'web/PR-7750 [42]' (https://jenkins.instance.com/job/web/job/PR-7750/42/)
    def summary = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})"

    // Send notification
    slackSend(channel: channel, color: color, message: summary)
}
