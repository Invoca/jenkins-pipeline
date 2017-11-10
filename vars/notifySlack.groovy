@NonCPS
def call (String buildStatus, String message = "", String channel = "#dev-jenkins") {
    // build status of null means ongoing
    buildStatus = buildStatus ?: 'ONGOING'

    def String color
    switch(buildStatus) {
        case 'ABORTED':
            color = '#D3D3D3'
            break
        case 'FAILURE':
            color = 'danger'
            break
        case 'ONGOING':
            color = '#D3D3D3'
            break
        case 'SUCCESS':
            color = 'good'
            break
        case 'UNSTABLE':
            color = 'warning',
            break
        default:
            color = 'warning'
            break
    }

    def String msg
    if (message) {
        msg = message
    } else {
        // SUCCESS: Job 'web/PR-7750 [42]' (https://jenkins.instance.com/job/web/job/PR-7750/42/)
        msg = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})"
    }

    // Send notification
    slackSend(channel: channel, color: color, message: msg)
}
