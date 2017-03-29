package de.flywieeinairplane.bomberman.server;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;


public class MainServer {
    public static void main(String[] args) throws RemoteException {

        LocateRegistry.createRegistry(9001);
        GameServer gameServer = new GameServer(19, 15);
        GameServerInterface stub = (GameServerInterface) UnicastRemoteObject.exportObject(gameServer, 9001);
        RemoteServer.setLog(System.out);
        Registry registry = LocateRegistry.getRegistry("localhost", 9001);
        registry.rebind("client", stub);
        System.out.println("Server ist jetzt erreichbar");

    }

}
