/**
 * Helper method for generating s3 cache keys
 *
 * @param cacheKey String
 * @param exhaustive Boolean
*/

ArrayList<String> call(String cacheKey, Boolean exhaustive = true) {
  if (exhaustive) {
    return [
      "assets-${cacheKey}",
      "assets-master",
      "assets-production"
    ].unique(false);
  }

  return ["assets-${cacheKey}"];
}
