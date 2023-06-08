package me.jesuismister.cubicracers.event;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.client.ItemHudOverlay;
import me.jesuismister.cubicracers.client.SpeedHudOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = CubicRacers.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents{
        @SubscribeEvent
        public static void registerGuiOverlay(RegisterGuiOverlaysEvent event){
            event.registerAboveAll("item_box_hud", ItemHudOverlay.HUD_ITEM_BOX);
            event.registerAboveAll("speed_hud", SpeedHudOverlay.HUD_SPEED);
        }
    }
}
