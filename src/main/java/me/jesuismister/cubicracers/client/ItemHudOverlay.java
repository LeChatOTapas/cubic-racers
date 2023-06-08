package me.jesuismister.cubicracers.client;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ItemHudOverlay {
    private static final ResourceLocation EMPTY_ITEM_BOX = new ResourceLocation(CubicRacers.MODID,
            "textures/kart/empty_item_box.png");
    private static final ResourceLocation BANANA_ITEM_BOX = new ResourceLocation(CubicRacers.MODID,
            "textures/kart/banana_item_box.png");

    public static final IGuiOverlay HUD_ITEM_BOX = (((gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if(shouldPrint(gui.getMinecraft().player)){
            Kart kart = (Kart) gui.getMinecraft().player.getVehicle();

            int imageSize = 32;

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            if(kart.objet==null) RenderSystem.setShaderTexture(0, EMPTY_ITEM_BOX);
            else if(kart.objet.equals("Banana")) RenderSystem.setShaderTexture(0, BANANA_ITEM_BOX);
            else RenderSystem.setShaderTexture(0, EMPTY_ITEM_BOX);

            GuiComponent.blit(poseStack, 5, 5, 0, 0, imageSize, imageSize, imageSize, imageSize);
        }
    }));

    private static boolean shouldPrint(LocalPlayer player) {
        return player!=null && player.getVehicle()!=null && player.getVehicle() instanceof Kart;
    }
}
