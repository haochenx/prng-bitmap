package name.haochenxie.randombitmap;

import java.util.Iterator;

/**
 * Created by haochen on 12/23/15.
 */
public class SuperTransformer implements Iterable<Boolean> {

    private byte[] data;

    public SuperTransformer(byte[] data) {
        this.data = data;
    }

    @Override
    public Iterator<Boolean> iterator() {
        return new Iterator<Boolean>() {

            private int i = 0;
            private int ii = 0;

            @Override
            public boolean hasNext() {
                return i < data.length;
            }

            @Override
            public Boolean next() {
                boolean r = (data[i] & (1 << (8 - ii - 1))) > 0;
                ++ii;
                i += ii / 8;
                ii %= 8;
                return r;
            }

        };
    }

}
