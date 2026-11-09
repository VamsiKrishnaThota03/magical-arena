package com.magicalarena;

import java.util.Random;

/**
 * A source of die rolls. The game uses a fair six-sided die; tests can plug in
 * a scripted implementation to make matches deterministic.
 */
@FunctionalInterface
public interface Dice {

    /** Number of faces on the standard die. */
    int SIDES = 6;

    /** Returns the next roll, a value between 1 and {@link #SIDES} inclusive. */
    int roll();

    /** A fair six-sided die backed by a fresh {@link Random}. */
    static Dice sixSided() {
        return sixSided(new Random());
    }

    /** A fair six-sided die backed by the given random source (use a seed for reproducible runs). */
    static Dice sixSided(Random random) {
        return () -> random.nextInt(SIDES) + 1;
    }
}
