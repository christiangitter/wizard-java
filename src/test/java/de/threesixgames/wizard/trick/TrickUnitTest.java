package de.threesixgames.wizard.trick;

import de.threesixgames.wizard.domain.cards.Card;
import de.threesixgames.wizard.domain.cards.Rank;
import de.threesixgames.wizard.domain.cards.Type;
import de.threesixgames.wizard.domain.game.Trick;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class TrickUnitTest {

    @Test
    void highestCardWins() {
        Trick trick = new Trick(UUID.randomUUID());
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        UUID p3 = UUID.randomUUID();
        trick.playCard(p1, new Card(Type.BLUE, Rank.R12));
        trick.playCard(p2, new Card(Type.BLUE, Rank.R4));
        trick.playCard(p3, new Card(Type.RED, Rank.R13));
        UUID winner = trick.resolve(Type.BLUE);

        assertEquals(p1, winner);
    }

    @Test
    void wizardCardWins() {
        Trick trick = new Trick(UUID.randomUUID());
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        UUID p3 = UUID.randomUUID();
        trick.playCard(p1, new Card(Type.BLUE, Rank.R13));
        trick.playCard(p2, new Card(Type.NONE, Rank.WIZARD));
        trick.playCard(p3, new Card(Type.RED, Rank.R13));

        UUID winner = trick.resolve(Type.BLUE);

        assertEquals(p2, winner);
    }

    @Test
    void foolCardisPlayed() {
        Trick trick = new Trick(UUID.randomUUID());
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        UUID p3 = UUID.randomUUID();
        trick.playCard(p1, new Card(Type.BLUE, Rank.R13));
        trick.playCard(p2, new Card(Type.RED, Rank.R1));
        trick.playCard(p3, new Card(Type.NONE, Rank.FOOL));

        UUID winner = trick.resolve(Type.RED);

        assertEquals(p2, winner);
    }

    @Test
    void trumpIsWinning() {
        Trick trick = new Trick(UUID.randomUUID());
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        UUID p3 = UUID.randomUUID();
        trick.playCard(p1, new Card(Type.BLUE, Rank.R13));
        trick.playCard(p2, new Card(Type.RED, Rank.R13));
        trick.playCard(p3, new Card(Type.YELLOW, Rank.R13));

        UUID winner = trick.resolve(Type.YELLOW);

        assertEquals(p3, winner);
    }

    @Test
    void firstWizardIsWinning() {
        Trick trick = new Trick(UUID.randomUUID());
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        UUID p3 = UUID.randomUUID();
        UUID p4 = UUID.randomUUID();
        trick.playCard(p1, new Card(Type.YELLOW, Rank.R13));
        trick.playCard(p2, new Card(Type.NONE, Rank.WIZARD));
        trick.playCard(p3, new Card(Type.NONE, Rank.WIZARD));
        trick.playCard(p4, new Card(Type.NONE, Rank.WIZARD));

        UUID winner = trick.resolve(Type.YELLOW);

        assertEquals(p2, winner);
    }

    @Test
    void onlyOneNormalCardWins() {
        Trick trick = new Trick(UUID.randomUUID());
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        UUID p3 = UUID.randomUUID();
        UUID p4 = UUID.randomUUID();
        trick.playCard(p1, new Card(Type.NONE, Rank.FOOL));
        trick.playCard(p2, new Card(Type.NONE, Rank.FOOL));
        trick.playCard(p3, new Card(Type.RED, Rank.R4));
        trick.playCard(p4, new Card(Type.NONE, Rank.FOOL));

        UUID winner = trick.resolve(Type.YELLOW);

        assertEquals(p3, winner);
    }

}
