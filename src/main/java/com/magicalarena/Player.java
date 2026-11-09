public class Player 
{
    String name;
    int health;
    int strength;
    int attack;

    // Constructor to initialize player attributes
    public Player(String name, int health, int strength, int attack) 
    {
        this.name = name;
        this.health = health;
        this.strength = strength;
        this.attack = attack;
    }

    // Method for attacking the opponent
    public void attackOpponent(Player opponent) 
    {
        // Roll the dice for both attacker and defender
        int attackRoll = Dice.roll();
        int defendRoll = Dice.roll();
        
        // Calculate the attack damage and defense damage
        int attackDamage = this.attack * attackRoll;
        int defendDamage = opponent.strength * defendRoll;  // Including defense in calculation
        
        // Calculate net damage to the opponent
        int netDamage = Math.max(attackDamage - defendDamage, 0);
        opponent.health -= netDamage;

        // Log the attack details
        System.out.println(this.name + " attacks " + opponent.name + ": " + attackDamage + " - " + defendDamage + " = " + netDamage + " damage");
    }
}
