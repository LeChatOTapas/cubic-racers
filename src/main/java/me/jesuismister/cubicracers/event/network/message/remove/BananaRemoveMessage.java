package me.jesuismister.cubicracers.event.network.message.remove;

import me.jesuismister.cubicracers.entity.custom.Banana;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class BananaRemoveMessage {
    public int test;

    public BananaRemoveMessage(){
    }

    public BananaRemoveMessage(int test){
        this.test = test;
    }


    public static void encode(BananaRemoveMessage message, FriendlyByteBuf buffer){
        buffer.writeInt(message.test);
    }

    public static BananaRemoveMessage decode(FriendlyByteBuf buffer){
        return new BananaRemoveMessage(buffer.readInt());
    }

    public static void handle(BananaRemoveMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                //DETRUIT TOUTES LES BANANES PROCHES DU KART
                List<Entity> nearbyEntities = kart.level().getEntities(kart, kart.getBoundingBox().inflate(0));
                for (Entity entity : nearbyEntities) {
                    if(entity instanceof Banana banana){
                        banana.remove(Entity.RemovalReason.KILLED);
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
