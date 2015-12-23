package name.haochenxie.randombitmap;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import org.apache.commons.io.HexDump;
import org.apache.commons.io.IOUtils;

import javax.script.*;
import java.io.InputStream;
import java.util.Random;

/**
 * Created by haochen on 12/23/15.
 */
public class JavaScriptPRNGEngine {

    private String code;

    public JavaScriptPRNGEngine(String code) {
        this.code = code;
    }

    public static class Receiver {

        public int byteCount = 0;

        public byte[] buff;

        public Receiver(int buffSize) {
            buff = new byte[buffSize];
        }

        public void submitAsNumber(int n, int byteCount) {
            for (int i = 0; i < byteCount; i++) {
                if (this.byteCount >= buff.length) {
                    break;
                }

                buff[this.byteCount++] = (byte) ((n >> ((byteCount - i - 1) * 8)) & 0xFF);
            }
        }

    }

    public static class JSApi {

        private Object rng;

        public void submitRng(Object rng) {
            this.rng = rng;
        }

        public Object getRng() {
            return rng;
        }
    }

    public byte[] getRandomBytes(int byteCount, double[] seeds) throws ScriptException, NoSuchMethodException {
        Preconditions.checkArgument(seeds.length == 4);

        JSApi api = new JSApi();
        Receiver receiver = new Receiver(byteCount);

        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine engine = scriptEngineManager.getEngineByName("JavaScript");

        engine.put("api", api);
        engine.eval(code);

        Invocable inv = (Invocable) engine;
        inv.invokeMethod(api.getRng(), "init", seeds[0], seeds[1], seeds[2], seeds[3]);

        while (receiver.byteCount < byteCount) {
            inv.invokeMethod(api.getRng(), "next", receiver);
        }

        return receiver.buff;
    }

    public static void main(String[] args) throws Exception {
        Random rng = new Random();

        InputStream stream = JavaScriptPRNGEngine.class.getResource("/prng_examples/xorshift.js").openStream();
        String code = IOUtils.toString(stream);
        JavaScriptPRNGEngine engine = new JavaScriptPRNGEngine(code);
        byte[] bytes = engine.getRandomBytes(100,
                new double[]{ 0.02630154852392408, 0.025206450209828812, 0.23265174898179253, 0.6705782168334786 });

        HexDump.dump(bytes, 0, System.out, 0);
    }

}
