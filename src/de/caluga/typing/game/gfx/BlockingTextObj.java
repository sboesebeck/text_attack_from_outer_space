package de.caluga.typing.game.gfx;

import java.awt.*;

public class BlockingTextObj implements Obj {

    private final double startSize;
    private int y2;
    private int x2;
    private String text;
    private double targetSize = 80;

    private double red = 255;
    private double green = 50;
    private double blue = 50;
    private int frame = 0;
    private int x;
    private int y;
    private int duration = 100; //cycles
    private int fadeOut = 10; //cycles
    private int fadeoutFrame = 0;
    private int v = 1;

    private boolean finished = false;

    public BlockingTextObj(String text, int x, int y, int r, int g, int b, int dur) {
        this(text, x, y, x, y, r, g, b, 15, 80, dur, 10);
    }

    public BlockingTextObj(String text, int x, int y, int x2, int y2, int r, int g, int b, int startSize, int targetSize, int dur, int fadeOut) {
        this.text = text;
        this.x = x;
        this.x2 = x2;
        this.y = y;
        this.y2 = y2;
        this.red = r;
        this.green = g;
        this.blue = b;
        this.duration = dur;
        this.targetSize = targetSize;
        this.startSize = startSize;
        this.fadeOut = fadeOut;

    }

    @Override
    public void draw(Graphics2D g) {
        if (finished) {
            //fadeout
            fadeoutFrame++;
            Color c = new Color((int) (red - (red / fadeOut) * fadeoutFrame), (int) (green - (green / fadeOut) * fadeoutFrame), (int) (blue - (blue / fadeOut) * fadeoutFrame));
            g.setColor(c);
            g.setFont(new Font("Arial", Font.BOLD, (int) targetSize));
            g.drawString(text, x2, y2);
        } else {
            Color c = new Color((int) (red / duration * frame), (int) (green / duration * frame), (int) (blue / duration * frame));
            g.setColor(c);
            int size = (int) ((targetSize - startSize) / duration * frame + startSize);
            g.setFont(new Font("Arial", Font.BOLD, size));
            int posX = (int) ((((double) (Math.abs(x - x2))) / ((double) duration) * (double) frame));
            int posY = (int) ((((double) (Math.abs(y - y2))) / ((double) duration) * (double) frame));

            if (x > x2) {
                posX = x - posX;
            } else {
                posX = x + posX;
            }
            if (y > y2) {
                posY = y - posY;
            } else {
                posY = y + posY;
            }
            g.drawString(text, posX, posY);
        }


    }

    @Override
    public boolean animate() {
        frame += v;
        if (frame == duration || (frame == 0)) {
            v = -v;
        }
        return fadeoutFrame >= fadeOut;
    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }
}
