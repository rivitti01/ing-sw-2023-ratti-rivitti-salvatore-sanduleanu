package it.polimi.ingsw.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatTest {

    private Chat chat;

    @BeforeEach
    void setUp() {
        chat = new Chat();
    }

    @Test
    void newMessage() {
        chat.newMessage("John", "Alice", "Hello, Alice!");
        List<ChatMessage> chatMessages = chat.getChat();
        Assertions.assertEquals(1, chatMessages.size());

        ChatMessage message = chatMessages.get(0);
        Assertions.assertEquals("John", message.getSender());
        Assertions.assertEquals("Alice", message.getReceiver());
        Assertions.assertEquals("Hello, Alice!", message.getMessage());
    }

    @Test
    void getChat() {
        List<ChatMessage> chatMessages = chat.getChat();
        Assertions.assertEquals(0, chatMessages.size());
    }
}
