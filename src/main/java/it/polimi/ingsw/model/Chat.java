package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat extends Thread implements Serializable {
    private List<String> chat;

    public Chat() {
        this.chat = new ArrayList<>();
    }

    public void newMessage(String nickname, String message) {
        this.chat.add(nickname + ": " + message);
    }

    public List<String> getChat() {
        return this.chat;
    }
}
