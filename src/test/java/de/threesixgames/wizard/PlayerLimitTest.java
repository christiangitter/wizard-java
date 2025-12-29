package de.threesixgames.wizard;

import de.threesixgames.wizard.domain.game.Game;
import de.threesixgames.wizard.domain.game.GameState;
import de.threesixgames.wizard.domain.players.Player;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
class PlayerLimitTest {

    @Test
    void testGameFlowWithThreePlayers_SUCCESS() {
        Game game = new Game();
        Player player1 = new Player(UUID.randomUUID(), "Mike");
        Player player2 = new Player(UUID.randomUUID(), "Niklas");
        Player player3 = new Player(UUID.randomUUID(), "Christian");

        game.join(player1);
        game.join(player2);
        game.join(player3);

        assertEquals(GameState.LOBBY, game.getState());

        game.start();
        assertEquals(GameState.BIDDING, game.getState());

        game.placeBid(player1.id(), 1);
        game.placeBid(player2.id(), 1);
        game.placeBid(player3.id(), 1);

        assertEquals(GameState.PLAYING, game.getState());
    }

    @Test
    void testGameFlowWithTwoPlayers_FAIL() {
        Game game = new Game();
        Player player1 = new Player(UUID.randomUUID(), "Mike");
        Player player2 = new Player(UUID.randomUUID(), "Niklas");

        game.join(player1);
        game.join(player2);

        assertEquals(GameState.LOBBY, game.getState());

        try {
            game.start();
        } catch (IllegalStateException e) {
            assertEquals("Need >= 3 players", e.getMessage());
        }
    }

    @Test
    void testGameFlowWithSixPlayers_FAIL() {
        Game game = new Game();
        Player player1 = new Player(UUID.randomUUID(), "Mike");
        Player player2 = new Player(UUID.randomUUID(), "Niklas");
        Player player3 = new Player(UUID.randomUUID(), "Christian");
        Player player4 = new Player(UUID.randomUUID(), "Anna");
        Player player5 = new Player(UUID.randomUUID(), "Sophie");
        Player player6 = new Player(UUID.randomUUID(), "Lukas");

        game.join(player1);
        game.join(player2);
        game.join(player3);
        game.join(player4);
        game.join(player5);

        try {
            game.join(player6);
        } catch (IllegalStateException e) {
            assertEquals("Max 5", e.getMessage());
        }
    }
}
