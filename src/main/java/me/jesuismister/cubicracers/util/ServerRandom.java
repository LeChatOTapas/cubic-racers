package me.jesuismister.cubicracers.util;

import java.util.Random;

public class ServerRandom {
    private static Random random;

    public static void initialize(long seed) {
        random = new Random(seed);
    }

    public static int nextInt(int bound) {
        return random.nextInt(bound);
    }
}