package de.caluga.typing.game.gfx;

import java.awt.*;

public class Star implements Obj {
    private int x = 0;
    private int y = 0;
    private int sz = 1;
    private int vy = 0;


    public Star(int x, int y, int r, int v) {
        this.x = x;
        this.y = y;
        this.vy = v;
        this.sz = r;
    }

    @Override
    public void draw(Graphics2D g) {
        if (sz > 1) {
            g.setColor(Color.gray);
            g.fillOval(x - sz, y - sz, sz * 2, sz * 2);

            g.setColor(Color.WHITE);
            g.fillOval(x - (sz / 2), y - (sz / 2), sz, sz);
        } else {
            g.setColor(Color.WHITE);
            g.fillOval(x - sz, y - sz, 2 * sz, 2 * sz);
        }
    }

    @Override
    public boolean animate() {
        y = y + vy;
        if (y > 1090) {
            x = (int) (1920 * Math.random());
            y = 0;
        }
        return false;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
}
