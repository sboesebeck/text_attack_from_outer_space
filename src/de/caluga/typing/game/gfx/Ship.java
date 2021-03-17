package de.caluga.typing.game.gfx;

import de.caluga.typing.game.MainFrame;

import java.awt.*;

public class Ship implements Obj {

    int red = 255;
    int green = 0;
    int blue = 0;
    int w = 10;
    private double v = 5;
    private int x = 900, y = 0;
    private int startX = 900, startY = 0;
    private int x1 = 900, y1 = 0;
    private int cycle = 0;
    private int hits = 0;
    private int cursor = 0;
    private String text;

    private boolean exploding = false;
    private boolean finished = false;
    private boolean active = false;

    public Ship(int startX, int startY, double v, String txt) {
        this.v = v;
        if (v <= 0) v = 0.1;
        this.x = startX;
        this.y = startY;
        this.startX = startX;
        this.startY = startY;
        x1 = x;
        y1 = y;

        this.text = txt;
        if (!text.endsWith(" ")) {
            text = text + " ";
        }
        if (text.length() > 5) {
            v = v - 2;
        }
        if (text.length() > 7) {
            v = v / 2;
        }

        if (v < 0.1) v = 0.1;
        //this.originalText = text;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isHit(String s) {
        if (text.length() > cursor) {
            return text.substring(cursor, cursor + 1).equals(s);
        }
        return false;
    }

    public String getText() {
        return text;
    }

    public void incCursor() {
        cursor++;
        if (cursor >= text.length()) {
            finished = true;
        }
    }

    @Override
    public void draw(Graphics2D g) {

        if (exploding) {
            g.setColor(new Color(red, green, blue));
            g.fillOval(x - 5, y - 5, w, w);
        } else {
            if (active) {
                g.setColor(Color.YELLOW);
            } else if (finished) {
                g.setColor(Color.GRAY);
            } else {
                g.setColor(new Color(red, green, blue));
            }
            if (text.length() > cursor) {
                String start = text.substring(cursor, cursor + 1);

                g.setFont(new Font("Menlo", Font.BOLD, 25));
                g.drawString(start, x - 15, y - 25);

                g.setColor(new Color(red, green, blue));
                if (text.length() > (cursor + 1)) {
                    g.setFont(new Font("Menlo", Font.PLAIN, 22));
                    String txt = text.substring(cursor + 1);
                    g.drawString(txt, x, y - 25);
                } else {
                    g.setColor(Color.yellow);
                }
            }
            g.fillRect(x - 5, y - 5, w, w);
        }
    }

    public int getCycle() {
        return cycle;
    }


    @Override
    public boolean animate() {
        cycle++;
        if (!exploding) {

            double alpha = Math.atan(Math.abs((double) (1080 - startY)) / Math.abs((double) (960 - startX)));

            int dx = (int) (Math.cos(alpha) * v * cycle);
            int dy = (int) (Math.sin(alpha) * v * cycle);
//
//            if (dx == 0) dx = 1;
//            if (dy == 0) dy = 1;

            if (x > 960) {
                dx = -dx;
            }
            x = startX + dx;
            y = startY + dy;
            if (y > 1020 && Math.abs(x - 960) < 10) {
                explode();
            }
        } else {
            red = incColor(red, 10);
            green = incColor(green, 10);
            blue = incColor(blue, 10);
            if (w < 50) {
                w++;
            } else {
                return true;
            }
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

    public void explode() {
        exploding = true;
        MainFrame.play("explosion2.wav");
    }


    public void hit() {
//        System.out.println("Hit called");
        if (cursor >= text.length()) {
            finished = true;
        }
        hits++;
    }

    public int getHits() {
        return hits;
    }

    public boolean isExploding() {
        return exploding;
    }

    public boolean isFinished() {
        return finished;
    }

    private int incColor(int cv, int v) {
        if (cv < 255) {
            cv += v;
        }
        if (cv > 255) {
            cv = 255;
        }
        return cv;
    }
}
