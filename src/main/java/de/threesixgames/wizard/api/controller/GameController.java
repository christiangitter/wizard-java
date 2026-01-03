package de.threesixgames.wizard.api.controller;

import de.threesixgames.wizard.api.GameStartResponse;
import de.threesixgames.wizard.api.GameStateResponse;
import de.threesixgames.wizard.data.GameRepository;
import de.threesixgames.wizard.domain.cards.Card;
import de.threesixgames.wizard.domain.cards.Rank;
import de.threesixgames.wizard.domain.cards.Type;
import de.threesixgames.wizard.domain.game.Game;
import de.threesixgames.wizard.domain.players.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameRepository repo;
    private final SimpMessagingTemplate messaging;
    private final Logger LOG = LoggerFactory.getLogger(GameController.class);

    public GameController(GameRepository repo, SimpMessagingTemplate messaging) {
        this.repo = repo;
        this.messaging = messaging;
    }

    @PostMapping("/create")
    public Map<String, Object> create(@RequestParam String username) {
        Game game = new Game();
        repo.save(game);
        Player player = new Player(UUID.randomUUID(), username);
        game.join(player);
        repo.save(game);
        LOG.info("Created game: {} by PlayerID: {}", game, player.id());
        return Map.of("gameId", game.getId(), "playerId", player.id());
    }

    @PostMapping("/{id}/join")
    public Map<String, Object> join(@PathVariable UUID id, @RequestParam String username) {
        Game game = repo.getGame(id);
        Player player = new Player(UUID.randomUUID(), username);
        game.join(player);
        repo.save(game);
        messaging.convertAndSend("/topic/" + id + "/joined", username);
        LOG.info("Joined game: {}, Player: {}", game, player.id());
        return Map.of("gameId", id, "playerId", player.id());
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<GameStartResponse> start(@PathVariable UUID id) {
        Game game = repo.getGame(id);
        game.start();
        repo.save(game);

        Map<UUID, Object> handsInfo = new HashMap<>();
        for (Player player : game.getPlayers()) {
            handsInfo.put(player.id(), game.getHand(player.id()));
        }

        Object trump = game.getTrump();

        messaging.convertAndSend("/topic/" + id + "/started", game);

        return ResponseEntity.ok(new GameStartResponse(handsInfo, trump));
    }

    @PostMapping("/{id}/bid")
    public void bid(@PathVariable UUID id,
                    @RequestParam UUID playerId,
                    @RequestParam int bid) {
        Game game = repo.getGame(id);
        game.placeBid(playerId, bid);
        repo.save(game);
        LOG.info("Bid: {}, Player: {}", bid, playerId);
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
        LOG.info("Played card: {}, Player: {}", game, playerId);
        messaging.convertAndSend("/topic/" + id + "/update", "");
    }

    @GetMapping("/{id}/state")
    public GameStateResponse getState(@PathVariable UUID id) {
        Game game = repo.getGame(id);
        Map<UUID, List<Card>> handsInfo = new HashMap<>();
        for (Player player : game.getPlayers()) {
            handsInfo.put(player.id(), game.getHand(player.id()));
        }
        return new GameStateResponse(game, handsInfo);
    }

    @GetMapping("/{id}/scores")
    public Map<UUID, Integer> getScores(@PathVariable UUID id) {
        Game game = repo.getGame(id);
        return game.getScores();
    }
}


