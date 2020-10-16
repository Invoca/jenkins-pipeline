/**
 * Helper method for restoring cached artifacts
 *
 * @param
*/

void call(String s3Bucket, ArrayList<String> cacheKeys) {
  // Verify that aws-cli is installed before proceeding
  sh 'which aws'

  echo 'Pulling cache from AWS'
  Boolean cacheExists = false
  String cacheTarball = ""
  for (String cacheKey : cacheKeys) {
    cacheTarball = "${cacheKey}.tar.gz"
    String cacheLocation = "s3://${s3Bucket}/jenkins_cache/${env.JOB_NAME.replaceAll("\\W", "")}/${cacheTarball}"

    cacheExists = sh(script: "aws s3 ls ${cacheLocation}", returnStatus: true) == 0
    if (cacheExists) {
      sh "aws s3 cp ${cacheLocation} ${cacheTarball} --content-type application/x-gzip"
      break;
    }
  }

  if (cacheExists) {
    echo 'Unpacking cache tarball'
    sh "tar -xzvf ${cacheTarball} ${itemsToCache.join(' ')}"

    echo 'Cleaning up local cache tarball'
    sh "rm -rf ${cacheTarball}"

    echo 'Cache restored'
  } else {
    echo 'Unable to find cache stored for any of the provided keys'
  }
}
