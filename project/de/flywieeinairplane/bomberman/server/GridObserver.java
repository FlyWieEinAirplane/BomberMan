package de.flywieeinairplane.bomberman.server;

import de.flywieeinairplane.bomberman.Explosion;
import de.flywieeinairplane.bomberman.client.GameClientInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Observer;

public class GridObserver implements Observer {
    private GameClientInterface client = null;
    private String name;

    GridObserver(GameClientInterface client) throws RemoteException, NotBoundException {
        this.client = client;
    }

    @Override
    public void update(Observable gameServer, Object arg) {
        GameServer gs = (GameServer) gameServer;
        try {
            this.client.updateGrid(gs.gameGrid, gs.playerList);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void die() {
        try {
            client.die();
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    public void drawExplosion(Explosion explosion) {
        try {
            client.drawExplosion(explosion);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    public void win() {
        try {
            client.win();
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}
