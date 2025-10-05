package me.jesuismister.cubicracers.util;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import me.jesuismister.cubicracers.network.Network;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class UtilityMethod {
    private static final double DISTANCE = 2f;

    public static Vec3 getCoordToSpawnItem(double x, double y, double z, float angle, boolean isFront) {
        double rad = isFront ? Math.toRadians(angle) : Math.toRadians(angle - 180);
        double newX = x + DISTANCE * -Math.sin(rad);
        double newZ = z + DISTANCE * Math.cos(rad);
        return new Vec3(newX, y, newZ);
    }

    public static void stunKartAround(Entity entity, int range, String stunMotif) {
        List<Entity> nearbyEntities = entity.level().getEntities(entity, entity.getBoundingBox().inflate(range));
        for (Entity target : nearbyEntities) {
            if (target instanceof TestKart kart && kart.getFirstPassenger() != null) {
                if (kart.getCanMove() && !entity.equals(target)) {
                    TestKart.stunKart(kart, stunMotif);
                    // Utiliser la méthode sécurisée pour envoyer le message de stun
                    Network.sendStunMessage(kart.getId(), stunMotif);
                }
            }
        }
    }
}
