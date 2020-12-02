/**
 * Helper method for caching artifacts that will be shared across builds
 *
 * @param
*/

import com.invoca.util.CacheUtil

void call(String s3Bucket, ArrayList<String> cacheKeys, ArrayList<String> itemsToCache, Boolean global = false) {
  String cacheDirectory       = global ? "s3://${s3Bucket}/jenkins_cache" : "s3://${s3Bucket}/jenkins_cache/${env.JOB_NAME.replaceAll("\\W", "")}";
  String serializedTarballKey = CacheUtil.sanitizeCacheKey(cacheKeys[0])
  String cacheTarball         = "${serializedTarballKey}.tar.gz"

  // Verify that aws-cli is installed before proceeding
  sh 'which aws'

  echo 'Creating local cache tarball'
  sh "tar -czf ${cacheTarball} ${itemsToCache.join(' ')}"

  echo 'Pushing cache to AWS'
  cacheKeys.each { cacheKey ->
    String serializedCacheKey = CacheUtil.sanitizeCacheKey(cacheKey)
    echo "Serialized cacheKey from ${cacheKey} => ${serializedCacheKey}"
    try {
      String cacheLocation = "${cacheDirectory}/${serializedCacheKey}.tar.gz"
      sh "aws s3 cp ${cacheTarball} ${cacheLocation} --content-type application/x-gzip"
    } catch(Exception ex) {
      echo "Error occurred while pushing cache to ${serializedCacheKey}"
      echo "${ex.toString()}\n${ex.getStackTrace().join("\n")}"
    }
  }

  echo 'Cleaning up local cache tarball'
  sh "rm -rf ${cacheTarball}"

  echo 'Caching complete'
}
