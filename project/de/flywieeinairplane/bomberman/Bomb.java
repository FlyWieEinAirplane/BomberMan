package de.flywieeinairplane.bomberman;

import java.io.Serializable;

/**
 * Created by flywieeinairplane on 24.03.17.
 */
public class Bomb implements Serializable {
    public String type;
    public int range;

    public Bomb(String type, int range) {
        this.type = type;
        this.range = range;
    }
}
