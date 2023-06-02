package me.jesuismister.mckart.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {

    public static final String KEY_KART_CATEOGRY = "key.cubicracers.kart_category";

    public static final String KEY_UP = "key.cubicracers.forward";
    public static final String KEY_DOWN = "key.cubicracers.backward";
    public static final String KEY_LEFT = "key.cubicracers.left";
    public static final String KEY_RIGHT = "key.cubicracers.right";
    public static final String KEY_JUMP = "key.cubicracers.jump";

    public static final KeyMapping KART_UP_KEY = new KeyMapping(KEY_UP, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_W, KEY_KART_CATEOGRY);
    public static final KeyMapping KART_DOWN_KEY = new KeyMapping(KEY_DOWN, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_S, KEY_KART_CATEOGRY);
    public static final KeyMapping KART_LEFT_KEY = new KeyMapping(KEY_LEFT, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_A, KEY_KART_CATEOGRY);
    public static final KeyMapping KART_RIGHT_KEY = new KeyMapping(KEY_RIGHT, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_D, KEY_KART_CATEOGRY);
    public static final KeyMapping KART_JUMP_KEY = new KeyMapping(KEY_JUMP, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_SPACE, KEY_KART_CATEOGRY);




}