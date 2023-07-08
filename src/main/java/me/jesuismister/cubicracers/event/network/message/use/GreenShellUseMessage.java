package me.jesuismister.cubicracers.event.network.message.use;

import me.jesuismister.cubicracers.entity.custom.GreenShell;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GreenShellUseMessage {


    public GreenShellUseMessage(){
    }

    public static void encode(GreenShellUseMessage message, FriendlyByteBuf buffer){
    }

    public static GreenShellUseMessage decode(FriendlyByteBuf buffer){
        return new GreenShellUseMessage();
    }

    public static void handle(GreenShellUseMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                GreenShell.spawnGreenShell(kart);
            }
        });
        context.setPacketHandled(true);
    }
}
