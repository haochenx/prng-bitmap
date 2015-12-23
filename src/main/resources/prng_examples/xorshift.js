/*
 * notes on JavaScript's treatments to the Number type:
 *
 * 1. there is no integer type in JavaScript, only the Number type,
 *    which is indeed an IEEE 754 double precision binary float number
 *
 * 2. '<<', '>>', '>>>' can be used as bit shift operators on Number
 *    types, but the left operant will be treated as a 32-bit integer.
 *    also note that '>>' will always preserve sign and '>>>' won't
 */

// Xorshift PRNG adopted from
// https://en.wikipedia.org/w/index.php?title=Xorshift&oldid=694749152
function xorshiftFactory() {
  // A PRNG algorithm is submitted as an object with mandatory methods
  // next(rec) and init(seed1, seed2, seed3, seed4).
  var xorshift = {
    x : 0,
    y : 0,
    z : 0,
    w : 0,

    // next(rec) is called to collect random bytes this PRNG algorithm
    // generates. this method should take a receiver object as
    // argument and call a submitting method of it at least once to
    // submit the next random bytes
    next : function (rec) {
      var r;
      with(this) {
        var t = x ^ (x << 11);
        x = y; y = z; z = w;
        r = w = w ^ (w >>> 19) ^ t ^ (t >>> 8);
      }

      // submitAsNumber(n, bytes) is currently the only method defined
      // in the receiver object. it takes the first argument as the
      // integer to extract random bytes from, using the second
      // argument to determine how many random bytes could be
      // extracted from this integer
      rec.submitAsNumber(r, 4);
    },

    // init(seed...) is called to seed this PRNG algorithm. this
    // method would be supplied with 4 seeds, each is an IEEE 754
    // double precision number ranged from 0 to 1, which means, each
    // seed would provide 52 bits of entropy. note that you don't need
    // to use all 4 seeds
    init : function (seed1, seed2, seed3, seed4) {
      with(this) {
        x = (seed1 *= 1e15) & 0x1FFF;
        y = (seed2 *= 1e15) & 0x1FFF;
        z = (seed3 *= 1e15) & 0x1FFF;
        w = (seed4 *= 1e15) & 0x1FFF;
      }
    }
  }
  return xorshift;
}

// You should call the method api.submitRng(rng) to sumbit your PRNG
// algorithm
api.submitRng(xorshiftFactory());

/*
 * The semantics of submitRng(rng) could be thought as:

function submitRng(rng) {
  var byteCount = 0;
  rng.init(Math.random(), Math.random(), Math.random(), Math.random());
  while (byteCount < NECESSARY_BYTE_COUNT) {
    rng.next({
      submitAsNumber : function (n, bytes) {
        SAVE_RANDOM_BYTES(n, bytes);
        byteCount += bytes;
      }
    })
  }
}

*/
