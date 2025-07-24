package me.jesuismister.cubicracers.network.message.clientToServer.kartItem;

import me.jesuismister.cubicracers.entity.custom.Banana;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import me.jesuismister.cubicracers.init.KartItemsInit;
import me.jesuismister.cubicracers.util.UtilityMethod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StarUseMessage {

    public StarUseMessage() {
    }

    public static void encode(StarUseMessage message, FriendlyByteBuf buffer) {
    }

    public static StarUseMessage decode(FriendlyByteBuf buffer) {
        return new StarUseMessage();
    }

    public static void handle(StarUseMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            if (player.getVehicle() != null && player.getVehicle() instanceof TestKart kart){
                kart.setTimeStar(20f);
                kart.setStarSpeedBoost(1.5f);
                kart.setInvinsible(true);
                kart.setKartItem("None");
            }
        });
        context.setPacketHandled(true);
    }
}
