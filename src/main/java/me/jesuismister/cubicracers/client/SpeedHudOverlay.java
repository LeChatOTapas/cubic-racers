package me.jesuismister.cubicracers.client;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class SpeedHudOverlay {

    public static final IGuiOverlay HUD_SPEED = (((gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        /*
        if (shouldPrint(gui.getMinecraft().player)) {
            Kart kart = (Kart) gui.getMinecraft().player.getVehicle();
            if (kart == null || !kart.level().isClientSide()) return;

            String text = "Speed: " + (((float)Math.round(kart.speedToShow*1000000f))/1000000f + " (" + (((float)Math.round(kart.getSpeed()*1000000f))/1000000f) + ")");
            int textWidth = Minecraft.getInstance().font.width(text);
            int textX = (screenWidth - textWidth) / 2;

            poseStack.drawCenteredString(Minecraft.getInstance().font, text, textX, 20, 0xFFFFFF);

        }*/
    }));

    /**
     * Méthode qui détermine si l'HUD doit être affiché
     *
     * @param player
     * @return
     */
    private static boolean shouldPrint(LocalPlayer player) {
        return player != null && player.getVehicle() != null && player.getVehicle() instanceof TestKart;
    }
}
