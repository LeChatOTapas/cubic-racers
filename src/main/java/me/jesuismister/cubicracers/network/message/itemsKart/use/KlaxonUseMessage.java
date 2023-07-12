package me.jesuismister.cubicracers.network.message.itemsKart.use;

import me.jesuismister.cubicracers.entity.custom.Kart;
import me.jesuismister.cubicracers.itemKart.Klaxon;
import me.jesuismister.cubicracers.network.Network;
import me.jesuismister.cubicracers.network.message.itemsKart.particles.KlaxonParticleMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class KlaxonUseMessage {
    public double x;
    public double y;
    public double z;

    public KlaxonUseMessage(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void encode(KlaxonUseMessage message, FriendlyByteBuf buffer) {
        buffer.writeDouble(message.x);
        buffer.writeDouble(message.y);
        buffer.writeDouble(message.z);
    }

    public static KlaxonUseMessage decode(FriendlyByteBuf buffer) {
        return new KlaxonUseMessage(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    public static void handle(KlaxonUseMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                Klaxon.applyKlaxonToOthersKarts(kart);
                Network.CHANNEL.send(PacketDistributor.ALL.noArg(), new KlaxonParticleMessage(message.x, message.y, message.z));
            }
        });
        context.setPacketHandled(true);
    }
}
