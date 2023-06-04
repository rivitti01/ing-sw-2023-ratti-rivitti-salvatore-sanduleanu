package it.polimi.ingsw.model;

public class ChatMessage {

    private String message;
    private String sender;
    private String receiver;

    public ChatMessage(String sender, String receiver, String message){
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
    public String getReceiver(){
        return this.receiver;
    }
}
