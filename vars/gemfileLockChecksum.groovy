/**
 * Helper method for a checksum against the contents of the Gemfile.lock
 *
 * @param
*/

String call() {
  return sh(script: "sha1sum -b Gemfile.lock | cut -d ' ' -f 1", returnStdout: true).trim();
}