package me.jesuismister.cubicracers.network;

import me.jesuismister.cubicracers.entity.custom.BobOmb;
import me.jesuismister.cubicracers.entity.custom.Kart;
import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.itemKart.Klaxon;
import me.jesuismister.cubicracers.network.message.ItemToClientMessage;
import me.jesuismister.cubicracers.network.message.itemsKart.particles.ExplosionParticleMessage;
import me.jesuismister.cubicracers.network.message.itemsKart.particles.KlaxonParticleMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientPacketHandlerClass {

    public static void handlePacket(ExplosionParticleMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        if(Minecraft.getInstance()!=null && Minecraft.getInstance().player!=null){
            BobOmb.spawnExplosionParticles(message.x, message.y, message.z);

            Player player = Minecraft.getInstance().player;
            BlockPos block = new BlockPos((int)message.x, (int)message.y, (int)message.z);
            int playerX = (int)player.getX();
            int playerY = (int)player.getY();
            int playerZ = (int)player.getZ();
            double distance = Math.sqrt(Math.pow(block.getX() - playerX, 2) + Math.pow(block.getY() - playerY, 2) + Math.pow(block.getZ() - playerZ, 2));
            SoundsInit.playSound(SoundsInit.BOB_OMB_EXPLOSION.get(), player.level(), block, player, SoundSource.RECORDS, (float) Math.max(1-distance/10, 0.1f));
        }
    }

    public static void handlePacket(KlaxonParticleMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        if(Minecraft.getInstance()!=null && Minecraft.getInstance().player!=null){
            Klaxon.spawnKlaxonParticles(message.x, message.y, message.z);

            Player player = Minecraft.getInstance().player;
            BlockPos block = new BlockPos((int)message.x, (int)message.y, (int)message.z);
            int playerX = (int)player.getX();
            int playerY = (int)player.getY();
            int playerZ = (int)player.getZ();
            double distance = Math.sqrt(Math.pow(block.getX() - playerX, 2) + Math.pow(block.getY() - playerY, 2) + Math.pow(block.getZ() - playerZ, 2));
            SoundsInit.playSound(SoundsInit.BOB_OMB_EXPLOSION.get(), player.level(), block, player, SoundSource.RECORDS, (float) Math.max(1-distance/10, 0.1f));
        }
    }

    public static void handlePacket(ItemToClientMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        if(Minecraft.getInstance()!=null && Minecraft.getInstance().player!=null){
            Player player = Minecraft.getInstance().player;
            if(player.getVehicle()!=null && player.getVehicle() instanceof Kart kart){
                System.out.println(message.item);
                kart.setKartItem(message.item);
                BlockPos block = new BlockPos((int)player.getX(), (int)player.getY(), (int)player.getZ());
                SoundsInit.playSound(SoundsInit.ITEM_BOX_CONSUME.get(), player.level(), block, player, SoundSource.RECORDS, 1f);
            }
        }
    }
}
