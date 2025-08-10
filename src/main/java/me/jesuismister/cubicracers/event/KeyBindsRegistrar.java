package me.jesuismister.cubicracers.event;

import com.mojang.blaze3d.platform.InputConstants;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.init.KeyBinds;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = CubicRacers.MODID, value = Dist.CLIENT)
public class KeyBindsRegistrar {

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent evt) {
        Options options = Minecraft.getInstance().options;

        // Récupérations des touches de direction existantes
        // KeyBinds.KART_ACCELERATE_KEY = options.keyUp;
        // KeyBinds.KART_DECCELERATE_KEY = options.keyDown;
        // KeyBinds.KART_LEFT_KEY = options.keyLeft;
        // KeyBinds.KART_RIGHT_KEY = options.keyRight;

        // Nouvelles touches
        KeyBinds.KART_FORWARD_KEY = new KeyMapping(
                "key.cubicracers.forward",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UP,
                KeyBinds.KEY_KART_CATEGORY
        );
        KeyBinds.KART_BACKWARD_KEY = new KeyMapping(
                "key.cubicracers.backward",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_DOWN,
                KeyBinds.KEY_KART_CATEGORY
        );
        KeyBinds.KART_DRIFT_KEY = new KeyMapping(
                "key.cubicracers.drift",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_CONTROL,
                KeyBinds.KEY_KART_CATEGORY
        );
        KeyBinds.KART_ITEM_KEY = new KeyMapping(
                "key.cubicracers.item",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinds.KEY_KART_CATEGORY
        );

        // Enregistrement de TOUTES les KeyMappings
        // evt.register(KeyBinds.KART_ACCELERATE_KEY);
        // evt.register(KeyBinds.KART_DECCELERATE_KEY);
        // evt.register(KeyBinds.KART_LEFT_KEY);
        // evt.register(KeyBinds.KART_RIGHT_KEY);
        evt.register(KeyBinds.KART_FORWARD_KEY);
        evt.register(KeyBinds.KART_BACKWARD_KEY);
        evt.register(KeyBinds.KART_DRIFT_KEY);
        evt.register(KeyBinds.KART_ITEM_KEY);
    }
}