package me.jesuismister.cubicracers.network.message.itemsKart.particles;

import me.jesuismister.cubicracers.entity.custom.BobOmb;
import me.jesuismister.cubicracers.itemKart.Klaxon;
import net.minecraft.client.Minecraft;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientPacketHandlerClass {

    public static void handlePacket(ExplosionParticleMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        if(Minecraft.getInstance()!=null && Minecraft.getInstance().player!=null){
            BobOmb.spawnExplosionParticles(message.x, message.y, message.z);
        }
    }

    public static void handlePacket(KlaxonParticleMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        if(Minecraft.getInstance()!=null && Minecraft.getInstance().player!=null){
            Klaxon.spawnKlaxonParticles(message.x, message.y, message.z);
        }
    }
}
