package de.threesixgames.wizard.api.controller;

import de.threesixgames.wizard.data.GameRepository;
import de.threesixgames.wizard.domain.cards.Card;
import de.threesixgames.wizard.domain.cards.Rank;
import de.threesixgames.wizard.domain.cards.Type;
import de.threesixgames.wizard.domain.game.Game;
import de.threesixgames.wizard.domain.players.Player;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameRepository repo;
    private final SimpMessagingTemplate messaging;

    public GameController(GameRepository repo, SimpMessagingTemplate messaging) {
        this.repo = repo;
        this.messaging = messaging;
    }

    @PostMapping("/create")
    public UUID create(@RequestParam String username) {
        Game game = new Game();
        repo.save(game);
        join(game.getId(), username);
        return game.getId();
    }

    @PostMapping("/{id}/join")
    public void join(@PathVariable UUID id, @RequestParam String username) {
        Game game = repo.getGame(id);
        game.join(new Player(UUID.randomUUID(), username));
        repo.save(game);
        messaging.convertAndSend("/topic/" + id + "/joined", username);
    }

    @PostMapping("/{id}/start")
    public void start(@PathVariable UUID id) {
        Game game = repo.getGame(id);
        game.start();
        repo.save(game);
        messaging.convertAndSend("/topic/" + id + "/started", game);
    }

    @PostMapping("/{id}/bid")
    public void bid(@PathVariable UUID id,
                    @RequestParam UUID playerId,
                    @RequestParam int bid) {
        Game game = repo.getGame(id);
        game.placeBid(playerId, bid);
        repo.save(game);
        messaging.convertAndSend("/topic/" + id + "/bid", "");
    }

    @PostMapping("/{id}/play")
    public void play(@PathVariable UUID id,
                     @RequestParam UUID playerId,
                     @RequestParam Type suit,
                     @RequestParam Rank rank) {
        Game game = repo.getGame(id);
        game.playCard(playerId, new Card(suit, rank));
        repo.save(game);
        messaging.convertAndSend("/topic/" + id + "/update", "");
    }
}
