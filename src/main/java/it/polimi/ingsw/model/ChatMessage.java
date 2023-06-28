package it.polimi.ingsw.model;

/**
 * The ChatMessage class represents a message in the chat.
 * It contains the sender, receiver, and message content.
 */
public class ChatMessage {

    private String message;
    private String sender;
    private String receiver;

    /**
     * Constructs a new ChatMessage object.
     *
     * @param sender   The sender of the message.
     * @param receiver The receiver of the message.
     * @param message  The content of the message.
     */
    public ChatMessage(String sender, String receiver, String message){
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Retrieves the content of the message.
     *
     * @return The content of the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the content of the message.
     *
     * @param message The content of the message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Retrieves the sender of the message.
     *
     * @return The sender of the message.
     */
    public String getSender() {
        return sender;
    }

    /**
     * Retrieves the receiver of the message.
     *
     * @return The receiver of the message.
     */
    public String getReceiver(){
        return this.receiver;
    }
}