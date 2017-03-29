package de.flywieeinairplane.bomberman.server;

import de.flywieeinairplane.bomberman.Explosion;

import java.security.InvalidKeyException;
import java.util.ArrayList;

/**
 * Created by flywieeinairplane on 27.03.17.
 */
public class ObserverList {
    private ArrayList<GridObserver> observerList = new ArrayList<>();

    public void addObserver(GridObserver observer) {
        this.observerList.add(observer);
    }

    public GridObserver getByName(String name) throws InvalidKeyException {
        for (GridObserver observer : this.observerList) {
            if (observer.getName().equals(name)) {
                return observer;
            }
        }
        throw new InvalidKeyException("Not GridObserver found for name " + name);
    }

    public void drawExplosion(Explosion explosion) {
        for (GridObserver observer :
                this.observerList) {
            observer.drawExplosion(explosion);
        }
    }

    public void remove(GridObserver obs) {
        this.observerList.remove(obs);
    }
}
