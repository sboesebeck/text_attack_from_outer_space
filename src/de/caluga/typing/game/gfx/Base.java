package de.caluga.typing.game.gfx;

import java.awt.*;

public class Base implements Obj {

    private boolean explode = false;
    private int cycle = 0;
    private boolean finished = false;

    @Override
    public void draw(Graphics2D g) {

        if (explode) {
            for (int i = 0; i < cycle; i++) {
                g.setColor(new Color((int) (255.0 / 100.0 * cycle), 0, 0));
                g.fillOval(930 - i, 1060 - i, i * 2 + 2, i * 2 + 2);
            }
        } else {
            g.setColor(new Color(150, 180, 255));
            g.fillRect(930, 1060, 60, 20);
            Polygon p = new Polygon();
            p.addPoint(955, 1080);
            p.addPoint(965, 1080);
            p.addPoint(960, 1020);
            g.fill(p);
        }
    }

    public void explode() {
        explode = true;
    }

    @Override
    public boolean animate() {
        if (explode) {
            cycle++;
            if (cycle >= 100) {
                finished = true;
            }
        }
        return finished;
    }

    @Override
    public int getX() {
        return 960;
    }

    @Override
    public int getY() {
        return 1050;
    }
}
