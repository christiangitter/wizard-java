package de.threesixgames.wizard;

import de.threesixgames.wizard.domain.game.Game;
import de.threesixgames.wizard.domain.game.GameState;
import de.threesixgames.wizard.domain.players.Player;
import de.threesixgames.wizard.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
class PlayerLimitTest {

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService();
    }

    @Test
    void testGameFlowWithThreePlayers_SUCCESS() {
        Player player1 = new Player(UUID.randomUUID(), "Mike");
        Player player2 = new Player(UUID.randomUUID(), "Niklas");
        Player player3 = new Player(UUID.randomUUID(), "Christian");
        List<Player> players = new ArrayList<>();
        players.add(player1);
        Game game = new Game(players);

        game.join(player2);
        game.join(player3);

        assertEquals(GameState.LOBBY, game.getState());

        gameService.startGame(game);
        assertEquals(GameState.BIDDING, game.getState());

        gameService.placeBid(game, player1.id(), 1);
        gameService.placeBid(game, player2.id(), 1);
        gameService.placeBid(game, player3.id(), 1);

        assertEquals(GameState.PLAYING, game.getState());
    }

    @Test
    void testGameFlowWithTwoPlayers_FAIL() {
        Player player1 = new Player(UUID.randomUUID(), "Mike");
        Player player2 = new Player(UUID.randomUUID(), "Niklas");
        List<Player> players = new ArrayList<>();
        players.add(player1);
        Game game = new Game(players);

        game.join(player2);

        assertEquals(GameState.LOBBY, game.getState());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> gameService.startGame(game));
        assertEquals("Need >= 3 players", ex.getMessage());
    }

    @Test
    void testGameFlowWithSixPlayers_FAIL() {
        Player player1 = new Player(UUID.randomUUID(), "Mike");
        Player player2 = new Player(UUID.randomUUID(), "Niklas");
        Player player3 = new Player(UUID.randomUUID(), "Christian");
        Player player4 = new Player(UUID.randomUUID(), "Anna");
        Player player5 = new Player(UUID.randomUUID(), "Sophie");
        Player player6 = new Player(UUID.randomUUID(), "Lukas");
        List<Player> players = new ArrayList<>();
        players.add(player1);
        Game game = new Game(players);

        game.join(player2);
        game.join(player3);
        game.join(player4);
        game.join(player5);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> game.join(player6));
        assertEquals("Max 5 players", ex.getMessage());
    }
}
