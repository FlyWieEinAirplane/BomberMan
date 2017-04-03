package de.flywieeinairplane.bomberman;

import java.io.Serializable;

/**
 * Created by flywieeinairplane on 23.03.17.
 */
public class Player implements Serializable{
    public String name;
    public float posX;
    public float posY;
    public String bombType;
    public int range = 2;
    public boolean alive = true;


    public Player(String name) {
        this.name = name;
        this.bombType = "norml";
    }

    public String getBombType() {
        return bombType;
    }

    public int getRange() {
        return range;
    }
}
