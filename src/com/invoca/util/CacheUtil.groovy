#!/usr/bin/groovy
package com.invoca.util

class CacheUtil {
  static String sanitizeCacheKey(String cacheKey) {
    return cacheKey.replaceAll("\\W", "-");
  }
}
