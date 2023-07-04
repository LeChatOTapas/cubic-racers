package me.jesuismister.cubicracers.event.network.message;

import me.jesuismister.cubicracers.entity.custom.Banana;
import me.jesuismister.cubicracers.entity.custom.GreenShell;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class GreenShellRemoveMessage {
    public int test;

    public GreenShellRemoveMessage(){
    }

    public GreenShellRemoveMessage(int test){
        this.test = test;
    }


    public static void encode(GreenShellRemoveMessage message, FriendlyByteBuf buffer){
        buffer.writeInt(message.test);
    }

    public static GreenShellRemoveMessage decode(FriendlyByteBuf buffer){
        return new GreenShellRemoveMessage(buffer.readInt());
    }

    public static void handle(GreenShellRemoveMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                //DETRUIT TOUTES LES GREEN SHELL PROCHES DU KART
                List<Entity> nearbyEntities = kart.getLevel().getEntities(kart, kart.getBoundingBox().inflate(2));
                for (Entity entity : nearbyEntities) {
                    if(entity instanceof GreenShell greenShell){
                        greenShell.remove(Entity.RemovalReason.KILLED);
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
