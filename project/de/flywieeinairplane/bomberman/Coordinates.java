package de.flywieeinairplane.bomberman;

import java.io.Serializable;

/**
 * Created by flywieeinairplane on 27.03.17.
 */
public class Coordinates implements Serializable {
    public int x;
    public int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
