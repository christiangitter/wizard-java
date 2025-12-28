package de.threesixgames.wizard.domain.cards;

public record Card(Type type, Rank rank) {
    public boolean isWizard() {
        return rank == Rank.WIZARD;
    }

    public boolean isFool() {
        return rank == Rank.FOOL;
    }
}
