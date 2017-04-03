package de.flywieeinairplane.bomberman;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by flywieeinairplane on 27.03.17.
 */
public class Explosion implements Serializable {
    public int currentFrame;
    public int length; // in frames
    public ArrayList<Coordinates> coordinatesList = new ArrayList<>();

    public Explosion() {
        this.currentFrame = 0;
        this.length = 30;
    }

    public void increaseFrame() {
        this.currentFrame++;
    }

    public boolean over() {
        return currentFrame >= length;
    }

    public void addCoordinates(int x, int y) {
        this.coordinatesList.add(new Coordinates(x,y));
    }
}
