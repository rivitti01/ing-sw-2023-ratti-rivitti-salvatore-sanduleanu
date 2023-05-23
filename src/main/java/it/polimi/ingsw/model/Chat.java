package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Chat extends Thread implements Serializable {
    private Map<String, String> chat;

    public Chat() {
        this.chat = new HashMap<>();
    }

    public void newMessage(String nickname, String message) {
        this.chat.put(nickname, message);
    }

    public Map<String, String> getChatMap() {
        return this.chat;
    }
}
