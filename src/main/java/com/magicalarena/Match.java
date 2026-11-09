package com.magicalarena;

import java.io.PrintStream;
import java.util.Objects;

/**
 * A fight between two players. The player with lower health attacks first
 * (player one on a tie), then the players alternate turns until one of them
 * reaches zero health.
 *
 * <p>On every turn the attacker rolls the die and multiplies it by their attack,
 * the defender rolls and multiplies it by their strength, and the defender
 * loses {@code max(0, attackDamage - defenceValue)} health.
 */
public final class Match {

    /** Outcome of a single attack. */
    public record Turn(Player attacker, Player defender, int attackRoll, int defendRoll,
                       int attackDamage, int defence, int damage) {
    }

    private final Player first;
    private final Player second;
    private final Dice dice;
    private final PrintStream out;
    private int turns;

    public Match(Player playerOne, Player playerTwo) {
        this(playerOne, playerTwo, Dice.sixSided(), System.out);
    }

    /**
     * @param dice die used for every roll
     * @param out  where the play-by-play is written; pass {@code null} to run silently
     * @throws IllegalArgumentException if either player is missing, both are the same object,
     *                                  either is already defeated, or neither can ever damage the other
     */
    public Match(Player playerOne, Player playerTwo, Dice dice, PrintStream out) {
        Objects.requireNonNull(playerOne, "playerOne");
        Objects.requireNonNull(playerTwo, "playerTwo");
        if (playerOne == playerTwo) {
            throw new IllegalArgumentException("A player cannot fight themselves");
        }
        if (!playerOne.isAlive() || !playerTwo.isAlive()) {
            throw new IllegalArgumentException("Both players must have health left to start a match");
        }
        if (!canEverDamage(playerOne, playerTwo) && !canEverDamage(playerTwo, playerOne)) {
            throw new IllegalArgumentException("Neither player can ever damage the other, so the match would never end");
        }
        this.dice = Objects.requireNonNull(dice, "dice");
        this.out = out;
        boolean oneStarts = playerOne.getHealth() <= playerTwo.getHealth();
        this.first = oneStarts ? playerOne : playerTwo;
        this.second = oneStarts ? playerTwo : playerOne;
    }

    /** True if the best attack roll can beat the worst defence roll. */
    static boolean canEverDamage(Player attacker, Player defender) {
        return attacker.getAttack() * Dice.SIDES > defender.getStrength();
    }

    /** The player who attacks on the first turn. */
    public Player firstAttacker() {
        return first;
    }

    /** Number of turns played so far. */
    public int turnsPlayed() {
        return turns;
    }

    /**
     * Plays the match to the end.
     *
     * @return the winner
     */
    public Player play() {
        log("The match begins: " + first + " vs " + second);
        Player attacker = first;
        Player defender = second;
        while (true) {
            Turn turn = attack(attacker, defender);
            log(String.format("%s rolls %d (%d attack) vs %s rolls %d (%d defence): %d damage. %s's health is now %d.",
                    attacker.getName(), turn.attackRoll(), turn.attackDamage(),
                    defender.getName(), turn.defendRoll(), turn.defence(),
                    turn.damage(), defender.getName(), defender.getHealth()));
            if (!defender.isAlive()) {
                log(defender.getName() + " is defeated! " + attacker.getName() + " wins after " + turns + " turns.");
                return attacker;
            }
            Player swap = attacker;
            attacker = defender;
            defender = swap;
        }
    }

    /** Resolves one attack and applies the damage to the defender. */
    Turn attack(Player attacker, Player defender) {
        int attackRoll = checkedRoll();
        int defendRoll = checkedRoll();
        int attackDamage = attacker.getAttack() * attackRoll;
        int defence = defender.getStrength() * defendRoll;
        int damage = Math.max(0, attackDamage - defence);
        defender.takeDamage(damage);
        turns++;
        return new Turn(attacker, defender, attackRoll, defendRoll, attackDamage, defence, damage);
    }

    private int checkedRoll() {
        int roll = dice.roll();
        if (roll < 1 || roll > Dice.SIDES) {
            throw new IllegalStateException("Die returned " + roll + ", expected 1.." + Dice.SIDES);
        }
        return roll;
    }

    private void log(String line) {
        if (out != null) {
            out.println(line);
        }
    }
}
