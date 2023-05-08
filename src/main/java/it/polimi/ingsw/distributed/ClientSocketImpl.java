package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.view.TUISocket;
import it.polimi.ingsw.view.TextualUI;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientSocketImpl implements PropertyChangeListener {
    private final int port;
    private final String ip;
    private Socket socket;
    private TUISocket view;
    GameView modelView;
    private String nickname;

    public ClientSocketImpl(int port, String ip) {
        this.port = port;
        this.ip = ip;
    }
    public void startClient() throws IOException, ClassNotFoundException, InterruptedException {
        socket = new Socket(this.ip, this.port);
        System.out.println("Connection established to "+ ip + ":"+port);
        Scanner scanner = new Scanner(System.in);
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);


        System.out.println("Inserisci nome utente");
        Message message = new Message(scanner.nextLine());
        nickname = message.getNickName();
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
        String setUp = (String) objectInputStream.readObject();
        if (setUp.equals("playerNumber")){
            System.out.println("Inserisci il numero di giocatori");
            message = new Message(scanner.nextInt());
            objectOutputStream.writeObject(message);
        }
        if (setUp.equals("cantPlay")){
            System.out.println("Il server ha già una partita piena, non puoi giocare");
        }

        modelView = (GameView) objectInputStream.readObject();
        //GameController controller = new GameController(modelView);
        view = new TUISocket(null,modelView,nickname,socket);
        view.addPropertyChangeListener(this);
        //Thread thread = new Thread(view);
        //thread.start();
        int i = 0;
        while (true){
            if (i > 0){
                modelView = (GameView) objectInputStream.readObject();
                if (modelView.getCurrentPlayerNickname().equals(nickname)) view.newTurn(modelView);
                else view.newTurnNotMine(modelView);
            }else {
                if (modelView.getCurrentPlayerNickname().equals(nickname)) view.newTurn(modelView);
                else view.newTurnNotMine(modelView);
            }
            i++;
        }








    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
