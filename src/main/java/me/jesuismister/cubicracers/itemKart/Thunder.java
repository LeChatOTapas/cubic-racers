package me.jesuismister.cubicracers.itemKart;

import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class Thunder {
    public static void applyThunderToOthersKarts(Kart kart){
        if(!(kart.getFirstPassenger() instanceof Player)) return;

        List<Entity> nearbyEntities = kart.getLevel().getEntities(kart, kart.getBoundingBox().inflate(1000));

        for (Entity entity : nearbyEntities) {
            if(entity instanceof Kart){
                LightningBolt lightningBolt = createThunderBolt(entity);
                kart.getLevel().addFreshEntity(lightningBolt);
                Kart.stunKart((Kart) entity);
            }
        }
    }

    private static LightningBolt createThunderBolt(Entity entity){
        LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.getLevel());
        lightningBolt.setPos(entity.getX(), entity.getY(), entity.getZ());
        lightningBolt.setVisualOnly(true);
        lightningBolt.setSilent(true); //POURQUOI CA MARCHE PAS ???
        return lightningBolt;
    }
}
