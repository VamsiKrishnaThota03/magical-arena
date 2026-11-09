import java.util.*;

public class Match
{
    private final List<Player> players;
    private int currentTurn; // To alternate between players easily

    // Constructor to initialize the two players for the match
    public Match(Player player1, Player player2)
    {
        this.players = Arrays.asList(player1, player2);
        this.currentTurn = (player1.health < player2.health) ? 0 : 1; // The player with lower health attacks first
    }

    // Method to start the match
    public void start()
    {
        System.out.println("The match begins!");

        // Loop until one player's health reaches 0 or below
        while (true)
        {
            // Get the current attacker and defender
            Player attacker = players.get(currentTurn);
            Player defender = players.get(1 - currentTurn); // The other player

            // Perform the attack
            performAttack(attacker, defender);

            // Check if the defender is defeated
            if (defender.health <= 0)
            {
                System.out.println(defender.name + " is defeated!");
                break;
            }

            // Alternate turns (modulo is used to toggle between 0 and 1)
            currentTurn = 1 - currentTurn;
        }
    }

    // Handle the attack logic separately
    private void performAttack(Player attacker, Player defender)
    {
        // Player rolls their attack and defense dice
        int attackRoll = Dice.roll();
        int defendRoll = Dice.roll();
        
        // System.out.println(attackRoll);
        // System.out.println(defendRoll);
        // Calculate the attack and defense damage
        int attackDamage = attacker.attack * attackRoll;
        int defenseDamage = defender.strength * defendRoll;

        // The damage is the difference between attack and defense
        int damage = Math.max(0, attackDamage - defenseDamage);

        // Reduce defender's health
        defender.health -= damage;

        // Print attack details for better clarity
        System.out.println(attacker.name + " attacks with damage " + damage + " (" +attackDamage + " attack vs " + defenseDamage + " defense). " +defender.name + "'s health is now " + defender.health + ".");
    }
}
