package de.caluga.typing.game.gfx;

import java.awt.*;

public interface Obj {

     void draw(Graphics2D g);

     //returns true, if it needs to be deleted
     boolean animate();

     int getX();

     int getY();
}
