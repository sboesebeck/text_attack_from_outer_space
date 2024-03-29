package de.caluga.typing.game;

import de.caluga.typing.game.gfx.*;

import javax.sound.sampled.*;
import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class MainFrame extends JFrame implements ActionListener, KeyListener {

    private final static Map<String, byte[]> sounds = new Hashtable<>();
    private static Map<String, AtomicInteger> soundCount = new ConcurrentHashMap<>();
    public GamePanel gamePanel;
    private long cycleCount = 0;
    private java.util.List<String> words = new ArrayList<>();
    private java.util.List<Obj> toAdd = new Vector<>();
    private Ship activeShip = null;
    private Obj modalObject = null;
    private int wave = 1;
    private int typos = 0;
    private int typed = 0;
    private int shipsCreated = 0;
    private double maxVel = 1.2;
    private boolean gameOver = false;
    private String lang = "de";

    public MainFrame() {
        super("Text Attack from outer Space");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);

        initUI();
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            MainFrame ex = new MainFrame();
            ex.setVisible(true);
        });
    }

    public static void play(String filename) {

        if (soundCount.containsKey(filename) && soundCount.get(filename).get() > 2) {
            return;
        }

        try {
            if (!sounds.containsKey(filename)) {
                InputStream audioFile = MainFrame.class.getClassLoader().getResourceAsStream(filename);
                if (audioFile == null) {
                    System.err.println("Did not find file " + filename);
                    return;
                }
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                audioFile.transferTo(bout);
                bout.close();
                sounds.put(filename, bout.toByteArray());
                audioFile.close();

            }


            InputStream in = new ByteArrayInputStream(sounds.get(filename));

            // create an audiostream from the inputstream
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(in);

//            if (clip != null && clip.isRunning()){
//                clip.stop();
//                clip=null;
//            }

//            if (clip==null) {
            Clip clip = AudioSystem.getClip();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    soundCount.get(filename).decrementAndGet();
                    clip.close();
                }
            });
            clip.open(audioStream);
//            }
            soundCount.putIfAbsent(filename, new AtomicInteger());
            soundCount.get(filename).incrementAndGet();
            clip.start();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        readSimpleText();
        gamePanel = new GamePanel(this);
        add(gamePanel);
        for (int i = 0; i < 150; i++) {
            //add stars
            int sz = (int) (Math.random() * 3);
            int v = (int) (Math.random() * 10) + 1;
            if (sz >= v) {
                v = 1;
            } else {
                v = v - sz;
            }


            gamePanel.addObj(new Star((int) (Math.random() * 1920), (int) (Math.random() * 1080), sz, v));
        }

        setResizable(false);
        pack();

        setTitle("Text Attack from outer Space");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.addObj(new Base());
        Timer t = new Timer(15, this);
        t.start();
        addKeyListener(this);

        restart();
    }

    public void readSimpleText() {
        read("500_" + lang + ".txt");
    }

    public void readComplexText() {
        read("words_" + lang + ".txt");
    }

    public void read(String file) {
        System.out.println("Reading in " + file);
        BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(file)));
        String l = "";
        words.clear();
        try {
            while ((l = br.readLine()) != null) {
                words.add(l);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRandomWord(int maxLen) {
        String w = null;
        while (w == null || w.length() > maxLen || w.isBlank() || w.isEmpty()) {
            w = words.get((int) (words.size() * Math.random()));
        }
        return w;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int shipCount = 0;
        //animations
        java.util.List<Obj> toRemove = new ArrayList<>();
        for (Obj o : gamePanel.getObjects()) {
            if (modalObject != null) {
                if (!(o instanceof Ship) && !(o instanceof Shot)) {
                    if (o.animate()) {
                        toRemove.add(o);
                    }
                }
            } else {
                if (o.animate()) {
                    toRemove.add(o);
                }
                if (o instanceof Ship) {
                    shipCount++;
                    if (((Ship) o).isFinished() && o == activeShip) {
                        activeShip = null;
                    }
                }
            }
        }
        for (Obj o : toRemove) {
            if (o == activeShip) {
                activeShip = null;
            }
            if (o == modalObject) {
                modalObject = null;
            }
            if (o instanceof Ship) {
                shipCount--;
                if (o.getY() >= 1020) {
                    gamePanel.addObj(new TextAnim("Game Over!", 550, 545, 600, 565, 255, 128, 190, 32, 100, 200, 100));
                    gameOver = true;
                    explodeAll();
                    play("gameover.wav");
                }
            }

            gamePanel.getObjects().remove(o);
        }

        if (gameOver) {
            return;
        }
        java.util.List<Obj> lst = toAdd;
        toAdd = new Vector<>();

        for (Obj o : lst) {
            gamePanel.addObj(o);
        }
        if (shipsCreated >= getShipsInWave()) {
            //next wave
            if (shipCount == 0) {

                nextWave();
//            } else {
//                System.out.println("Waiting for ships to be destroyed!");
            }

        } else {

            if (modalObject == null) {
                int mod = (200 - wave * 4);
                if (mod <= 40) {
                    mod = 40;
                }
                if (cycleCount % mod == 0) {
                    //create ships
                    shipsCreated++;

                    if (wave == 3) {
                        maxVel = 2.2;
                    }
                    if (wave == 5) {
                        maxVel = 2.8;
                    }
                    if (wave == 7) {
                        maxVel = 3.0;
                    }

                    int maxlen = 3 + (wave * 3);
                    if (gamePanel.isHard()) {
                        maxlen = maxlen * 2;
                        maxVel = maxVel - 2;
                        if (maxVel <= 0) maxVel = 1;
                    }

                    int x = (int) (Math.random() * 1920);
                    while (true) {

                        boolean ok = true;
                        for (Obj o : gamePanel.getObjects()) {
                            if (o instanceof Ship) {
                                if (Math.abs(o.getX() - x) < 25) {
                                    x = (int) (Math.random() * 1920);
                                    ok = false;
                                    break;
                                }
                            }
                        }
                        if (ok) break;
                    }
                    double minVel = wave * 0.5;
                    if (gamePanel.isHard()) {
                        minVel += 0.5;
                    }
                    if (minVel < 1) minVel = 1;
                    double v = (Math.random() * maxVel) + minVel;
                    String txt = getRandomWord(maxlen);
                    v = v - txt.length() / 10.0;
                    if (v < minVel) v = minVel;
                    System.out.println("minVel: " + minVel + " maxVel: " + maxVel + " v: " + v + " txt:" + txt);
                    gamePanel.addObj(new Ship(x, 0, v, txt));
                    if (wave >= 3) {
                        for (int i = 0; i < (wave / 3); i++) {
                            if (Math.random() < 0.3) {
                                x = (int) (Math.random() * 1920);
                                while (true) {
                                    boolean ok = true;
                                    for (Obj o : gamePanel.getObjects()) {
                                        if (o instanceof Ship) {
                                            if (Math.abs(o.getX() - x) < 25) {
                                                x = (int) (Math.random() * 1920);
                                                ok = false;
                                                break;
                                            }
                                        }
                                    }
                                    if (ok) break;

                                }
                                gamePanel.addObj(new Ship((int) (Math.random() * 1920), 0, Math.random() * maxVel / 2.0 + minVel, getRandomWord(1 + (wave * 2))));
                            }
                        }
                    }
                }
            }
            cycleCount++;
        }


    }

    private void removeAllObj() {

        gamePanel.getObjects().removeIf(new Predicate<Obj>() {
            @Override
            public boolean test(Obj obj) {
                return (obj instanceof Ship) || (obj instanceof Shot);
            }
        });
    }

    private void explodeAll() {
        for (Obj obj : gamePanel.getObjects()) {
            if (obj instanceof Ship) {
                ((Ship) obj).explode();
            } else if (obj instanceof Base) {
                ((Base) obj).explode();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (modalObject != null || e.getExtendedKeyCode() == 27) {
            return;
        }
        String str = "" + e.getKeyChar();
//        System.out.println("Key: " + str);
        if (activeShip == null) {
            for (Object o : gamePanel.getObjects().stream().sorted(new Comparator<Obj>() {
                @Override
                public int compare(Obj o1, Obj o2) {
                    return -Integer.valueOf(o1.getY()).compareTo(Integer.valueOf(o2.getY()));
                }
            }).toArray()) {
                if (o instanceof Ship) {
                    if (((Ship) o).isHit(str)) {
                        ((Ship) o).setActive(true);
                        activeShip = (Ship) o;
                        break;
                    }
                }
            }
            if (activeShip == null) {
//                System.out.println("you mistyped!");
                HiddenObject target = new HiddenObject();
                target.setX((int) (Math.random() * 1920));
                target.setY((int) (Math.random() * 900));
                gamePanel.addObj(target);
                gamePanel.addObj(new Shot(target, 960, 1050, this, str, true));
                gamePanel.setMultiplier(1);
//                play("fail.wav");
                typos++;
            } else {
                typed++;
                gamePanel.addObj(new Shot(activeShip, 960, 1050, this, str));

            }

        } else {
            if (activeShip.isHit(str)) {
                typed++;

                gamePanel.addObj(new Shot(activeShip, 960, 1050, this, str));

                if (activeShip.isFinished()) {
//                    System.out.println("Ship finished... ");
                    activeShip.setActive(false);
                    activeShip = null;
                }
            } else {
//                System.out.println("Missed active ship!");
//                play("fail.wav");
                HiddenObject target = new HiddenObject();
                target.setX((int) (Math.random() * 1920));
                target.setY((int) (Math.random() * 900));
                gamePanel.addObj(target);
                gamePanel.addObj(new Shot(target, 960, 1050, this, str, true));
                gamePanel.setMultiplier(1);
            }
        }
    }
//    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (modalObject == null) {
                modalObject = new BlockingTextObj("Pause", 550, 500, 900, 550, 100, 100, 220, 50, 100, 200, 30);
                gamePanel.addObj(modalObject);
            } else {
                gamePanel.getObjects().remove(modalObject);
                modalObject = null;
            }
            return;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public int getWave() {
        return wave;
    }

    public int getMultiplier() {
        return gamePanel.getMultiplier();
    }

    public void incScore(int sc) {
        gamePanel.incScore(sc);
    }

    public void addObj(Obj o) {
        toAdd.add(o);
    }

    public void incMultiplier() {
        gamePanel.incMultiplier();
    }

    public void restart() {
        removeAllObj();
        wave = 0;
        maxVel = 1;
        gamePanel.setMultiplier(1);
        gamePanel.setScore(0);
        modalObject = new BlockingTextObj("New Game - press ESC", 500, 500, 600, 500, 255, 100, 100, 15, 90, 100, 15);
        gamePanel.addObj(modalObject);
        new Thread() {
            public void run() {
                while (modalObject != null) {
                    Thread.yield();
                }
                nextWave();
            }
        }.start();


    }

    public void nextWave() {


        activeShip = null;
        shipsCreated = 0;
        if (gamePanel.getScore() != 0 || wave > 0) {
            int x = getWidth() / 2 - 200;
            int y = getHeight() / 2 - 100;
            //showing wave bonus score
            addObj(new TextAnim("Wave Bonus", x, y, x, y, 155, 155, 190, 40, 45, 200, 10));
            y = y + 150;
            addObj(new TextAnim("Descruction Score: " + typed, x - 100, y, x + 135, y + 135, 255, 155, 120, 15, 42, 90, 10));
            if (typos > 0) {
                addObj(new TextAnim("Typos: -" + typos, x, y, x - 30, y + 30, 255, 20, 20, 20, 45, 80, 10));
            }
            if (getMultiplier() > 2)
                addObj(new TextAnim("x" + getMultiplier(), x, y, x - 130, y + 130, 255, 122, 100, 15, 32, 90, 10));

            int score = (typed - typos) + getShipsInWave();
            if (score < 0) {
                score = getShipsInWave();
            } else {
                score = score * gamePanel.getMultiplier();
            }
            addObj(new TextAnim("Total wave bonus: " + score, x, y, x + 50, y + 150, 255, 90, 90, 20, 45, 180, 20));

        }
        typos = 0;
        typed = 0;
        //starting new wave...
        wave++;
        modalObject = new TextAnim("Wave: " + wave, 500, 500, 600, 500, 255, 100, 100, 15, 90, 100, 15);
        gamePanel.addObj(modalObject);
        gameOver = false;
        boolean found = false;
        for (Obj o : gamePanel.getObjects()) {
            if (o instanceof Base) {
                found = true;
                break;
            }
        }
        if (!found) gamePanel.addObj(new Base());

        play("startup.wav");
    }

    public int getShipsCreated() {
        return shipsCreated;
    }

    public int getShipsInWave() {
        return wave * 5 + 5;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String l) {
        if (l.equals(lang)) return;
        lang = l;
        if (gamePanel.isHard()) {
            readComplexText();
        } else {
            readSimpleText();
        }

    }

    public void toggleLang() {
        if (lang.equals("de")) {
            setLang("en");
        } else {
            setLang("de");
        }
    }
}
