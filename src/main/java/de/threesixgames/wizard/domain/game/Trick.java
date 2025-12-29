package de.threesixgames.wizard.domain.game;

import de.threesixgames.wizard.domain.cards.Card;
import de.threesixgames.wizard.domain.cards.Type;

import java.util.*;

public class Trick {

    private final UUID leadPlayerId;
    private Type leadType;
    private final List<Entry> cards = new ArrayList<>();
    private UUID winner;

    public record Entry(UUID playerId, Card card) {
    }

    public Trick(UUID leadPlayerId) {
        this.leadPlayerId = leadPlayerId;
    }

    public void playCard(UUID playerId, Card card) {
        cards.add(new Entry(playerId, card));
        if (cards.size() == 1 && !card.isWizard() && !card.isFool()) {
            leadType = card.type();
        }
    }

    public boolean isComplete(int players) {
        return cards.size() == players;
    }

    public UUID resolve(Type trump) {

        // 1. Check if any card played is a Wizard; if so, the first Wizard wins
        Optional<Entry> wizard = cards.stream()
                .filter(c -> c.card().isWizard())
                .findFirst();

        if (wizard.isPresent())
            return winner = wizard.get().playerId();

        // 2. If all cards are Fools, the first player wins
        boolean allFools = cards.stream().allMatch(c -> c.card().isFool());
        if (allFools)
            return winner = cards.get(0).playerId();

        // 3. Check for trump cards - highest trump wins
        Optional<Entry> trumpWinner = cards.stream()
                .filter(c -> c.card().type() == trump)
                .max(Comparator.comparingInt(c -> c.card().rank().getValue()));

        if (trumpWinner.isPresent())
            return winner = trumpWinner.get().playerId();

        // 4. Otherwise, highest card of the lead type wins
        Optional<Entry> leadWinner = cards.stream()
                .filter(c -> c.card().type() == leadType)
                .max(Comparator.comparingInt(c -> c.card().rank().getValue()));

        if (leadWinner.isPresent())
            return winner = leadWinner.get().playerId();

        // 5. Fallback: first non-FOOL card wins
        return winner = cards.stream()
                .filter(c -> !c.card().isFool())
                .findFirst()
                .orElse(cards.get(0)) // fallback: all FOOLs, already handled above, but for safety
                .playerId();
    }
}
