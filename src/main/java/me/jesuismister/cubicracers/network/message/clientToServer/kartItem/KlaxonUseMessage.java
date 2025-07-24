
package me.jesuismister.cubicracers.network.message.clientToServer.kartItem;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.network.Network;
import me.jesuismister.cubicracers.network.message.serverToClient.StunMessage;
import me.jesuismister.cubicracers.network.message.serverToClient.kartItem.ExplosionParticleMessage;
import me.jesuismister.cubicracers.network.message.serverToClient.kartItem.KlaxonParticleMessage;
import me.jesuismister.cubicracers.util.ClientUtil;
import me.jesuismister.cubicracers.util.UtilityMethod;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class KlaxonUseMessage {
    public static final int KLAXON_RANGE = 4;

    public KlaxonUseMessage() {
    }

    public static void encode(KlaxonUseMessage message, FriendlyByteBuf buffer) {
    }

    public static KlaxonUseMessage decode(FriendlyByteBuf buffer) {
        return new KlaxonUseMessage();
    }

    public static void handle(KlaxonUseMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            if (player.getVehicle() != null && player.getVehicle() instanceof TestKart kart) {
                kart.setKartItem("None");

                UtilityMethod.stunKartAround(kart, KLAXON_RANGE, "Klaxon");

                Network.CHANNEL.send(
                        PacketDistributor.ALL.noArg(),
                        new KlaxonParticleMessage(kart.getX(), kart.getY(), kart.getZ())
                );

                ClientUtil.playSoundToAll(kart.level(), kart.getX(), kart.getY(), kart.getZ(), 8 + KLAXON_RANGE, SoundsInit.KLAXON.get(), SoundSource.RECORDS, 1f, 0.95f);
            }
        });
        context.setPacketHandled(true);
    }
}
