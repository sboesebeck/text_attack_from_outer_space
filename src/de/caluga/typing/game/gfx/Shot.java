package de.caluga.typing.game.gfx;

import de.caluga.typing.game.MainFrame;

import java.awt.*;

public class Shot implements Obj {
    private Obj target;
    private int x = 0;
    private int y = 0;
    private int x2 = 0;
    private int y2 = 0;
    private int v = 10;
    private MainFrame main;
    private boolean exploded = false;
    private boolean exploding = false;
    private int frame = 0;
    private String txt = "";
    private boolean fail = false;

    public Shot(Obj target, int startX, int startY, MainFrame main, String text) {
        this(target, startX, startY, main, text, false);
    }

    public Shot(Obj target, int startX, int startY, MainFrame main, String text, boolean fail) {
        this.target = target;
        x = startX;
        y = startY;
        x2 = x;
        y2 = y;
        this.main = main;
        if (target instanceof Ship) {
            ((Ship) target).incCursor();
        }
        main.play("shot.wav");
        this.txt = text;
        this.fail = fail;
    }

    @Override
    public void draw(Graphics2D g) {
        if (exploding) {
            frame++;
            if (frame > 25) {
                exploded = true;
            } else {
                if (fail) {
                    g.setColor(new Color(128, 40, 40));
                    g.drawOval(x - ((int) ((double) frame / 2)), y - ((int) ((double) frame / 2)), frame, frame);
                } else {
                    g.setColor(new Color(128, 40, 40));
                    g.drawOval(x - ((int) ((double) frame * 1.5)), y - ((int) ((double) frame * 1.5)), frame * 3, frame * 3);
                    g.setColor(Color.yellow);
                    g.drawOval(x - frame, y - frame, frame * 2, frame * 2);
                }
            }
        } else {
            if (txt.equals(" ") && !fail) {
                g.setColor(Color.green);
                g.setStroke(new BasicStroke(3));
                g.drawLine(x, y, x2, y2);
                g.setColor(Color.YELLOW);
                g.fillOval(x2 - 4, y2 - 4, 8, 8);
                g.setColor(Color.RED);
                g.fillOval(x2 - 2, y2 - 2, 4, 4);
            } else {
                if (fail) {
                    g.setColor(Color.gray);
                } else {
                    g.setColor(Color.RED);
                }
                g.setStroke(new BasicStroke(3));
                g.drawLine(x, y, x2, y2);
                g.fillOval(x2 - 5, y2 - 5, 10, 10);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Menlo", Font.PLAIN, 15));
                if (x2 > x) {
                    g.drawString(txt, x2 + 15, y2);
                } else {
                    g.drawString(txt, x2 - 20, y2);

                }
            }
        }

    }

    @Override
    public boolean animate() {
        if (fail) {

        }
        if (!exploding) {
            double alpha = Math.atan(Math.abs((double) (target.getY() - y)) / Math.abs((double) (target.getX() - x)));

            int dx = (int) (Math.cos(alpha) * v);
            int dy = (int) (Math.sin(alpha) * v);
            if (target.getY() < y) {
                y = y - dy;
                y2 = y - 2 * dy;
            } else {
                y = y + dy;
                y2 = y + 2 * dy;
            }

            if (target.getX() < x) {
                x = x - dx;
                x2 = x - 2 * dx;
            } else {
                x = x + dx;
                x2 = x + 2 * dx;
            }
            if (Math.abs(target.getX() - x) < 5 && Math.abs(target.getY() - y) < 5) {
                exploding = true;

                if (target instanceof Ship) {
                    main.play("explosion.wav");
                    Ship targetShip = (Ship) this.target;
                    targetShip.hit();

                    if (targetShip.isFinished() && targetShip.getHits() == targetShip.getText().length()) {
                        targetShip.explode();
                        main.incMultiplier();
                        int score = targetShip.getHits();
                        if (score <= main.getWave()) score = main.getWave();
                        main.addObj(new TextAnim("" + score, x, y, x + 35, y + 35, 155, 155, 190, 15, 22, 90, 10));
                        int tm = (500 - targetShip.getCycle());
                        if (tm <= 0) tm = 0;
                        if (tm > 0)
                            main.addObj(new TextAnim("Tm: " + tm, x, y, x - 30, y + 25, 255, 155, 190, 15, 22, 90, 10));
                        if (main.getMultiplier() > 2)
                            main.addObj(new TextAnim("x" + main.getMultiplier(), x, y, x + 30, y - 30, 255, 122, 100, 15, 32, 90, 10));

                        score = score * main.getMultiplier() + tm;
                        main.incScore(score);
                        main.addObj(new TextAnim("" + score, x, y, x - 30, y - 30, 255, 255, 0, 20, 35, 80, 10));
                    }
                } else if (target instanceof HiddenObject) {
                    main.play("plop.wav");
                    ((HiddenObject) target).setDelete(true);
                }
                return exploded;
            }
        }
        return exploded;
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
