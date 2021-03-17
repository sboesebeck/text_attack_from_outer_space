# Text Attack from outer Space

This is a little game I created in Java. It was tested on JDK15, but it should also run with JDK11.

It does not have any dependencies, just runs with the plain JDK.

## how to play

When starting up the application, you start the game immediately. The "Text-Ships" are flying at you from the top of the
screen towards the bottom. Every "ship" does have text associated with it. When you typed the text correctly followed by
an SPACE, you destroy the ship.

Every killed ship will increase your score multiplier. Every typo will reset your Multiplier to 1.

If one of those text-ships reach your base at the bottom of the screen, you lost.

You can restart the game by clicking the restart button at the top right corner.

## implementation

The main Class is called `MainFrame` and it does host the main animation loop and the whole game logic. The graphics are
built in the class `GamePanel`.

The GamePanel redraws the screen every 15ms (~67 fps), same as the animation loop.

All items animated on the screen are represented in Java by objects implementing the `Obj` interface. This interface
defines 4 Methods:

```
public interface Obj {

     void draw(Graphics2D g);
     //returns true, if it needs to be deleted
     boolean animate();
     int getX();
     int getY();
}
```

of course, draw is called when drawing the screen for each object. the animation is implemented in `animate`. If that
returns true, animation is finished and the corresponding object should be removed from view. The getter methods for x
and y are used for collision detection.

Here is a simple example for such an object (the stars in the background are each one of those):

```

public class Star implements Obj{
    private int x=0;
    private int y=0;
    private int vy=0;


    public Star(int x, int y, int v) {
        this.x = x;
        this.y = y;
        this.vy =v;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillOval(x-1,y-1,2,2);
    }

    @Override
    public boolean animate() {
        y=y+vy;
        if (y>1090){
            x= (int) (1920* Math.random());
            y=0;
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
```

# Disclaimer

this is only tested on a few machines, use at own risk

All sounds used in this project can be found at [soundbible.com](https://soundbible.com)

The code is published under GPL