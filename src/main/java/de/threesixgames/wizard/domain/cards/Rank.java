package de.threesixgames.wizard.domain.cards;

public enum Rank {
    R1(1), R2(2), R3(3), R4(4), R5(5),
    R6(6), R7(7), R8(8), R9(9), R10(10),
    R11(11), R12(12), R13(13),
    FOOL(0),
    WIZARD(100);

    private final int value;

    Rank(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
