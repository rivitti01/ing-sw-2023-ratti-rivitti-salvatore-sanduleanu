package it.polimi.ingsw.distributed.socket;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Game;

import java.net.ServerSocket;
import java.util.Map;

public class ServerSocketImpl {
    private Game model;
    private GameController controller;
    private ServerSocket serverSocket;
    private Map<Integer,ServerHandler> clients;
    private int port;

    public ServerSocketImpl(int port){

    }
    public void start(){
        while (true){

        }
    }
}
