package de.flywieeinairplane.bomberman.server;

import de.flywieeinairplane.bomberman.Grid;
import de.flywieeinairplane.bomberman.client.GameClientInterface;
import de.flywieeinairplane.bomberman.Player;
import de.flywieeinairplane.bomberman.exceptions.GameFullException;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface GameServerInterface extends Remote {

    void addClient(GameClientInterface client, String playerName) throws RemoteException, NotBoundException;

    void addPlayer(Player player) throws RemoteException, NotBoundException, GameFullException, AlreadyBoundException;

    Grid getGrid() throws RemoteException, NotBoundException;

    ArrayList<Player> getPlayerList() throws RemoteException, NotBoundException;

    Player getPlayer(String name) throws RemoteException, NotBoundException;

    void playerLeave(String playerName) throws RemoteException, NotBoundException;

    void move(String playerName, int keyIndex) throws RemoteException, NotBoundException;

    void placeBomb(String playerNam) throws RemoteException, NotBoundException;

    void restartGame() throws RemoteException, NotBoundException;
}
