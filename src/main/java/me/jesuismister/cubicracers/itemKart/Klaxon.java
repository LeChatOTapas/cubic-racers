package me.jesuismister.cubicracers.itemKart;

import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class Klaxon {
    private static final float RANGE = 6;

    public static void applyKlaxonToOthersKarts(Kart kart) {
        //APPLY DES STUNS
        List<Entity> nearbyEntities = kart.level().getEntities(kart, kart.getBoundingBox().inflate(RANGE));

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Kart k) {
                if (k.getCanMove()) Kart.stunKart(k);
            }
        }
    }

    public static void spawnKlaxonParticles(double x, double y, double z) {
        // SPAWN DES PARTICULES
        double angle, x0, z0;
        for (int i = 0; i < 360; i += 10) {
            angle = Math.toRadians(i);
            for (float j = 0; j < RANGE; j += 0.25f) {
                x0 = x + j * Math.cos(angle);
                z0 = z + j * Math.sin(angle);
                Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.ITEM_SNOWBALL, x0, y, z0, 0, 0, 0);
            }
        }
    }
}
