package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ChatView implements Serializable {
    private List<ChatMessage> chat;
    
    public ChatView(Game model) {
        this.chat = model.getChat().getChat();
    }
    
    public List<ChatMessage> getChat() {
        return this.chat;
    }


}
