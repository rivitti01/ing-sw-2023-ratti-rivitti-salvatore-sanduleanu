package it.polimi.ingsw.model;

public class ChatMessage {

    private String message;
    private Player sender;
    private String receiver;

    public ChatMessage(String message, Player sender, String receiver){
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
    }
}
