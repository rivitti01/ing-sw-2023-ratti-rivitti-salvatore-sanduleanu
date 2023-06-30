package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.First;
import it.polimi.ingsw.distributed.rmi.ServerRMIImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatViewTest {
    private Game game;
    private List<Player> players;
    private ServerRMIImpl server;
    private First first;
    private GameController gameController;

    @BeforeEach
    public void setUp() throws RemoteException {
        game = new Game();
        gameController = new GameController(game);
        first = new First();
        players = new ArrayList<>();
        server = new ServerRMIImpl(game, gameController, first);
        game.addModelListener(server);
        players.add(new Player("John"));
        players.add(new Player("Alice"));
        players.add(new Player("Bob"));
        game.startGame(3, players);
    }

    @Test
    public void testGetChat() {
        // Set up the game chat with some messages
        game.getChat().newMessage("Alice", "John", "Hello, John!");
        game.getChat().newMessage("John", "Alice", "Hi, Alice!");
        game.getChat().newMessage("Bob", "John", "Hey, John!");

        // Create a ChatView instance
        ChatView chatView = new ChatView(game, players.get(0));

        // Verify that only relevant chat messages are included
        List<String> expectedChat = Arrays.asList(
                "Alice to John: Hello, John!",
                "John to Alice: Hi, Alice!",
                "Bob to John: Hey, John!"
        );
        System.out.print(chatView.getChat());
        Assertions.assertEquals(expectedChat, chatView.getChat());
    }

    @Test
    public void testGetChat_NoMessages() {
        ChatView chatView = new ChatView(game, players.get(0));
        Assertions.assertEquals(new ArrayList<>(), chatView.getChat());
    }
}