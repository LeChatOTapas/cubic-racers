package me.jesuismister.cubicracers.event.network.message.use;

import me.jesuismister.cubicracers.entity.custom.GreenShell;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GreenShellUseMessage {
    public boolean isPressingKeyBackward;

    public GreenShellUseMessage(boolean isPressingKeyBackward){
        this.isPressingKeyBackward = isPressingKeyBackward;
    }

    public static void encode(GreenShellUseMessage message, FriendlyByteBuf buffer){
        buffer.writeBoolean(message.isPressingKeyBackward);
    }

    public static GreenShellUseMessage decode(FriendlyByteBuf buffer){
        return new GreenShellUseMessage(buffer.readBoolean());
    }

    public static void handle(GreenShellUseMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                if(message.isPressingKeyBackward){
                    GreenShell.spawnGreenShellBack(kart);
                }else{
                    GreenShell.spawnGreenShellFront(kart);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
