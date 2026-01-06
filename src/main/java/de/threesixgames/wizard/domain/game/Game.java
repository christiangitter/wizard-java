package de.threesixgames.wizard.domain.game;

import de.threesixgames.wizard.domain.cards.Card;
import de.threesixgames.wizard.domain.cards.Type;
import de.threesixgames.wizard.domain.players.Player;

import java.util.*;

public class Game {

    private final UUID id;
    private GameState state;

    private final List<Player> players;
    private final Map<UUID, List<Card>> hands;
    private final Map<UUID, Integer> bids;
    private final Map<UUID, Integer> tricksWon;
    private final Map<UUID, Integer> scores;

    private Trick currentTrick;
    private Type trump;
    private UUID currentPlayer;

    private int round;
    private int maxRounds;

    public Game(List<Player> players) {
        this.id = UUID.randomUUID();
        this.players = new ArrayList<>(players);
        this.hands = new HashMap<>();
        this.bids = new HashMap<>();
        this.tricksWon = new HashMap<>();
        this.scores = new HashMap<>();
        this.state = GameState.LOBBY;
        this.trump = Type.NONE;
        this.round = 1;
        this.maxRounds = 1;
    }

    public UUID getId() {
        return id;
    }

    public GameState getState() {
        return state;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Type getTrump() {
        return trump;
    }

    public int getRound() {
        return round;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public Map<UUID, Integer> getScores() {
        return Collections.unmodifiableMap(scores);
    }

    public Map<UUID, Integer> getTricksWon() {
        return Collections.unmodifiableMap(tricksWon);
    }

    public List<Card> getHand(UUID playerId) {
        return hands.getOrDefault(playerId, Collections.emptyList());
    }

    public void join(Player player) {
        if (state != GameState.LOBBY)
            throw new IllegalStateException("Game already started");
        if (players.size() >= 5)
            throw new IllegalStateException("Max 5 players");
        players.add(player);
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void setTrump(Type trump) {
        this.trump = trump;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public void setMaxRounds(int maxRounds) {
        this.maxRounds = maxRounds;
    }

    public void setCurrentPlayer(UUID playerId) {
        this.currentPlayer = playerId;
    }

    public void setHand(UUID playerId, List<Card> hand) {
        hands.put(playerId, new ArrayList<>(hand));
    }

    public void setCurrentTrick(Trick trick) {
        this.currentTrick = trick;
    }

    public void placeBid(UUID playerId, int value) {
        if (state != GameState.BIDDING)
            throw new IllegalStateException("Not in bidding phase");
        bids.put(playerId, value);
    }

    public void playCard(UUID playerId, Card card) {
        if (state != GameState.PLAYING)
            throw new IllegalStateException("Not playing");
        List<Card> hand = hands.get(playerId);
        if (hand == null || !hand.contains(card))
            throw new IllegalStateException("Card not owned");
        currentTrick.playCard(playerId, card);
        hand.remove(card);
    }

    public void addTrick(UUID playerId) {
        tricksWon.put(playerId, tricksWon.getOrDefault(playerId, 0) + 1);
    }

    public void addScore(UUID playerId, int score) {
        scores.put(playerId, scores.getOrDefault(playerId, 0) + score);
    }

    public Trick getCurrentTrick() {
        return currentTrick;
    }

    public UUID getCurrentPlayer() {
        return currentPlayer;
    }

    public Map<UUID, Integer> getBids() {
        return Collections.unmodifiableMap(bids);
    }

    public void clearBids() {
        bids.clear();
    }

    public void resetTricksWon() {
        tricksWon.clear();
    }
}
