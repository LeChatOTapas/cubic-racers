package me.jesuismister.cubicracers.network.message.itemsKart.use;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.itemKart.Klaxon;
import me.jesuismister.cubicracers.util.ClientUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

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
            if (player.getVehicle() != null && player.getVehicle() instanceof TestKart kart) {
                Klaxon.applyKlaxonToOthersKarts(kart);
                ClientUtil.playSoundToAll(player.level(), player.getX(), player.getY(), player.getZ(), 32, SoundsInit.KLAXON.get(), SoundSource.RECORDS, 1f, 0.95f);
            }
        });
        context.setPacketHandled(true);
    }
}
