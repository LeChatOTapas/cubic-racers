package me.jesuismister.cubicracers.util;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.UUID;

public class ClientRandom {
    private static Random random;

    public static void initialize(long seed) {
        random = new Random(seed);
    }

    public static int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public static UUID generateUUIDFromSeed(long seed) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16); // Modifier la taille du ByteBuffer à 16

        byteBuffer.putLong(seed);
        byteBuffer.putLong(seed); // Répéter la seed pour remplir les 16 bytes

        byteBuffer.flip();

        long mostSignificantBits = byteBuffer.getLong();
        long leastSignificantBits = byteBuffer.getLong();

        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}
