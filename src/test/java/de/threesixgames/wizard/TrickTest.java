package de.threesixgames.wizard;

import de.threesixgames.wizard.domain.cards.Card;
import de.threesixgames.wizard.domain.game.Game;
import de.threesixgames.wizard.domain.players.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class TrickTest {

    private Game game;
    private Player player1, player2, player3;

    @BeforeEach
    void setUp() {
        game = new Game();
        player1 = new Player(UUID.randomUUID(), "Mike");
        player2 = new Player(UUID.randomUUID(), "Niklas");
        player3 = new Player(UUID.randomUUID(), "Christian");

        game.join(player1);
        game.join(player2);
        game.join(player3);

    }

    @Test
    void testTrickFunctionality() {
        game.start();
        // Place bids to move to PLAYING state
        game.placeBid(player1.id(), 0);
        game.placeBid(player2.id(), 0);
        game.placeBid(player3.id(), 0);

        // Play one card per player
        Card card1 = game.getHand(player1.id()).getFirst();
        Card card2 = game.getHand(player2.id()).getFirst();
        Card card3 = game.getHand(player3.id()).getFirst();

        game.playCard(player1.id(), card1);
        game.playCard(player2.id(), card2);
        game.playCard(player3.id(), card3);

        // Verify that the trick is completed and a winner is determined
        int tricks = game.getTricksWon().values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(1, tricks);
    }
}
