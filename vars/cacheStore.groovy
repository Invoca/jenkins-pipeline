/**
 * Helper method for caching artifacts that will be shared across builds
 *
 * @param
*/

void call(String s3Bucket, String cacheKey, ArrayList<String> itemsToCache) {
  String cacheTarball = "${cacheKey}.tar.gz"
  String cacheLocation = "s3://${s3Bucket}/jenkins_cache/${env.JOB_NAME.replaceAll("\\W", "")}/${cacheTarball}"

  // Verify that aws-cli is installed before proceeding
  sh 'which aws'

  echo 'Creating local cache tarball'
  sh "tar -czvf ${cacheTarball} ${itemsToCache.join(' ')}"

  echo 'Pushing cache to AWS'
  sh "aws s3 cp ${cacheTarball} ${cacheLocation} --content-type application/x-gzip"

  echo 'Cleaning up local cache tarball'
  sh "rm -rf ${cacheTarball}"

  echo 'Caching complete'
}
