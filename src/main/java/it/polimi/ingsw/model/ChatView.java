package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The ChatView class represents a view of the chat in the game.
 * It provides a filtered list of chat messages based on the player's perspective,
 * because they may receive private messages.
 */
public class ChatView implements Serializable {
    private List<String> chat;

    /**
     * Constructs a new ChatView object.
     *
     * @param model  The game model containing the chat messages.
     * @param player The player for whom the chat view is created.
     */
    public ChatView(Game model, Player player) {
        List<ChatMessage> modelChat = model.getChat().getChat();
        this.chat = new ArrayList<>();
        for (ChatMessage chatMessage : modelChat) {
            if (chatMessage.getSender().equals(player.getNickname()) || chatMessage.getReceiver().equals(player.getNickname()) || chatMessage.getReceiver().equals("all"))
                this.chat.add(chatMessage.getSender() + " to " + chatMessage.getReceiver() + ": " + chatMessage.getMessage());
        }
    }

    /**
     * Retrieves the filtered chat messages.
     *
     * @return The filtered chat messages.
     */
    public List<String> getChat() {
        return this.chat;
    }


}