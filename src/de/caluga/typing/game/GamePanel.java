package de.caluga.typing.game;

import de.caluga.typing.game.gfx.Obj;
import de.caluga.typing.game.gfx.Ship;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

public class GamePanel extends JPanel implements ActionListener, MouseListener {
    private final Timer repaintTimer;

    private Vector<Obj> objects = new Vector<>();

    private int score = 0;
    private int multiplier = 0;
    private MainFrame main;
    private boolean fillButton = false;
    private boolean hard = false;

    public GamePanel(MainFrame main) {
//        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(1920, 1080));
//        setSize(new Dimension(1920,1080));
//        setMinimumSize(new Dimension(1920,1080));
//        setBounds(0,0,1920,1080);

        this.main = main;

        repaintTimer = new Timer(15, this);
        repaintTimer.start();

        addMouseListener(this);


    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public void incMultiplier() {
        if (multiplier < 8) {
            multiplier++;
        }
    }

    public void addObj(Obj o) {
        objects.add(o);
    }


    public java.util.List<Obj> getObjects() {
        return objects;
    }

    @Override
    public void paintComponent(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;
        super.paintComponent(g);
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g.setRenderingHints(rh);

        g.setColor(Color.black);
        g.fillRect(0, 0, 1920, 1080);

        //score
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Mult : " + multiplier, 10, 40);

        g.setColor(new Color(128, 255, 255));
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        g.drawString("Wave #" + main.getWave(), 130, 28);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        int active = 0;
        for (Obj o : objects) {
            if (o instanceof Ship) active++;
        }
        g.drawString("Incoming: " + (main.getShipsInWave() - main.getShipsCreated() + active), 500, 20);


        //language "button"

        if (fillButton) {
            g.setColor(Color.gray);
            g.fillRect(getWidth() - 100, 10, 70, 30);

        }
        g.drawRect(getWidth() - 100, 10, 70, 30);

        g.setColor(Color.blue);
        g.drawString("restart", getWidth() - 90, 25);
        if (hard) {
            g.setColor(Color.gray);
            g.fillRect(getWidth() - 250, 10, 50, 30);

        }

        g.setColor(Color.RED);
        g.drawRect(getWidth() - 250, 10, 50, 30);
        g.drawString("hard", getWidth() - 240, 28);


        g.setColor(new Color(244, 244, 0));
        g.drawRect(getWidth() - 350, 10, 50, 30);
        g.drawString(main.getLang(), getWidth() - 320, 28);


        for (Obj o : objects) {
            o.draw(g);
        }
//        g.setColor(Color.RED);
//        g.fillRect(0,getHeight()/2,getWidth(),10);
////
//        ((Graphics2D)g).drawString("parent width: "+getParent().getWidth()+" height:"+getParent().getHeight(),10,30);
//        g.setColor(Color.BLUE);
//        g.drawRect(0,0,1900,1020);
//        g.fillRect(x, y, 100, 100);
//        g.setColor(Color.WHITE);
//        ((Graphics2D)g).drawString("X: "+x+" Y:"+y,x+5,y+15);
//        ((Graphics2D)g).drawString("X: "+x+" Y:"+y,5,555);
//        ((Graphics2D)g).drawString("X: "+x+" Y:"+y,5,55);
//        g.setColor(Color.red);

        // g.fillOval(x+100,y+240,100,100);
//        g.fillOval(1000,600,100,100);
//        g.fillOval(1000,700,100,100);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int s) {
        score = s;
    }

    public void incScore(int s) {
        score += s;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getX() > getWidth() - 100 && e.getY() > 10 && e.getY() < 40 && e.getX() < getWidth() - 50) {
            fillButton = true;
        } else if (e.getX() > getWidth() - 250 && e.getY() > 10 && e.getY() < 40 && e.getX() < getWidth() - 220) {
            hard = !hard;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getX() > getWidth() - 100 && e.getY() > 10 && e.getY() < 40 && e.getX() < getWidth() - 50) {
            fillButton = false;
            main.restart();
        } else if (e.getX() > getWidth() - 350 && e.getY() > 10 && e.getY() < 40 && e.getX() < getWidth() - 300) {
            main.toggleLang();
        } else if (e.getX() > getWidth() - 250 && e.getY() > 10 && e.getY() < 40 && e.getX() < getWidth() - 220) {
            if (hard) {
                main.readComplexText();
            }
        }
    }

    public boolean isHard() {
        return hard;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
