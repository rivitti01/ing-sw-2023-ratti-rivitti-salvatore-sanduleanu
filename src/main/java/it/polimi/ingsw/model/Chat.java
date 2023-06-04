package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat extends Thread implements Serializable {
    private List<ChatMessage> chat;

    public Chat() {
        this.chat = new ArrayList<>();
    }

    public void newMessage(String sender, String receiver, String message) {
        this.chat.add(new ChatMessage(sender, receiver, message));
    }

    public List<ChatMessage> getChat() {
        return this.chat;
    }
}
