package me.jesuismister.cubicracers.network.message.clientToServer.kartItem;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import me.jesuismister.cubicracers.network.Network;
import me.jesuismister.cubicracers.network.message.serverToClient.kartItem.BoostSyncMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class MushroomUseMessage {

    public MushroomUseMessage() {
    }

    public static void encode(MushroomUseMessage message, FriendlyByteBuf buffer) {
    }

    public static MushroomUseMessage decode(FriendlyByteBuf buffer) {
        return new MushroomUseMessage();
    }

    public static void handle(MushroomUseMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            if (player.getVehicle() != null && player.getVehicle() instanceof TestKart kart){
                kart.setTimeBoost(5.f);
                kart.setSpeed(kart.getMAX_SPEED() + kart.getBOOST());
                kart.setKartItem("None");
                Network.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> kart), new BoostSyncMessage(kart.getId(), kart.getTimeBoost()));
            }
        });
        context.setPacketHandled(true);
    }
}
