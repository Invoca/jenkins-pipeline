/**
 * Helper method for generating s3 cache keys
 *
 * @param cacheKey String
 * @param exhaustive Boolean
*/

ArrayList<String> call(String cacheKey, Boolean exhaustive = true) {
  if (exhaustive) {
    return [
      "bundle-${gemfileLockChecksum()}",
      "bundle-${cacheKey}",
      "bundle-master"
    ].unique(false);
  }

  return [
    "bundle-${gemfileLockChecksum()}",
    "bundle-${cacheKey}"
  ];
}
