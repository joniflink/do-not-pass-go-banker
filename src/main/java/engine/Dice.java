package engine;

import java.util.Random;

public class Dice {
    private Random random = new Random();

    /**
     * Throws a dice.
     * @return A random integer between 1 and 6 (inclusive).
     */
    public int throwDice() {
        return random.nextInt(6) + 1;
    }
}
