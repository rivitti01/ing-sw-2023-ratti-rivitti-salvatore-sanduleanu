package it.polimi.ingsw.distributed.socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CheckConnection implements Runnable {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int time;
    public CheckConnection(ObjectInputStream in, ObjectOutputStream out, int time){
        this.in = in;
        this.out = out;
        this.time = time;
    }

    @Override
    public void run() {
        while (true){
            try {
                if (time != 0) {
                    Thread.sleep(time);
                    ping();
                }
                in.readObject();
                String message = (String) in.readObject();
                if(message.equals("PING")){
                    pong();
                }
                if (message.equals("PONG")) {

                }
            }catch (Exception e){
                System.err.println("Something went wrong with the connection to the server!");
            }
        }
    }
    private void ping(){
        try {
            out.writeObject("PING");
            out.flush();
            out.reset();
        }catch (Exception e){
            System.err.println("Something went wrong with the connection to the server!");
        }
    }
    private void pong(){
        try {
            out.writeObject("PONG");
            out.flush();
            out.reset();
        }catch (Exception e){
            System.err.println("Something went wrong with the connection to the server!");
        }
    }
}
