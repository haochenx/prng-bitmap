// to use the Java SecureRandom implementation:
api.submitRng(api.createJavaSecureRandomRNG());

// You can also specify the algorithm like
//
//     api.submitRng(api.createJavaSecureRandomRNG("NativePRNG"));
//
// the default algorithm is "SHA1PRNG", which is a standard algorithm
// to Java's SecureRandom that every Java installation should be
// providing.
//
// Different installation of Java may have different list of available
// PRNG algorithms. The list of provided PRNG algorithms of the Oracle
// Java runtime could be found here:
// https://docs.oracle.com/javase/8/docs/technotes/guides/security/SunProviders.html
