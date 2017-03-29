package de.flywieeinairplane.bomberman.client;

import de.flywieeinairplane.bomberman.*;
import de.flywieeinairplane.bomberman.server.GameServerInterface;
import de.flywieeinairplane.bomberman.exceptions.GameFullException;
import processing.core.PApplet;

import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class GameClient extends PApplet implements GameClientInterface, Serializable {
    String serverHost = "localhost";
    int serverPort = 9001;
    String ownIP = "localhost";
    int ownPort = 9005;

    private String playerName = "PlayerName5";
    private Player player;
    private boolean win = false;

    private int fieldSize = 50;

    private Grid gameGrid;
    private GameServerInterface gameServer;
    private ArrayList<Player> players;
    private ArrayList<Explosion> explosions = new ArrayList<>();


    public GameClient() throws RemoteException, NotBoundException {

        LocateRegistry.createRegistry(ownPort);
        GameClientInterface clientStub = (GameClientInterface) UnicastRemoteObject.exportObject(this, ownPort);

        Registry localRegistry = LocateRegistry.getRegistry(ownIP, ownPort);
        localRegistry.rebind("client", clientStub);

        Registry serverRegistry = LocateRegistry.getRegistry(serverHost, serverPort);
        try {

            this.gameServer = (GameServerInterface) serverRegistry.lookup("client");
        } catch (ConnectException e) {
            System.out.println("Server not available");
            throw new NotBoundException("Server not available");
        }


        this.gameServer.addClient(this, this.playerName);
        this.gameGrid = gameServer.getGrid();
        this.players = gameServer.getPlayerList();

//        LocateRegistry.createRegistry(ownPort);
//        Registry localRegistry = LocateRegistry.getRegistry(ownIP, ownPort);
//        GameClientInterface clientStub = (GameClientInterface) UnicastRemoteObject.exportObject(this, ownPort);
//        localRegistry.rebind("client", clientStub);
        player = new Player(playerName);
        try {
            this.gameServer.addPlayer(player);
        } catch (GameFullException e) {
            System.out.println("Game is full already");

        } catch (AlreadyBoundException e) {
            System.out.println("Playername already in use");
        }

    }


    public void settings() {
        try {
            size(gameServer.getGrid().getSizeX() * fieldSize, gameServer.getGrid().getSizeY() * fieldSize);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

    }

    public void setup() {
        background(200);
    }

    public void draw() {
        background(200);
        color(0);
        int rowNumber = 0;
        for (Field[] row : gameGrid.getGrid()) {
            int fieldNumber = 0;
            for (Field field : row) {
                if (field.type.equals("wall")) {
                    fill(0);
                }
                if (field.type.equals("rock")) {
                    fill(150, 100, 50);
                }
//                Draw Field
                rect(fieldNumber * fieldSize, rowNumber * fieldSize, fieldSize, fieldSize);
                noFill();
                if (field.bomb != null) {
//                    Draw Bomb
                    fill(0);
                    ellipse(fieldNumber * fieldSize + 25, rowNumber * fieldSize + 25, 40, 40);
                    noFill();
                }
                fieldNumber++;
            }
            rowNumber++;
        }

//        draw player

        color(0, 0, 100);
        fill(0, 0, 200);
        strokeWeight(2);
        for (Player player : players) {
            if (player == this.player){
                fill(0, 180, 0);
            }
            if (player.alive)
                ellipse(player.posX * fieldSize, player.posY * fieldSize, 30, 30);
            if (player == this.player){
                fill(0, 0, 200);
            }
        }

//        Draw explosions

        fill(255, 255, 0);
        synchronized (explosions) {
            Explosion toDelete = null;
            for (Explosion explosion : this.explosions) {

                for (Coordinates cood : explosion.coordinatesList) {
                    rect(cood.x * fieldSize, cood.y * fieldSize, fieldSize, fieldSize);
                }

                explosion.increaseFrame();

                if (explosion.over()) {
                    toDelete = explosion;
                }
            }
            explosions.remove(toDelete);
        }
        if (win) {
            textAlign(CENTER);
            textSize(30);
            text("Congratulation, You won the game",this.gameGrid.getSizeX()*fieldSize/2,this.gameGrid.getSizeY()*fieldSize/2);
            delay(5000);
            try {
                this.gameServer.restartGame();
            } catch (RemoteException | NotBoundException e) {
                System.out.println("Game could not be restarted");
            }
            this.win = false;
        }
    }

    public void keyPressed() {
        if (!this.player.alive) {
            return;
        }
        int keyIndex = -1;
        if (key == 'o') {
            keyIndex = 1;
        } else if (key == 'w') {
            keyIndex = key - 'w';
        } else if (key == 'a') {
            keyIndex = key - 'a';
        } else if (key == 's') {
            keyIndex = key - 's';
        } else if (key == 'd') {
            keyIndex = key - 'd';
        }
        if (keyIndex == -1) {
            // If it's not a letter key, clear the screen
        } else if (keyIndex == 0) {
            // It's a letter key, fill a rectangle
            try {
                gameServer.move(playerName, key);
//                Player thisPlayer = gameServer.getPlayer(playerName);
//                System.out.printf("PosX: %f, PosY: %f, Richtung: %s\n", thisPlayer.posX, thisPlayer.posY, key);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
        } else if (keyIndex == 1) {
            try {
                gameServer.placeBomb(playerName);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void exit() {
        try {
            gameServer.playerLeave(playerName);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        super.exit();
    }


    @Override
    public void updateGrid(Grid gameGrid, ArrayList<Player> players) throws RemoteException, NotBoundException {
        this.gameGrid = gameGrid;
        this.players = players;
        System.out.println("Client updated");
    }

    @Override
    public void die() throws RemoteException, NotBoundException {
        this.player.alive = false;
    }

    @Override
    public void drawExplosion(Explosion explosion) throws RemoteException, NotBoundException {
        synchronized (explosions) {
            this.explosions.add(explosion);
        }
    }

    @Override
    public void win() throws RemoteException, NotBoundException {
        this.win = true;
    }
}
