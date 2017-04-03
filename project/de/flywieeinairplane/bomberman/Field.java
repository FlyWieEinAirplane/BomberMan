package de.flywieeinairplane.bomberman;

import java.io.Serializable;

/**
 * Created by flywieeinairplane on 23.03.17.
 */
public class Field implements Serializable{
    public String type;
    public Bomb bomb = null;

    public Field(String type) {
        this.type = type;
    }

    public boolean placeBomb(Bomb bomb) {
        if (this.bomb == null) {
            this.bomb = bomb;
            return true;
        } else {
            return false;
        }
    }
}
