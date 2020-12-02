/**
 * Helper method for restoring cached artifacts
 *
 * @param
*/

import com.invoca.util.CacheUtil

void call(String s3Bucket, ArrayList<String> cacheKeys, Boolean global = false) {
  String cacheDirectory = global ? "s3://${s3Bucket}/jenkins_cache" : "s3://${s3Bucket}/jenkins_cache/${env.JOB_NAME.replaceAll("\\W", "")}";
  // Verify that aws-cli is installed before proceeding
  sh 'which aws'

  echo 'Pulling cache from AWS'
  Boolean cacheExists = false
  String cacheTarball = ""
  for (String cacheKey : cacheKeys) {
    String serializedCacheKey = CacheUtil.sanitizeCacheKey(cacheKey)
    echo "Serialized cacheKey from ${cacheKey} => ${serializedCacheKey}"

    cacheTarball = "${serializedCacheKey}.tar.gz"
    String cacheLocation = "${cacheDirectory}/${cacheTarball}"

    cacheExists = sh(script: "aws s3 ls ${cacheLocation}", returnStatus: true) == 0
    if (cacheExists) {
      try {
        echo "Found cache at key ${serializedCacheKey}"
        sh "aws s3 cp ${cacheLocation} ${cacheTarball} --content-type application/x-gzip"

        echo "Unpacking cache tarball from ${serializedCacheKey}"
        sh "tar -xzf ${cacheTarball}"

        echo "Cleaning up local cache tarball from ${serializedCacheKey}"
        sh "rm -rf ${cacheTarball}"

        echo 'Cache restored!'
        break;
      } catch(Exception ex) {
        echo "Error occurred while unpacking cache from ${serializedCacheKey}"
        echo "${ex.toString()}\n${ex.getStackTrace().join("\n")}"
        cacheExists = false
        sh "rm -rf ${cacheTarball}"
      }
    }
  }

  if (!cacheExists) {
    echo 'Unable to find cache stored for any of the provided keys'
  }
}
