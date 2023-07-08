package me.jesuismister.cubicracers.event.network.message.use;

import me.jesuismister.cubicracers.entity.custom.FakeBox;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FakeBoxUseMessage {


    public FakeBoxUseMessage(){
    }

    public static void encode(FakeBoxUseMessage message, FriendlyByteBuf buffer){
    }

    public static FakeBoxUseMessage decode(FriendlyByteBuf buffer){
        return new FakeBoxUseMessage();
    }

    public static void handle(FakeBoxUseMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                FakeBox.spawnFakeBox(kart);
            }
        });
        context.setPacketHandled(true);
    }
}
