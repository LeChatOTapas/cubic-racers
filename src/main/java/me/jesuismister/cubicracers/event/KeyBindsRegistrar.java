package me.jesuismister.cubicracers.event;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.init.KeyBinds;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = CubicRacers.MODID, value = Dist.CLIENT)
public class KeyBindsRegistrar {

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBinds.KART_ACCELERATE_KEY);
            event.register(KeyBinds.KART_DECCELERATE_KEY);

            event.register(KeyBinds.KART_FORWARD_KEY);
            event.register(KeyBinds.KART_BACKWARD_KEY);
            event.register(KeyBinds.KART_LEFT_KEY);
            event.register(KeyBinds.KART_RIGHT_KEY);

            event.register(KeyBinds.KART_DRIFT_KEY);
            event.register(KeyBinds.KART_ITEM_KEY);
        }
}
