package me.jesuismister.cubicracers.event.network.message.use;

import me.jesuismister.cubicracers.entity.custom.BobOmb;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BobOmbUseMessage {


    public BobOmbUseMessage(){
    }

    public static void encode(BobOmbUseMessage message, FriendlyByteBuf buffer){
    }

    public static BobOmbUseMessage decode(FriendlyByteBuf buffer){
        return new BobOmbUseMessage();
    }

    public static void handle(BobOmbUseMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                BobOmb.spawnBobOmb(kart);
            }
        });
        context.setPacketHandled(true);
    }
}
