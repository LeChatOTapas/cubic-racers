package me.jesuismister.cubicracers.event.network.message.use;

import me.jesuismister.cubicracers.entity.custom.Banana;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BananaUseMessage {


    public BananaUseMessage(){
    }

    public static void encode(BananaUseMessage message, FriendlyByteBuf buffer){
    }

    public static BananaUseMessage decode(FriendlyByteBuf buffer){
        return new BananaUseMessage();
    }

    public static void handle(BananaUseMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                Banana.spawnBanana(kart);
            }
        });
        context.setPacketHandled(true);
    }
}
