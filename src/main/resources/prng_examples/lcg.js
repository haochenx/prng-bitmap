function lcgFactory() {
  var lcg = {
    x : 0,
    next : function (rec) {
      with(this) {
        x = (1103515245 * x + 12345) % 2147483648;
        rec.submitAsNumber(x, 4);
      }
    },
    init : function (seed) {
      this.x = seed * 1e15;
    }
  };
  return lcg;
}

api.submitRng(lcgFactory());
