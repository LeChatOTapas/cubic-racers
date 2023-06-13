package me.jesuismister.cubicracers.client;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class SpeedHudOverlay {

    public static final IGuiOverlay HUD_SPEED = (((gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if (shouldPrint(gui.getMinecraft().player)) {
            Kart kart = (Kart) gui.getMinecraft().player.getVehicle();
            if(kart.getLevel().toString().contains("Server")) return;

            String text = "Speed: " + kart.getSpeed();
            int textWidth = Minecraft.getInstance().font.width(text);
            int textX = (screenWidth - textWidth) / 2;

            Minecraft.getInstance().font.draw(poseStack, text, textX, 20, 0xFFFFFF);
        }
    }));

    private static boolean shouldPrint(LocalPlayer player) {
        return player != null && player.getVehicle() != null && player.getVehicle() instanceof Kart;
    }
}
