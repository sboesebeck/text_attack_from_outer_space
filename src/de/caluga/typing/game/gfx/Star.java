package de.caluga.typing.game.gfx;

import java.awt.*;

public class Star implements Obj {
    private int x = 0;
    private int y = 0;
    private int vy = 0;


    public Star(int x, int y, int v) {
        this.x = x;
        this.y = y;
        this.vy = v;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillOval(x - 1, y - 1, 2, 2);
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
