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

    public static final String KEY_UP = "key.cubicracers.forward";
    public static final String KEY_DOWN = "key.cubicracers.backward";
    public static final String KEY_LEFT = "key.cubicracers.left";
    public static final String KEY_RIGHT = "key.cubicracers.right";

    public static final String KEY_DELTA = "key.cubicracers.delta";
    public static final String KEY_DRIFT = "key.cubicracers.drift";
    public static final String KEY_ITEM = "key.cubicracers.item";

    public static final KeyMapping KART_UP_KEY = new KeyMapping(KEY_UP, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_W, KEY_KART_CATEOGRY);
    public static final KeyMapping KART_DOWN_KEY = new KeyMapping(KEY_DOWN, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
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