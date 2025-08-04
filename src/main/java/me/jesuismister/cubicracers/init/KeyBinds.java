package me.jesuismister.cubicracers.init;

import com.mojang.blaze3d.platform.InputConstants;
import me.jesuismister.cubicracers.CubicRacers;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {

    public static final String KEY_KART_CATEOGRY = "key.cubicracers.kart_category";

    public static final KeyMapping KART_ACCELERATE_KEY = Minecraft.getInstance().options.keyUp;
    public static final KeyMapping KART_DECCELERATE_KEY = Minecraft.getInstance().options.keyDown;
    public static final KeyMapping KART_LEFT_KEY = Minecraft.getInstance().options.keyLeft;
    public static final KeyMapping KART_RIGHT_KEY = Minecraft.getInstance().options.keyRight;

    public static final KeyMapping KART_FORWARD_KEY = new KeyMapping("key.cubicracers.forward",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UP, KEY_KART_CATEOGRY);

    public static final KeyMapping KART_BACKWARD_KEY = new KeyMapping("key.cubicracers.backward",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_DOWN, KEY_KART_CATEOGRY);

    public static final KeyMapping KART_DRIFT_KEY = new KeyMapping("key.cubicracers.drift",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_CONTROL, KEY_KART_CATEOGRY);

    public static final KeyMapping KART_ITEM_KEY = new KeyMapping("key.cubicracers.item",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, KEY_KART_CATEOGRY);


}