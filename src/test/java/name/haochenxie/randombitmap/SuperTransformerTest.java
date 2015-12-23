package name.haochenxie.randombitmap;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by haochen on 12/23/15.
 */
public class SuperTransformerTest {

    @Test
    public void test() {
        int count = 0;
        for (Boolean b : new SuperTransformer(new byte[]{(byte) 0x1F, (byte) 0x53})) {
            System.out.print(b ? 1 : 0);
            ++count;

            if (count % 4 == 0) {
                System.out.print(' ');
            }
            if (count % 8 == 0) {
                System.out.print(' ');
            }
        }
        System.out.println();
        System.out.println(count);
    }

}
