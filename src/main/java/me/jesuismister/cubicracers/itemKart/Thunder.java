package me.jesuismister.cubicracers.itemKart;

import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class Thunder {
    private static final float RANGE = 1000;

    public static void applyThunderToOthersKarts(Kart kart){
        if(!(kart.getFirstPassenger() instanceof Player)) return;

        List<Entity> nearbyEntities = kart.level().getEntities(kart, kart.getBoundingBox().inflate(RANGE));

        for (Entity entity : nearbyEntities) {
            if(entity instanceof Kart){
                LightningBolt lightningBolt = createThunderBolt(entity);
                kart.level().addFreshEntity(lightningBolt);
                Kart.stunKart((Kart) entity);
            }
        }
    }

    private static LightningBolt createThunderBolt(Entity entity){
        LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
        lightningBolt.setPos(entity.getX(), entity.getY(), entity.getZ());
        lightningBolt.setVisualOnly(true);
        lightningBolt.setSilent(true);
        return lightningBolt;
    }
}
