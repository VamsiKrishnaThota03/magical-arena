package com.magicalarena;

/**
 * A fighter in the arena. Strength and attack are fixed; health goes down as
 * the player takes damage and never drops below zero.
 */
public final class Player {

    private final String name;
    private final int strength;
    private final int attack;
    private int health;

    /**
     * @throws IllegalArgumentException if the name is blank or any attribute is not positive
     */
    public Player(String name, int health, int strength, int attack) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Player name must not be blank");
        }
        requirePositive("health", health);
        requirePositive("strength", strength);
        requirePositive("attack", attack);
        this.name = name;
        this.health = health;
        this.strength = strength;
        this.attack = attack;
    }

    private static void requirePositive(String attribute, int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Player " + attribute + " must be a positive integer, got " + value);
        }
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getStrength() {
        return strength;
    }

    public int getAttack() {
        return attack;
    }

    public boolean isAlive() {
        return health > 0;
    }

    /** Reduces health by {@code damage} (negative values are ignored), stopping at zero. */
    void takeDamage(int damage) {
        health = Math.max(0, health - Math.max(0, damage));
    }

    @Override
    public String toString() {
        return name + " (health " + health + ", strength " + strength + ", attack " + attack + ")";
    }
}
