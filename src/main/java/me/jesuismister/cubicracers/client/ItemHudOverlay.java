package me.jesuismister.cubicracers.client;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ItemHudOverlay {
    private static final ResourceLocation EMPTY_ITEM_BOX = new ResourceLocation(CubicRacers.MODID,
            "textures/kart/empty_item_box.png");
    private static final ResourceLocation BANANA_ITEM_BOX = new ResourceLocation(CubicRacers.MODID,
            "textures/kart/banana_item_box.png");
    private static final ResourceLocation MUSHROOM_ITEM_BOX = new ResourceLocation(CubicRacers.MODID,
            "textures/kart/mushroom_item_box.png");
    private static final ResourceLocation STAR_ITEM_BOX = new ResourceLocation(CubicRacers.MODID,
            "textures/kart/star_item_box.png");
    private static final ResourceLocation THUNDER_ITEM_BOX = new ResourceLocation(CubicRacers.MODID,
            "textures/kart/thunder_item_box.png");
    private static final ResourceLocation KLAXON_ITEM_BOX = new ResourceLocation(CubicRacers.MODID,
            "textures/kart/klaxon_item_box.png");
    private static final ResourceLocation BOB_OMB_ITEM_BOX = new ResourceLocation(CubicRacers.MODID,
            "textures/kart/bob_omb_item_box.png");
    private static final ResourceLocation GREEN_SHELL_ITEM_BOX = new ResourceLocation(CubicRacers.MODID,
            "textures/kart/green_shell_item_box.png");
    private static final ResourceLocation FAKE_BOX_ITEM_BOX = new ResourceLocation(CubicRacers.MODID,
            "textures/kart/fake_box_item_box.png");
    private static final int imageSize = 32;

    public static final IGuiOverlay HUD_ITEM_BOX = (((gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if (shouldPrint(gui.getMinecraft().player)) {
            TestKart kart = (TestKart) gui.getMinecraft().player.getVehicle();
            if (kart == null) return;

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            //CHOIX DE LA TEXTURE EN FONCTION DE L'OBJET EN STOCK
            ResourceLocation resource;
            if (kart.getKartItem().equals("Banana")) resource = BANANA_ITEM_BOX;
            else if (kart.getKartItem().equals("Mushroom")) resource = MUSHROOM_ITEM_BOX;
            else if (kart.getKartItem().equals("Star")) resource = STAR_ITEM_BOX;
            else if (kart.getKartItem().equals("Thunder")) resource = THUNDER_ITEM_BOX;
            else if (kart.getKartItem().equals("Klaxon")) resource = KLAXON_ITEM_BOX;
            else if (kart.getKartItem().equals("Bob_omb")) resource = BOB_OMB_ITEM_BOX;
            else if (kart.getKartItem().equals("Green_shell")) resource = GREEN_SHELL_ITEM_BOX;
            else if (kart.getKartItem().equals("Fake_box")) resource = FAKE_BOX_ITEM_BOX;
            else resource = EMPTY_ITEM_BOX;

            RenderSystem.setShaderTexture(0, resource);
            poseStack.blit(resource, 5, 5, 0, 0, imageSize, imageSize, imageSize, imageSize);
        }
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
