package me.jesuismister.cubicracers.event.network.message.use;

import me.jesuismister.cubicracers.entity.custom.Kart;
import me.jesuismister.cubicracers.itemKart.Thunder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ThunderUseMessage {


    public ThunderUseMessage(){
    }

    public static void encode(ThunderUseMessage message, FriendlyByteBuf buffer){
    }

    public static ThunderUseMessage decode(FriendlyByteBuf buffer){
        return new ThunderUseMessage();
    }

    public static void handle(ThunderUseMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                Thunder.applyThunderToOthersKarts(kart);
            }
        });
        context.setPacketHandled(true);
    }
}
