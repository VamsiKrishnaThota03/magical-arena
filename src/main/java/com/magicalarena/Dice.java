import java.util.Random;

public class Dice
{
    // Roll a 6-sided die and return a random value between 1 and 6
    public static int roll()
    {
        Random random = new Random();
        return random.nextInt(6) + 1;
    }
}