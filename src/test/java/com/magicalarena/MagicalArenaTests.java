package com.magicalarena;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Dependency-free test runner. Each test is a method reference; a failed
 * assertion or unexpected exception marks the test as failed and the process
 * exits with status 1 so it can be used in scripts and CI.
 */
public final class MagicalArenaTests {

    private interface Test {
        void run() throws Exception;
    }

    private record Case(String name, Test test) {
    }

    private static final List<Case> CASES = List.of(
            new Case("player stores its attributes", MagicalArenaTests::playerStoresAttributes),
            new Case("player rejects blank name and non-positive attributes", MagicalArenaTests::playerValidatesInput),
            new Case("health never drops below zero", MagicalArenaTests::healthClampsAtZero),
            new Case("six-sided die stays within 1..6 and hits every face", MagicalArenaTests::diceBounds),
            new Case("seeded die is reproducible", MagicalArenaTests::seededDiceReproducible),
            new Case("damage is attack*roll minus strength*roll", MagicalArenaTests::damageFormula),
            new Case("fully defended attack deals no damage", MagicalArenaTests::fullyDefendedAttack),
            new Case("lower-health player attacks first", MagicalArenaTests::lowerHealthStarts),
            new Case("player one starts on equal health", MagicalArenaTests::tieGoesToPlayerOne),
            new Case("README example: A(50,5,10) vs B(100,10,5)", MagicalArenaTests::specExampleFirstTurn),
            new Case("match ends with exactly one survivor and returns the winner", MagicalArenaTests::matchHasWinner),
            new Case("players alternate turns", MagicalArenaTests::turnsAlternate),
            new Case("match rejects a player fighting themselves", MagicalArenaTests::rejectsSamePlayer),
            new Case("match rejects a fight nobody can win", MagicalArenaTests::rejectsEndlessMatch),
            new Case("die values outside 1..6 are rejected", MagicalArenaTests::rejectsInvalidRoll),
            new Case("seeded matches are deterministic", MagicalArenaTests::seededMatchDeterministic),
            new Case("1,000 random matches all terminate", MagicalArenaTests::randomMatchesTerminate),
            new Case("match log reports the winner", MagicalArenaTests::logReportsWinner)
    );

    public static void main(String[] args) {
        int failed = 0;
        for (Case c : CASES) {
            try {
                c.test().run();
                System.out.println("[PASS] " + c.name());
            } catch (Throwable t) {
                failed++;
                System.out.println("[FAIL] " + c.name() + ": " + t);
            }
        }
        System.out.printf("%n%d tests, %d passed, %d failed%n", CASES.size(), CASES.size() - failed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    /* ------------------------------------------------------------ helpers */

    /** A die that returns the given values in order, then fails if asked for more. */
    private static Dice scripted(int... rolls) {
        int[] index = {0};
        return () -> {
            if (index[0] >= rolls.length) {
                throw new IllegalStateException("scripted die ran out of rolls");
            }
            return rolls[index[0]++];
        };
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void checkEquals(Object expected, Object actual, String what) {
        if (!expected.equals(actual)) {
            throw new AssertionError(what + ": expected " + expected + " but was " + actual);
        }
    }

    private static void checkThrows(Class<? extends Throwable> type, Test body) {
        try {
            body.run();
        } catch (Throwable t) {
            if (type.isInstance(t)) {
                return;
            }
            throw new AssertionError("expected " + type.getSimpleName() + " but got " + t);
        }
        throw new AssertionError("expected " + type.getSimpleName() + " to be thrown");
    }

    private static Match silent(Player a, Player b, Dice dice) {
        return new Match(a, b, dice, null);
    }

    /* -------------------------------------------------------------- tests */

    private static void playerStoresAttributes() {
        Player p = new Player("Player A", 50, 5, 10);
        checkEquals("Player A", p.getName(), "name");
        checkEquals(50, p.getHealth(), "health");
        checkEquals(5, p.getStrength(), "strength");
        checkEquals(10, p.getAttack(), "attack");
        check(p.isAlive(), "new player should be alive");
    }

    private static void playerValidatesInput() {
        checkThrows(IllegalArgumentException.class, () -> new Player(" ", 10, 1, 1));
        checkThrows(IllegalArgumentException.class, () -> new Player(null, 10, 1, 1));
        checkThrows(IllegalArgumentException.class, () -> new Player("X", 0, 1, 1));
        checkThrows(IllegalArgumentException.class, () -> new Player("X", 10, -1, 1));
        checkThrows(IllegalArgumentException.class, () -> new Player("X", 10, 1, 0));
    }

    private static void healthClampsAtZero() {
        Player p = new Player("X", 10, 1, 1);
        p.takeDamage(25);
        checkEquals(0, p.getHealth(), "health after overkill");
        check(!p.isAlive(), "player should be defeated");
        p.takeDamage(-5);
        checkEquals(0, p.getHealth(), "negative damage must not heal");
    }

    private static void diceBounds() {
        Dice dice = Dice.sixSided(new Random(42));
        boolean[] seen = new boolean[Dice.SIDES + 1];
        for (int i = 0; i < 10_000; i++) {
            int roll = dice.roll();
            check(roll >= 1 && roll <= Dice.SIDES, "roll out of range: " + roll);
            seen[roll] = true;
        }
        for (int face = 1; face <= Dice.SIDES; face++) {
            check(seen[face], "face " + face + " never rolled");
        }
    }

    private static void seededDiceReproducible() {
        Dice a = Dice.sixSided(new Random(7));
        Dice b = Dice.sixSided(new Random(7));
        for (int i = 0; i < 100; i++) {
            checkEquals(a.roll(), b.roll(), "roll " + i);
        }
    }

    private static void damageFormula() {
        Player attacker = new Player("A", 50, 5, 10);
        Player defender = new Player("B", 100, 5, 10);
        Match match = silent(attacker, defender, scripted(5, 2));
        Match.Turn turn = match.attack(attacker, defender);
        checkEquals(50, turn.attackDamage(), "attack damage (10 x 5)");
        checkEquals(10, turn.defence(), "defence (5 x 2)");
        checkEquals(40, turn.damage(), "damage");
        checkEquals(60, defender.getHealth(), "defender health");
    }

    private static void fullyDefendedAttack() {
        Player attacker = new Player("A", 50, 5, 2);
        Player defender = new Player("B", 50, 10, 10);
        Match match = silent(attacker, defender, scripted(1, 6));
        Match.Turn turn = match.attack(attacker, defender);
        checkEquals(0, turn.damage(), "damage");
        checkEquals(50, defender.getHealth(), "defender health unchanged");
    }

    private static void lowerHealthStarts() {
        Player a = new Player("A", 100, 5, 10);
        Player b = new Player("B", 50, 5, 10);
        checkEquals("B", silent(a, b, Dice.sixSided()).firstAttacker().getName(), "first attacker");
    }

    private static void tieGoesToPlayerOne() {
        Player a = new Player("A", 50, 5, 10);
        Player b = new Player("B", 50, 5, 10);
        checkEquals("A", silent(a, b, Dice.sixSided()).firstAttacker().getName(), "first attacker");
    }

    private static void specExampleFirstTurn() {
        // A attacks first (lower health), rolls 5 -> 50 damage; B defends, rolls 2 -> 20; B loses 30.
        Player a = new Player("Player A", 50, 5, 10);
        Player b = new Player("Player B", 100, 10, 5);
        Match match = silent(a, b, scripted(5, 2));
        checkEquals(a, match.firstAttacker(), "first attacker");
        match.attack(match.firstAttacker(), b);
        checkEquals(70, b.getHealth(), "Player B health");
    }

    private static void matchHasWinner() {
        Player a = new Player("A", 30, 5, 10);
        Player b = new Player("B", 40, 5, 10);
        // A attacks first: 60-5=55 (B dies) ... B is defeated on turn one.
        Player winner = silent(a, b, scripted(6, 1)).play();
        checkEquals(a, winner, "winner");
        checkEquals(0, b.getHealth(), "loser health");
        check(a.isAlive(), "winner should be alive");
    }

    private static void turnsAlternate() {
        Player a = new Player("A", 10, 1, 1);
        Player b = new Player("B", 20, 1, 1);
        // Turn 1: A hits B for 6-1=5 (B 15). Turn 2: B hits A for 6-1=5 (A 5).
        // Turn 3: A hits B for 5 (B 10). Turn 4: B hits A for 5 (A 0) -> B wins.
        Match match = silent(a, b, scripted(6, 1, 6, 1, 6, 1, 6, 1));
        Player winner = match.play();
        checkEquals(b, winner, "winner");
        checkEquals(4, match.turnsPlayed(), "turns");
        checkEquals(10, b.getHealth(), "winner health");
    }

    private static void rejectsSamePlayer() {
        Player a = new Player("A", 10, 1, 1);
        checkThrows(IllegalArgumentException.class, () -> new Match(a, a));
        checkThrows(NullPointerException.class, () -> new Match(a, null));
    }

    private static void rejectsEndlessMatch() {
        // Best attack 1*6=6 never beats worst defence 10*1=10, for either side.
        Player a = new Player("A", 10, 10, 1);
        Player b = new Player("B", 10, 10, 1);
        checkThrows(IllegalArgumentException.class, () -> new Match(a, b));
    }

    private static void rejectsInvalidRoll() {
        Player a = new Player("A", 10, 1, 1);
        Player b = new Player("B", 10, 1, 1);
        checkThrows(IllegalStateException.class, () -> silent(a, b, scripted(7, 1)).play());
        checkThrows(IllegalStateException.class, () -> silent(a, b, scripted(0, 1)).play());
    }

    private static void seededMatchDeterministic() {
        String first = transcript(123);
        String second = transcript(123);
        checkEquals(first, second, "transcript with the same seed");
    }

    private static String transcript(long seed) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        new Match(new Player("A", 50, 5, 10), new Player("B", 100, 10, 5),
                Dice.sixSided(new Random(seed)), new PrintStream(buffer, true)).play();
        return buffer.toString();
    }

    private static void randomMatchesTerminate() {
        Random random = new Random(2024);
        for (int i = 0; i < 1_000; i++) {
            Player a = new Player("A", 1 + random.nextInt(200), 1 + random.nextInt(20), 1 + random.nextInt(20));
            Player b = new Player("B", 1 + random.nextInt(200), 1 + random.nextInt(20), 1 + random.nextInt(20));
            if (!Match.canEverDamage(a, b) && !Match.canEverDamage(b, a)) {
                continue;
            }
            Match match = silent(a, b, Dice.sixSided(random));
            Player winner = match.play();
            Player loser = winner == a ? b : a;
            check(winner.isAlive() && !loser.isAlive(), "exactly one survivor in match " + i + ": " + Arrays.asList(a, b));
            check(match.turnsPlayed() < 100_000, "match " + i + " took too long");
        }
    }

    private static void logReportsWinner() {
        String log = transcript(1);
        check(log.startsWith("The match begins"), "log should start with the match header");
        check(log.contains(" is defeated! ") && log.contains(" wins after "), "log should announce the winner");
        List<String> lines = new ArrayList<>(log.lines().toList());
        check(lines.size() >= 3, "log should include at least one turn");
    }
}
