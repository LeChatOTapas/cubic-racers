package me.jesuismister.cubicracers.client;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;

import java.util.List;
/*
public class SpeedHudLayer implements LayeredDraw.Layer{

    public static final LayeredDraw.Layer HUD_SPEED = (((gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if (Minecraft.getInstance().player!=null) {
            Player player = Minecraft.getInstance().player;
            if(!player.level().isClientSide()) return;

            double x,y,z;
            float yRot, speed;
            TestKart kart = null;
            String text;
            if(player.getVehicle()==null){
                List<Entity> nearbyEntities = player.level().getEntities(player, player.getBoundingBox().inflate(200));
                for(Entity e : nearbyEntities){
                    if(e instanceof TestKart) {
                        kart = (TestKart) e;
                        break;
                    }
                }
                if(kart == null){
                    text = "No kart found";
                }else{
                    x = kart.getX();
                    y = kart.getY();
                    z = kart.getZ();
                    yRot = kart.getYRot();
                    speed = kart.getSpeed();
                    text = "x = " + x + "\ny = " + y + "\nz = " + z + "\nyRot = " + yRot + "\nSpeed = " + speed;
                }
            }else{
                kart = (TestKart) player.getVehicle();
                x = kart.getX();
                y = kart.getY();
                z = kart.getZ();
                yRot = kart.getYRot();
                speed = kart.getSpeed();
                text = "x = " + x + "\ny = " + y + "\nz = " + z + "\nyRot = " + yRot + "\nSpeed = " + speed;
            }


            int textWidth = Minecraft.getInstance().font.width(text);

            int heightPos = 20;
            for(String s : text.split("\n")){
                poseStack.(Minecraft.getInstance().font, s, 500, heightPos, 0xFFFFFF);
                heightPos+=20;
            }

        }

    private static boolean shouldPrint(LocalPlayer player) {
        return player != null && player.getVehicle() != null && player.getVehicle() instanceof TestKart;
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        // Exemple : afficher la vitesse du joueur
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player != null) {
            double speed = Math.sqrt(player.getDeltaMovement().lengthSqr()) * 20; // blocs/seconde
            String speedText = String.format("Speed: %.1f b/s", speed);
            guiGraphics.drawString(mc.font, speedText, 10, 10, 0xFFFFFF, false);
        }
    }
}
*/