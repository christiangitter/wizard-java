package de.threesixgames.wizard;

import de.threesixgames.wizard.domain.game.Game;
import de.threesixgames.wizard.domain.players.Player;
import de.threesixgames.wizard.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class DealingTest {

    private Game game;
    private Player player1, player2, player3;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        player1 = new Player(UUID.randomUUID(), "Mike");
        player2 = new Player(UUID.randomUUID(), "Niklas");
        player3 = new Player(UUID.randomUUID(), "Christian");
        game = new Game(List.of(player1, player2, player3));
        gameService = new GameService();
    }

    @Test
    void testDealing() {
        gameService.startGame(game);
        assertEquals(1, game.getHand(player1.id()).size());
        assertEquals(1, game.getHand(player2.id()).size());
        assertEquals(1, game.getHand(player3.id()).size());
    }
}
