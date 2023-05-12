package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.view.TUISocket;
import it.polimi.ingsw.view.TextualUI;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class ClientSocketImpl implements PropertyChangeListener {
    private final int port;
    private final String ip;
    private Socket socket;
    private TUISocket view;
    GameView modelView;
    private String nickname;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

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
        objectInputStream = new ObjectInputStream(inputStream);
        objectOutputStream = new ObjectOutputStream(outputStream);


        setNickname();

        Object object = objectInputStream.readObject();
        String setUp = null;
        if (object instanceof GameView){
            modelView = (GameView) object;
            view = new TUISocket(null,modelView,nickname,socket);
            view.addPropertyChangeListener(this);
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
        }else {
            setUp = (String) object;
        }

        //String setUp = (String) objectInputStream.readObject();
        if (Objects.equals(setUp, "playerNumber")){
            System.out.println("Inserisci il numero di giocatori");
            Message message = new Message(scanner.nextInt());
            sendMessage(message);
        }
        if (Objects.equals(setUp, "NoPlayerNumber")){
            System.out.println("Partita in fase di completamento, aspetto il model");
        }
        /*if (setUp.equals("cantPlay")){
            System.out.println("Il server ha già una partita piena, non puoi giocare");
        }*/
        object = objectInputStream.readObject();
        if (object instanceof GameView){
            modelView = (GameView) object;
        }else {
            System.out.println("Il server ha già una partita piena, non puoi giocare");
        }
        //modelView = (GameView) objectInputStream.readObject();
        //GameController controller = new GameController(modelView);
        view = new TUISocket(null,modelView,nickname,socket);
        view.addPropertyChangeListener(this);

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

    private void setNickname() throws IOException, ClassNotFoundException {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Inserisci nome utente");
            Message message = new Message(scanner.nextLine());
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            String tmp = (String) objectInputStream.readObject();
            if (tmp.equals("ko")){
                System.out.println("Esiste già un giocatore con questo nome utente, si prega di ripetere.");
            }else{
                System.out.println("Nome utente impostato correttamente");
                nickname = message.getNickName();
                break;
            }
        }

    }
    private void sendMessage(Message message) throws IOException {
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
