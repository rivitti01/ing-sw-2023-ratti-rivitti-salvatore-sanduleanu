package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Chat class represents a chat system in the game.
 * It allows players to send and receive messages both public and private (on sender's choice).
 */
public class Chat extends Thread implements Serializable {
    private List<ChatMessage> chat;

    /**
     * Constructs a new Chat object.
     * Initializes the chat list.
     */
    public Chat() {
        this.chat = new ArrayList<>();
    }

    /**
     * Adds a new message to the chat.
     *
     * @param sender   The sender of the message.
     * @param receiver The receiver of the message.
     * @param message  The content of the message.
     */
    public void newMessage(String sender, String receiver, String message) {
        this.chat.add(new ChatMessage(sender, receiver, message));
    }

    /**
     * Retrieves the list of chat messages.
     *
     * @return The list of chat messages.
     */
    public List<ChatMessage> getChat() {
        return this.chat;
    }
}