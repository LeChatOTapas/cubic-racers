package me.jesuismister.cubicracers.event.network.message.use;

import me.jesuismister.cubicracers.entity.custom.Banana;
import me.jesuismister.cubicracers.entity.custom.Kart;
import me.jesuismister.cubicracers.event.network.message.InputMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BananaUseMessage {
    private boolean isPressingKeyForward;

    public BananaUseMessage(boolean isPressingKeyForward){
        this.isPressingKeyForward = isPressingKeyForward;
    }

    public static void encode(BananaUseMessage message, FriendlyByteBuf buffer){
        buffer.writeBoolean(message.isPressingKeyForward);
    }

    public static BananaUseMessage decode(FriendlyByteBuf buffer){
        return new BananaUseMessage(buffer.readBoolean());
    }

    public static void handle(BananaUseMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                if(message.isPressingKeyForward){
                    Banana.spawnBananaFront(kart);
                }else{
                    Banana.spawnBananaBack(kart);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
