package com.magicalarena;

import java.util.Arrays;

/**
 * Entry point. With no arguments it plays two sample matches. Pass eight
 * arguments to play a custom match:
 *
 * <pre>java -cp out com.magicalarena.MagicalArena NAME HEALTH STRENGTH ATTACK NAME HEALTH STRENGTH ATTACK</pre>
 */
public final class MagicalArena {

    private MagicalArena() {
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            new Match(new Player("Player A", 50, 5, 10), new Player("Player B", 100, 10, 5)).play();
            System.out.println();
            new Match(new Player("Player C", 42, 15, 9), new Player("Player D", 35, 20, 10)).play();
            return;
        }
        if (args.length != 8) {
            usage("Expected 8 arguments, got " + args.length + ".");
            return;
        }
        try {
            Player one = parse(Arrays.copyOfRange(args, 0, 4));
            Player two = parse(Arrays.copyOfRange(args, 4, 8));
            new Match(one, two).play();
        } catch (IllegalArgumentException ex) {
            usage(ex.getMessage());
        }
    }

    private static Player parse(String[] fields) {
        try {
            return new Player(fields[0], Integer.parseInt(fields[1]), Integer.parseInt(fields[2]), Integer.parseInt(fields[3]));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Health, strength and attack must be whole numbers: " + String.join(" ", fields));
        }
    }

    private static void usage(String problem) {
        System.err.println(problem);
        System.err.println("Usage: MagicalArena [NAME HEALTH STRENGTH ATTACK NAME HEALTH STRENGTH ATTACK]");
        System.exit(2);
    }
}
