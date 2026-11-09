public class MagicalArena
{
    public static void main(String[] args)
    {
        // Create two players
        Player playerA = new Player("Player A", 50, 5, 10);
        Player playerB = new Player("Player B", 100, 10, 5);

        // Start the match between the two players
        Match match1 = new Match(playerA, playerB);
        match1.start();

        System.out.println();

        Player playerC = new Player("Player C", 42, 15, 9);
        Player playerD = new Player("Player D", 35, 20, 10);

        // Start the match between the two players
        Match match2 = new Match(playerC, playerD);
        match2.start();
    }
}