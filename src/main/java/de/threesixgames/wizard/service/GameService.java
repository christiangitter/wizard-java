package de.threesixgames.wizard.service;

import de.threesixgames.wizard.domain.cards.Card;
import de.threesixgames.wizard.domain.cards.Type;
import de.threesixgames.wizard.domain.game.DeckBuilder;
import de.threesixgames.wizard.domain.game.Game;
import de.threesixgames.wizard.domain.game.GameState;
import de.threesixgames.wizard.domain.game.Trick;
import de.threesixgames.wizard.domain.game.utils.ScoreUtil;
import de.threesixgames.wizard.domain.players.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class GameService {

    public void startGame(Game game) {
        if (game.getPlayers().size() < 3)
            throw new IllegalStateException("Need >= 3 players");

        int deckSize = 60;
        int maxRounds = deckSize / game.getPlayers().size();
        game.setMaxRounds(maxRounds);
        game.setRound(1);
        game.setState(GameState.DEALING);

        for (Player p : game.getPlayers()) {
            game.setHand(p.id(), new ArrayList<>());
            game.addTrick(p.id());
            game.addScore(p.id(), 0);
        }

        dealCards(game);
        game.setState(GameState.BIDDING);
        game.setCurrentPlayer(game.getPlayers().getFirst().id());
    }

    public void dealCards(Game game) {
        List<Card> deck = DeckBuilder.build();
        Collections.shuffle(deck);

        int cardCount = game.getRound();
        for (Player p : game.getPlayers()) {
            List<Card> hand = new ArrayList<>(deck.subList(0, cardCount));
            game.setHand(p.id(), hand);
            deck.subList(0, cardCount).clear();
        }
        Type trump = deck.isEmpty() ? Type.NONE : deck.getFirst().type();
        game.setTrump(trump);
        game.clearBids();
        game.resetTricksWon();
    }

    public void placeBid(Game game, UUID playerId, int value) {
        game.placeBid(playerId, value);
        if (game.getBids().size() == game.getPlayers().size()) {
            game.setState(GameState.PLAYING);
            game.setCurrentTrick(new Trick(game.getCurrentPlayer()));
        }
    }

    public void playCard(Game game, UUID playerId, Card card) {
        game.playCard(playerId, card);
        if (game.getCurrentTrick().isComplete(game.getPlayers().size())) {
            UUID winner = game.getCurrentTrick().resolve(game.getTrump());
            game.addTrick(winner);
            game.setCurrentTrick(new Trick(winner));

            boolean roundOver = game.getPlayers().stream()
                    .allMatch(p -> game.getHand(p.id()).isEmpty());
            if (roundOver) {
                scoreRound(game);
            }
        }
    }

    public void scoreRound(Game game) {
        for (Player player : game.getPlayers()) {
            int predicted = game.getBids().getOrDefault(player.id(), 0);
            int actual = game.getTricksWon().getOrDefault(player.id(), 0);
            int score = ScoreUtil.calculateScore(predicted, actual);
            game.addScore(player.id(), game.getScores().getOrDefault(player.id(), 0) + score);
        }
        game.setState(GameState.SCORING);

        if (game.getRound() < game.getMaxRounds()) {
            game.setRound(game.getRound() + 1);
            startNextRound(game);
        } else {
            game.setState(GameState.FINISHED);
        }
    }

    public void startNextRound(Game game) {
        game.setState(GameState.DEALING);
        dealCards(game);
        game.setState(GameState.BIDDING);
        game.setCurrentPlayer(game.getPlayers().getFirst().id());
    }
}
