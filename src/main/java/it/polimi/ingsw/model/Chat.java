package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Chat extends Thread implements Serializable {
    private List<ChatMessage> chat;

    public Chat() {
        this.chat = new ArrayList<>();
    }

    public void newMessage(String message, Player sender, String receiver) {
        this.chat.add(new ChatMessage(message, sender, receiver));
    }

    public List<ChatMessage> getChat() {
        return this.chat;
    }
}
