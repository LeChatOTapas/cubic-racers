package me.jesuismister.cubicracers.event.network.message;

import me.jesuismister.cubicracers.entity.custom.Banana;
import me.jesuismister.cubicracers.entity.custom.BobOmb;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class BobOmbRemoveMessage {
    public int test;

    public BobOmbRemoveMessage(){
    }

    public BobOmbRemoveMessage(int test){
        this.test = test;
    }


    public static void encode(BobOmbRemoveMessage message, FriendlyByteBuf buffer){
        buffer.writeInt(message.test);
    }

    public static BobOmbRemoveMessage decode(FriendlyByteBuf buffer){
        return new BobOmbRemoveMessage(buffer.readInt());
    }

    public static void handle(BobOmbRemoveMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                //DETRUIT TOUTES LES BOB OMBS PROCHES DU KART
                List<Entity> nearbyEntities = kart.getLevel().getEntities(kart, kart.getBoundingBox().inflate(0));
                for (Entity entity : nearbyEntities) {
                    if(entity instanceof BobOmb bobOmb){
                        bobOmb.remove(Entity.RemovalReason.KILLED);
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
