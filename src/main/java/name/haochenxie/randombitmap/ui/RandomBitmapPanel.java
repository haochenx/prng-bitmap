package name.haochenxie.randombitmap.ui;

import com.google.common.base.Preconditions;
import name.haochenxie.randombitmap.SuperTransformer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by haochen on 12/23/15.
 */
public class RandomBitmapPanel extends JPanel {

    private static final int PREFERRED_PADDING = 20;

    private int bitMapWidth;
    private int bitMapHeight;
    private int scale;

    private BitmapType type;

    private byte[] data;

    public RandomBitmapPanel(int bitMapWidth, int bitMapHeight, int scale, BitmapType type) {
        this.bitMapWidth = bitMapWidth;
        this.bitMapHeight = bitMapHeight;
        this.scale = scale;
        this.type = type;
    }

    // TODO improve performance, probably by caching
    @Override
    public void paintComponent(Graphics g0) {
        super.paintComponent(g0);

        Graphics g = g0.create();

        Rectangle bounds = getBounds();
        int w = bitMapWidth * scale;
        int h = bitMapHeight * scale;

        g.translate((bounds.width - w) / 2, (bounds.height - h) / 2);

        g.setColor(Color.WHITE);
        g.fillRect(-PREFERRED_PADDING / 2, -PREFERRED_PADDING / 2, w + PREFERRED_PADDING, h + PREFERRED_PADDING);

        if (isDataValid()) {
            switch (type) {
                case BINARY:
                    paintBinaryBitmap(g);
                    break;
                case RGB:
                    paintRGBBitmap(g);
                    break;
                case GRAY_SCALE:
                    paintGrayScaleBitmap(g);
                    break;
                default:
                    paintNA(g);
            }
        } else {
            paintNA(g);
        }
    }

    private void paintGrayScaleBitmap(Graphics g0) {
        Graphics g = g0.create();

        for (int i = 0; i < bitMapWidth * bitMapHeight; i++) {
            int lightness = (int) data[i] & 0xFF;
            g.setColor(new Color(lightness, lightness, lightness));

            int x = i % bitMapWidth;
            int y = i / bitMapWidth;
            g.fillRect(x * scale, y * scale, scale, scale);
        }
    }

    private void paintRGBBitmap(Graphics g0) {
        Graphics g = g0.create();

        for (int i = 0; i < bitMapWidth * bitMapHeight; i++) {
            g.setColor(new Color(
                    (int) data[i * 3 + 0] & 0xFF,
                    (int) data[i * 3 + 1] & 0xFF,
                    (int) data[i * 3 + 2] & 0xFF));

            int x = i % bitMapWidth;
            int y = i / bitMapWidth;
            g.fillRect(x * scale, y * scale, scale, scale);
        }
    }

    private void paintBinaryBitmap(Graphics g0) {
        Graphics g = g0.create();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, bitMapWidth * scale, bitMapHeight * scale);

        int count = 0;
        g.setColor(Color.BLACK);
        for (Boolean b : new SuperTransformer(data)) {
            if (count >= bitMapWidth * bitMapHeight) {
                break;
            }

            if (b) {
                int x = count % bitMapWidth;
                int y = count / bitMapWidth;
                g.fillRect(x * scale, y * scale, scale, scale);
            } else {
                // no-op
            }

            ++count;
        }
    }

    public BufferedImage createImage() throws IOException {
        if (isDataValid()) {
            BufferedImage buff = new BufferedImage(bitMapWidth * scale, bitMapHeight * scale, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = buff.createGraphics();
            paintBinaryBitmap(g);
            return buff;
        } else {
            throw new IOException("Supplied data is not sufficient to generate the requested bitmap");
        }
    }

    private boolean isDataValid() {
        return data != null && data.length >= getRequiredBytes();
    }

    private void paintNA(Graphics g0) {
        Graphics g = g0.create();
        int w = bitMapWidth * scale;
        int h = bitMapHeight * scale;

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, w, h);
        g.drawLine(0, 0, w, h);
        g.drawLine(w, 0, 0, h);
    }

    public int getRequiredBytes() {
        switch (type) {
            case BINARY:
                return (bitMapWidth * bitMapHeight + 7) / 8;
            case RGB:
                return (bitMapWidth * bitMapHeight) * 3;
            case GRAY_SCALE:
                return bitMapWidth * bitMapHeight;
            default:
                throw new IllegalArgumentException("wait, what? i don't know the type you passed in");
        }
    }

    public void setData(byte[] data) {
        Preconditions.checkArgument(data.length >= getRequiredBytes(), "data size isn't big enough");
        this.data = data.clone();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(bitMapWidth * scale + 2 * PREFERRED_PADDING, bitMapHeight * scale + 2 * PREFERRED_PADDING);
    }

    public void setSizeScale(int bitMapWidth, int bitMapHeight, int scale) {
        this.bitMapWidth = bitMapWidth;
        this.bitMapHeight = bitMapHeight;
        this.scale = scale;
        revalidate();
    }

    public BitmapType getType() {
        return type;
    }

    public void setType(BitmapType type) {
        this.type = type;
        repaint();
    }
}
