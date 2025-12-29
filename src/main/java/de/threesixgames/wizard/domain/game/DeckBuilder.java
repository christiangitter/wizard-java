package de.threesixgames.wizard.domain.game;

import de.threesixgames.wizard.domain.cards.Card;
import de.threesixgames.wizard.domain.cards.Rank;
import de.threesixgames.wizard.domain.cards.Type;

import java.util.ArrayList;
import java.util.List;

public class DeckBuilder {

    public static List<Card> build() {
        List<Card> deck = new ArrayList<>();

        List<Type> suits = List.of(Type.BLUE, Type.RED, Type.GREEN, Type.YELLOW);

        for (Type suit : suits) {
            for (int i = 1; i <= 13; i++)
                deck.add(new Card(suit, Rank.values()[i]));
        }

        for (int i = 0; i < 4; i++)
            deck.add(new Card(Type.NONE, Rank.WIZARD));

        for (int i = 0; i < 4; i++)
            deck.add(new Card(Type.NONE, Rank.FOOL));

        return deck;
    }
}
