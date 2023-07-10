package me.jesuismister.cubicracers.util;

import com.mojang.blaze3d.platform.InputConstants;
import me.jesuismister.cubicracers.CubicRacers;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = CubicRacers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBinds {

    public static final String KEY_KART_CATEOGRY = "key.cubicracers.kart_category";

    public static final String KEY_ACCELERATE = "key.cubicracers.accelerate";
    public static final String KEY_DECCELERATE = "key.cubicracers.deccelerate";

    public static final String KEY_FORMWARD = "key.cubicracers.forward";
    public static final String KEY_BACKWARD = "key.cubicracers.backward";
    public static final String KEY_LEFT = "key.cubicracers.left";
    public static final String KEY_RIGHT = "key.cubicracers.right";

    public static final String KEY_DELTA = "key.cubicracers.delta";
    public static final String KEY_DRIFT = "key.cubicracers.drift";
    public static final String KEY_ITEM = "key.cubicracers.item";

    public static final KeyMapping KART_ACCELERATE_KEY = new KeyMapping(KEY_ACCELERATE, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_A, KEY_KART_CATEOGRY);
    public static final KeyMapping KART_DECCELERATE_KEY = new KeyMapping(KEY_DECCELERATE, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_E, KEY_KART_CATEOGRY);
    public static final KeyMapping KART_FORWARD_KEY = new KeyMapping(KEY_FORMWARD, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z, KEY_KART_CATEOGRY);
    public static final KeyMapping KART_BACKWARD_KEY = new KeyMapping(KEY_BACKWARD, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_S, KEY_KART_CATEOGRY);
    public static final KeyMapping KART_LEFT_KEY = new KeyMapping(KEY_LEFT, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_A, KEY_KART_CATEOGRY);
    public static final KeyMapping KART_RIGHT_KEY = new KeyMapping(KEY_RIGHT, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_D, KEY_KART_CATEOGRY);
    public static final KeyMapping KART_DELTA_KEY = new KeyMapping(KEY_DELTA, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_SPACE, KEY_KART_CATEOGRY);
    public static final KeyMapping KART_DRIFT_KEY = new KeyMapping(KEY_DRIFT, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B, KEY_KART_CATEOGRY);
    public static final KeyMapping KART_ITEM_KEY = new KeyMapping(KEY_ITEM, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G, KEY_KART_CATEOGRY);
}