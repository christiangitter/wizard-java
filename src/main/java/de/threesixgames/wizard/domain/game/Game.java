package de.threesixgames.wizard.domain.game;

import de.threesixgames.wizard.domain.cards.Card;
import de.threesixgames.wizard.domain.cards.Type;
import de.threesixgames.wizard.domain.game.utils.ScoreUtil;
import de.threesixgames.wizard.domain.players.Player;

import java.util.*;

public class Game {

    private final UUID id = UUID.randomUUID();
    private GameState state = GameState.LOBBY;

    private final List<Player> players = new ArrayList<>();
    private final Map<UUID, List<Card>> hands = new HashMap<>();
    private final Map<UUID, Integer> bids = new HashMap<>();
    private final Map<UUID, Integer> tricksWon = new HashMap<>();
    private final Map<UUID, Integer> scores = new HashMap<>();

    private Trick currentTrick;
    private Type trump = Type.NONE;
    private UUID currentPlayer;

    private int round = 1;
    private int maxRounds = 1;

    public UUID getId() { return id; }
    public GameState getState() { return state; }

    public void join(Player player) {
        if (state != GameState.LOBBY)
            throw new IllegalStateException("Game already started");

        if (players.size() >= 5)
            throw new IllegalStateException("Max 5");

        players.add(player);
    }

    public void start() {
        if (players.size() < 3)
            throw new IllegalStateException("Need >= 3 players");

        // Calculate max rounds based on deck size and player count
        int deckSize = 60; // Wizard deck: 52 + 4 Wizards + 4 Jesters
        maxRounds = deckSize / players.size();

        state = GameState.DEALING;
        round = 1;

        players.forEach(p -> {
            hands.put(p.id(), new ArrayList<>());
            tricksWon.put(p.id(), 0);
            scores.put(p.id(), 0);
        });

        dealCards();

        state = GameState.BIDDING;
        currentPlayer = players.getFirst().id();
    }

    private void dealCards() {
        List<Card> deck = DeckBuilder.build();
        Collections.shuffle(deck);

        int cardCount = round;

        for (Player p : players) {
            List<Card> hand = deck.subList(0, cardCount);
            hands.get(p.id()).clear();
            hands.get(p.id()).addAll(hand);
            deck.subList(0, cardCount).clear();
        }

        trump = deck.isEmpty() ? Type.NONE : deck.getFirst().type();
        bids.clear();
        tricksWon.replaceAll((k, v) -> 0);
    }

    public void placeBid(UUID playerId, int value) {
        bids.put(playerId, value);

        if (bids.size() == players.size()) {
            state = GameState.PLAYING;
            currentTrick = new Trick(currentPlayer);
        }
    }

    public void playCard(UUID playerId, Card card) {
        if (state != GameState.PLAYING)
            throw new IllegalStateException("Not playing");

        List<Card> hand = hands.get(playerId);
        if (!hand.contains(card))
            throw new IllegalStateException("Card not owned");

        currentTrick.playCard(playerId, card);
        hand.remove(card);

        if (currentTrick.isComplete(players.size())) {
            UUID winner = currentTrick.resolve(trump);
            tricksWon.put(winner, tricksWon.get(winner) + 1);
            currentTrick = new Trick(winner);

            // Check if round is over (all hands empty)
            if (hands.values().stream().allMatch(List::isEmpty)) {
                scoreRound();
            }
        }
    }

    private void scoreRound() {
        for (Player player : players) {
            int predicted = bids.getOrDefault(player.id(), 0);
            int actual = tricksWon.getOrDefault(player.id(), 0);
            int score = ScoreUtil.calculateScore(predicted, actual);
            scores.put(player.id(), scores.getOrDefault(player.id(), 0) + score);
        }
        state = GameState.SCORING;

        // Start next round if possible
        if (round < maxRounds) {
            round++;
            startNextRound();
        } else {
            state = GameState.FINISHED;
        }
    }

    private void startNextRound() {
        state = GameState.DEALING;
        dealCards();
        state = GameState.BIDDING;
        currentPlayer = players.getFirst().id();
    }

    public List<Card> getHand(UUID playerId) {
        return hands.getOrDefault(playerId, Collections.emptyList());
    }

    public Map<UUID, Integer> getTricksWon() {
        return Collections.unmodifiableMap(tricksWon);
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Type getTrump() {
        return trump;
    }

    public Map<UUID, Integer> getScores() {
        return Collections.unmodifiableMap(scores);
    }
}
