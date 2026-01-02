package de.threesixgames.wizard.domain.game.utils;

public class ScoreUtil {
    public static int calculateScore(int predictedTricks, int actualTricks) {
        if (predictedTricks == actualTricks) {
            return 20 + (10 * actualTricks);
        } else {
            return -10 * Math.abs(predictedTricks - actualTricks);
        }
    }
}
