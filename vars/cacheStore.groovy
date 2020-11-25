/**
 * Helper method for caching artifacts that will be shared across builds
 *
 * @param
*/

void call(String s3Bucket, ArrayList<String> cacheKeys, ArrayList<String> itemsToCache, Boolean global = false) {
  String cacheDirectory = global ? "s3://${s3Bucket}/jenkins_cache" : "s3://${s3Bucket}/jenkins_cache/${env.JOB_NAME.replaceAll("\\W", "")}";
  String cacheTarball   = "${cacheKeys[0]}.tar.gz"

  // Verify that aws-cli is installed before proceeding
  sh 'which aws'

  echo 'Creating local cache tarball'
  sh "tar -czf ${cacheTarball} ${itemsToCache.join(' ')}"

  echo 'Pushing cache to AWS'
  cacheKeys.each { cacheKey ->
    try {
      String cacheLocation = "${cacheDirectory}/${cacheKey}.tar.gz"
      sh "aws s3 cp ${cacheTarball} ${cacheLocation} --content-type application/x-gzip"
    } catch(Exception ex) {
      echo "Error occurred while pushing cache to ${cacheKey}"
      echo "${ex.toString()}\n${ex.getStackTrace().join("\n")}"
    }
  }

  echo 'Cleaning up local cache tarball'
  sh "rm -rf ${cacheTarball}"

  echo 'Caching complete'
}
