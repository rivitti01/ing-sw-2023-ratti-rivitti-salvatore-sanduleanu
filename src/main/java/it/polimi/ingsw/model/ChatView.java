package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.Map;

public class ChatView implements Serializable {
    private Map<String, String> chat;
    
    public ChatView(Game model) {
        this.chat = model.getChat().getChatMap();
    }
    
    public Map<String, String> getChat() {
        return this.chat;
    }


}
