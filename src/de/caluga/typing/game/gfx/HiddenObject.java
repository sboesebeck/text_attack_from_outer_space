package de.caluga.typing.game.gfx;

import java.awt.*;

public class HiddenObject implements Obj {
    private int x;
    private int y;
    private boolean delete = false;

    @Override
    public void draw(Graphics2D g) {

    }

    @Override
    public boolean animate() {
        x = (int) (x + (25 - (int) (Math.random() * 50)));
        y = (int) (y + (25 - (int) (Math.random() * 50)));
        return delete;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    @Override
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
