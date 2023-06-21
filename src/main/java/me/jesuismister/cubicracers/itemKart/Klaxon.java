package me.jesuismister.cubicracers.itemKart;

import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class Klaxon {
    private static final float RANGE = 4;

    public static void applyKlaxonToOthersKarts(Kart kart) {
        if (!(kart.getFirstPassenger() instanceof Player)) return;

        // SPAWN DES PARTICULES
        double angle, x, z;
        for (int i = 0; i < 360; i += 10) {
            angle = Math.toRadians(i);
            for (float j = 0; j < RANGE; j += 0.5f) {
                x = kart.getX() + j * Math.cos(angle);
                z = kart.getZ() + j * Math.sin(angle);
                Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.ITEM_SNOWBALL, x, kart.getY(), z, 0, 0, 0);
            }
        }

        //APPLY DES STUNS
        List<Entity> nearbyEntities = kart.getLevel().getEntities(kart, kart.getBoundingBox().inflate(RANGE));
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Kart) {
                Kart.stunKart((Kart) entity);
            }
        }
    }
}
