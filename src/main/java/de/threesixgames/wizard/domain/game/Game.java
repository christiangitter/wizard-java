package de.threesixgames.wizard.domain.game;

import de.threesixgames.wizard.domain.cards.Card;
import de.threesixgames.wizard.domain.cards.Type;
import de.threesixgames.wizard.domain.players.Player;

import java.util.*;

public class Game {

    private final UUID id = UUID.randomUUID();
    private GameState state = GameState.LOBBY;

    private final List<Player> players = new ArrayList<>();
    private final Map<UUID, List<Card>> hands = new HashMap<>();
    private final Map<UUID, Integer> bids = new HashMap<>();
    private final Map<UUID, Integer> tricksWon = new HashMap<>();

    private Trick currentTrick;
    private Type trump = Type.NONE;
    private UUID currentPlayer;

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

        state = GameState.DEALING;

        players.forEach(p -> {
            hands.put(p.id(), new ArrayList<>());
            tricksWon.put(p.id(), 0);
        });

        dealCards();

        state = GameState.BIDDING;
        currentPlayer = players.get(0).id();
    }

    private void dealCards() {
        List<Card> deck = DeckBuilder.build();
        Collections.shuffle(deck);

        int cardCount = 1;

        for (Player p : players) {
            List<Card> hand = deck.subList(0, cardCount);
            hands.get(p.id()).addAll(hand);
            deck.removeAll(hand);
        }

        trump = deck.getFirst().type();
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
        }
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
}

