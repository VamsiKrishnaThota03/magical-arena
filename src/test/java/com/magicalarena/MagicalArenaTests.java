public class ManualTests
{
    public static void main(String[] args)
    {
        // Run all test cases
        testPlayerInitialization();
        testDiceRoll();
        testPlayerAttack();
        testMatchInitialization();
        testMatchExecution();
        testMatchWithDifferentPlayerHealth();
    }

    /**
     * Asserts a condition and prints the result of the test.
     *
     * --->condition - Boolean condition to assert
     * --->message   - Test message for context
     */
    private static void assertCondition(boolean condition, String message)
    {
        if (condition) {
            System.out.println("[PASS] " + message);
        } else {
            System.out.println("[FAIL] " + message);
        }
    }


    /**
     * Test: Verify correct initialization of Player object.
     */
    private static void testPlayerInitialization()
    {
        Player player = new Player("Player A", 50, 5, 10);

        // Assertions for Player attributes
        assertCondition("Player A".equals(player.name), "Player name should be 'Player A'");
        assertCondition(player.health == 50, "Player health should be 50");
        assertCondition(player.strength == 5, "Player strength should be 5");
        assertCondition(player.attack == 10, "Player attack should be 10");
    }

    /**
     * Test: Ensure the Dice class generates valid rolls between 1 and 6.
     */
    private static void testDiceRoll()
    {
        boolean allRollsValid = true;

        // Roll the dice 100 times to check bounds
        for (int i = 0; i < 100; i++)
        {
            int roll = Dice.roll();
            if (roll < 1 || roll > 6)
            {
                allRollsValid = false;
                break;
            }
        }

        assertCondition(allRollsValid, "Dice roll should always be between 1 and 6");
    }


    /**
     * Test: Simulate a player attacking another and verify health deduction logic.
     */
    private static void testPlayerAttack()
    {
        Player attacker = new Player("Player A", 50, 5, 10);
        Player defender = new Player("Player B", 50, 5, 10);

        // Simulate an attack
        attacker.attackOpponent(defender);

        // Check if defender's health decreased or remained unchanged
        assertCondition(defender.health <= 50, "Defender's health should decrease or remain unchanged if the attack was fully defended");
    }

    
    /**
     * Test: Verify correct initialization of Match object.
     */
    private static void testMatchInitialization()
    {
        Player playerA = new Player("Player A", 50, 5, 10);
        Player playerB = new Player("Player B", 50, 5, 10);

        Match match = new Match(playerA, playerB);

        // Ensure the Match object is initialized
        assertCondition(match != null, "Match should be successfully initialized");
    }

    /**
     * Test: Simulate a match and verify if one player's health reaches 0.
     */
    private static void testMatchExecution()
    {
        Player playerA = new Player("Player A", 50, 5, 10);
        Player playerB = new Player("Player B", 50, 5, 10);

        Match match = new Match(playerA, playerB);

        // Start the match
        match.start();

        // Verify if any player's health is reduced to 0 or below
        assertCondition(playerA.health <= 0 || playerB.health <= 0,"One player's health should reach 0 or below after the match ends");
    }

    /**
     * Test: Simulate a match where players have different health levels and verify the result.
     */
    private static void testMatchWithDifferentPlayerHealth()
    {
        Player playerA = new Player("Player A", 50, 5, 10);
        Player playerB = new Player("Player B", 100, 5, 10);

        Match match = new Match(playerA, playerB);

        // Start the match
        match.start();

        // Verify if any player's health is reduced to 0 or below
        assertCondition(playerA.health <= 0 || playerB.health <= 0,"One player's health should reach 0 or below after the match ends");
    }
}
