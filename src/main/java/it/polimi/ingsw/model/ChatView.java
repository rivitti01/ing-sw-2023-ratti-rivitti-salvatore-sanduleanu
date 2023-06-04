package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatView implements Serializable {
    private List<String> chat;

    public ChatView(Game model, Player player) {
        List<ChatMessage> modelChat = model.getChat().getChat();
        this.chat = new ArrayList<>();
        for(int i=0; i<modelChat.size(); i++){
            ChatMessage chatMessage = modelChat.get(i);
            if(chatMessage.getSender().equals(player.getNickname()) || chatMessage.getReceiver().equals(player.getNickname()) || chatMessage.getReceiver().equals("all"))
                this.chat.add(chatMessage.getSender() + " to " + chatMessage.getReceiver() + ": " +chatMessage.getMessage());
        }
    }

    public List<String> getChat() {
        return this.chat;
    }


}
