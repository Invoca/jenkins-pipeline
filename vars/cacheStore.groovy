/**
 * Helper method for caching artifacts that will be shared across builds
 *
 * @param
*/

void call(String s3Bucket, ArrayList<String> cacheKeys, ArrayList<String> itemsToCache) {
  String cacheTarball = "${cacheKeys[0]}.tar.gz"

  // Verify that aws-cli is installed before proceeding
  sh 'which aws'

  echo 'Creating local cache tarball'
  sh "tar -czf ${cacheTarball} ${itemsToCache.join(' ')}"

  echo 'Pushing cache to AWS'
  cacheKeys.each { cacheKey ->
    String cacheLocation = "s3://${s3Bucket}/jenkins_cache/${env.JOB_NAME.replaceAll("\\W", "")}/${cacheKey}.tar.gz"
    sh "aws s3 cp ${cacheTarball} ${cacheLocation} --content-type application/x-gzip"
  }

  echo 'Cleaning up local cache tarball'
  sh "rm -rf ${cacheTarball}"

  echo 'Caching complete'
}
