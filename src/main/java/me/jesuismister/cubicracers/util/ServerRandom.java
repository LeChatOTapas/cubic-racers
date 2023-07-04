package me.jesuismister.cubicracers.util;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.UUID;

public class ServerRandom {
    private static Random random;

    public static void initialize(long seed) {
        random = new Random(seed);
    }

    public static int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public static UUID generateUUIDFromSeed(long seed) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.putLong(seed);

        // Ajouter des bytes supplémentaires si nécessaire pour la taille correcte du ByteBuffer
        while (byteBuffer.position() < 16) {
            byteBuffer.put((byte) 0);
        }

        byteBuffer.flip();

        long mostSignificantBits = byteBuffer.getLong();
        long leastSignificantBits = byteBuffer.getLong();

        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}