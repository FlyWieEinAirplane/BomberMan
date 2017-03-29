package de.flywieeinairplane.bomberman.server;

import de.flywieeinairplane.bomberman.*;
import de.flywieeinairplane.bomberman.exceptions.GameFullException;
import de.flywieeinairplane.bomberman.client.GameClientInterface;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.DelayQueue;


public class GameServer extends Observable implements GameServerInterface {

    public Grid gameGrid;
    public ArrayList<Player> playerList = new ArrayList<>();
    private DelayQueue<DelayedBomb> delayedBombs = new DelayQueue<>();
    private ObserverList observerList = new ObserverList();


    public GameServer(int sizeX, int sizeY) {
        this.gameGrid = new Grid(sizeX, sizeY);
        Thread watcher = new Thread(new BombWatcher(delayedBombs, this));
        watcher.start();

    }

    @Override
    public void addClient(GameClientInterface client, String playerName) throws RemoteException, NotBoundException {
        GridObserver observer = new GridObserver(client);
        observer.setName(playerName);
        this.observerList.addObserver(observer);
        this.addObserver(observer);
    }

    @Override
    public void addPlayer(Player player) throws RemoteException, NotBoundException, GameFullException, AlreadyBoundException {

        if (this.playerList.size() == 4) {
            throw new GameFullException();
        } else {
            Player currentPlayer = null;
            for (Player lPlayer : playerList) {
                if (lPlayer.name.equals(player.name)) {
                    currentPlayer = lPlayer;
                }
            }
            if (currentPlayer == null) {
                switch (this.playerList.size()) {
                    case 0:
                        player.posX = 1.5f;
                        player.posY = 1.5f;
                        break;
                    case 1:
                        player.posX = this.gameGrid.getSizeX()-1.5f;
                        player.posY = this.gameGrid.getSizeY()-1.5f;
                        break;
                    case 2:
                        player.posX = 1.5f;
                        player.posY = this.gameGrid.getSizeY()-1.5f;
                        break;
                    case 3:
                        player.posX = this.gameGrid.getSizeX()-1.5f;
                        player.posY = 1.5f;
                        break;
                }
                System.out.printf("Player x: %f und y: %f\n", player.posX, player.posY);
                this.playerList.add(player);
                this.setChanged();
                this.notifyObservers();
            } else {
                throw new AlreadyBoundException();
            }
        }

    }

    @Override
    public Grid getGrid() throws RemoteException, NotBoundException {
        return this.gameGrid;
    }

    public ArrayList<Player> getPlayerList() throws RemoteException, NotBoundException {
        return this.playerList;
    }

    public Player getPlayer(String name) throws RemoteException, NotBoundException {
        for (Player lPlayer : this.playerList) {
            if (lPlayer.name.equals(name)) {
                return lPlayer;
            }
        }
        return null;
    }

    @Override
    public void move(String playerName, int keyIndex) {
        float step = 1.0f;
        System.out.println("KeyIndex: " + keyIndex);
        Player currentPlayer = null;
        try {
            currentPlayer = getPlayerByName(playerName);
            switch (keyIndex) {
//                W
                case 119:
                    if (!collisionDetected("w", step, currentPlayer.posX, currentPlayer.posY)) {
                        currentPlayer.posY -= step;
                    }
                    break;
//                A
                case 97:
                    if (!collisionDetected("a", step, currentPlayer.posX, currentPlayer.posY)) {
                        currentPlayer.posX -= step;

                    }

                    break;
//                S
                case 115:
                    if (!collisionDetected("s", step, currentPlayer.posX, currentPlayer.posY)) {
                        currentPlayer.posY += step;

                    }
                    break;
//                D
                case 100:
                    if (!collisionDetected("d", step, currentPlayer.posX, currentPlayer.posY)) {
                        currentPlayer.posX += step;
                    }
                    break;
            }
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        this.setChanged();
        this.notifyObservers();
//        System.out.printf("Player %s moved/n", playerName);
    }

    @Override
    public void placeBomb(String playerName) {
        Player currentPlayer = null;
        try {
            currentPlayer = getPlayerByName(playerName);
            Bomb newBomb = new Bomb(currentPlayer.getBombType(), currentPlayer.getRange());
            gameGrid.getField(currentPlayer.posX, currentPlayer.posY).placeBomb(newBomb);
            delayedBombs.put(new DelayedBomb(currentPlayer.posX, currentPlayer.posY, 2000, newBomb));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        this.setChanged();
        this.notifyObservers();
    }

    @Override
    public void restartGame() throws RemoteException, NotBoundException {
        this.gameGrid = new Grid(this.gameGrid.getSizeX(), this.gameGrid.getSizeY());
        this.delayedBombs.clear();
        for (Player player : this.playerList) {
            player.alive = true;
        }
    }

    private boolean collisionDetected(String direction, float stepSize, float x, float y) {

        double restX = x % 1.0;
        double restY = y % 1.0;

        switch (direction) {
            case "w":
                if (restY - stepSize > 0) return false;
                if (restX == 0) {
                    boolean direct = isFieldBlocked(x, y - 1);
                    boolean indirect = isFieldBlocked(x - 1, y - 1);
                    if (direct || indirect) return true;
                } else {
                    return isFieldBlocked(x, y - 1);
                }


            case "a":
                if (restX - stepSize > 0) return false;
                if (restY == 0) {
                    boolean direct = isFieldBlocked(x - 1, y);
                    boolean indirect = isFieldBlocked(x - 1, y - 1);
                    if (direct || indirect) return true;
                } else {
                    return isFieldBlocked(x - 1, y);
                }


            case "s":
                if (restY + stepSize < 1) return false;
                if (restX == 0) {
                    boolean direct = isFieldBlocked(x, y + 1);
                    boolean indirect = isFieldBlocked(x - 1, y + 1);
                    if (direct || indirect) return true;
                } else {
                    return isFieldBlocked(x, y + 1);
                }


            case "d":
                if (restX + stepSize < 1) return false;
                if (restY == 0) {
                    boolean direct = isFieldBlocked(x + 1, y);
                    boolean indirect = isFieldBlocked(x + 1, y - 1);
                    if (direct || indirect) return true;
                } else {
                    return isFieldBlocked(x + 1, y);
                }
        }

        return false;
    }

    private boolean isFieldBlocked(float x, float y) {
        Field field = gameGrid.getField(x, y);
        return !field.type.equals("weg") || field.bomb != null;
    }

    public void playerLeave(String playerName) {
        try {
            Player toDelete = getPlayerByName(playerName);
            this.playerList.remove(toDelete);
        } catch (InvalidKeyException e) {
            System.out.println("Player was dead when he left");
        }

        try {
            GridObserver obs = this.observerList.getByName(playerName);
            this.deleteObserver(obs);
            this.observerList.remove(obs);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        this.setChanged();
        this.notifyObservers();
        System.out.printf("Player %s left the game\n", playerName);
    }

    public void explodeBomb(DelayedBomb bomb) {
        Field bombField = this.gameGrid.getField(bomb.x, bomb.y);
        expandBomb(bomb);
        bombField.bomb = null;

        this.setChanged();
        this.notifyObservers();
    }

    private void expandBomb(DelayedBomb bomb) {
        boolean wCollision = false;
        boolean aCollision = false;
        boolean sCollision = false;
        boolean dCollision = false;
        Explosion ex = new Explosion();
        clearField(bomb.x, bomb.y);
        ex.addCoordinates(bomb.x, bomb.y);

        for (int i = 1; i <= bomb.bomb.range; i++) {
            if (!dCollision) {
                dCollision = clearField(bomb.x + i, bomb.y);
                if (!dCollision) ex.addCoordinates(bomb.x + i, bomb.y);
            }
            if (!aCollision) {
                aCollision = clearField(bomb.x - i, bomb.y);
                if (!aCollision) ex.addCoordinates(bomb.x - i, bomb.y);
            }
            if (!sCollision) {
                sCollision = clearField(bomb.x, bomb.y + i);
                if (!sCollision) ex.addCoordinates(bomb.x, bomb.y + i);
            }
            if (!wCollision) {
                wCollision = clearField(bomb.x, bomb.y - i);
                if (!wCollision) ex.addCoordinates(bomb.x, bomb.y - i);
            }
        }
        observerList.drawExplosion(ex);
        this.setChanged();
        this.notifyObservers();
    }

    //    returns true if collision was detected and expansion should stop or false if not
    private boolean clearField(int x, int y) {
        Field field = this.gameGrid.getField(x, y);
//        check boundaries
        if (x >= 0 && x < this.gameGrid.getSizeX() && y >= 0 && x < this.gameGrid.getSizeY() + 3) {
            if (field.type.equals("wall")) {
                return true;
            } else if (field.type.equals("rock")) {
                field.type = "weg";
            }
            if (field.bomb != null) {
                try {
                    DelayedBomb db = getDelayedBombByBomb(field.bomb);
                    db.explodeNow();
                    //explodeBomb(db);
                    //delayedBombs.remove(db);
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
            }
            int aliveCount = 0;
            boolean playerDied = false;
            for (Player player : this.playerList) {
                if ((int) player.posX == x && (int) player.posY == y) {
                    try {
                        this.observerList.getByName(player.name).die();
                        player.alive = false;
                        playerDied = true;

                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }
                }
                if (player.alive) {
                    aliveCount ++;
                }
            }
            if (aliveCount == 1 && playerDied) {
                for (Player player : this.playerList) {
                    if (player.alive) {
                        try {
                            this.observerList.getByName(this.playerList.get(0).name).win();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            return false;
        } else {
            System.out.println("out of range");
        }
        return true;
    }

    private DelayedBomb getDelayedBombByBomb(Bomb bomb) throws InvalidKeyException {
        for (DelayedBomb del : this.delayedBombs) {
            if (del.bomb == bomb) {
                return del;
            }
        }
        throw new InvalidKeyException("DelayedBomb for that Bomb not found");
    }

    private Player getPlayerByName(String playerName) throws InvalidKeyException {
        for (Player player : this.playerList) {
            if (player.name.equals(playerName)) {
                return player;
            }
        }
        throw new InvalidKeyException("Player not found");
    }
}
