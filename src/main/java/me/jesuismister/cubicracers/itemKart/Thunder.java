package me.jesuismister.cubicracers.itemKart;

import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class Thunder {
    public static void applyThunderToOthersKarts(Kart kart){
        if(!(kart.getFirstPassenger() instanceof Player)) return;

        List<Entity> nearbyEntities = kart.getLevel().getEntities(kart, kart.getBoundingBox().inflate(1000));

        for (Entity entity : nearbyEntities) {
            if(entity instanceof Kart){
                Kart.stunKart((Kart) entity);
            }
        }
    }
}
