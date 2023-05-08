package it.polimi.ingsw.distributed;

import it.polimi.ingsw.model.GameView;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            Message message = (Message) objectInputStream.readObject();


        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
}
