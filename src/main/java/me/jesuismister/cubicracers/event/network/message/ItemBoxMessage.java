package me.jesuismister.cubicracers.event.network.message;

import me.jesuismister.cubicracers.entity.custom.GreenShell;
import me.jesuismister.cubicracers.entity.custom.ItemBox;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Supplier;

public class ItemBoxMessage {
    public boolean isFalse;
    public String item;

    public ItemBoxMessage(){
    }

    public ItemBoxMessage(boolean isTrue,String item){
        this.isFalse = isTrue;
        this.item = item;
    }


    public static void encode(ItemBoxMessage message, FriendlyByteBuf buffer){
        buffer.writeBoolean(message.isFalse);
        buffer.writeCharSequence(message.item.subSequence(0, message.item.length()), Charset.defaultCharset());
    }

    public static ItemBoxMessage decode(FriendlyByteBuf buffer){
        return new ItemBoxMessage(buffer.readBoolean(),
                buffer.readCharSequence(buffer.readableBytes(), Charset.defaultCharset()).toString());
    }

    public static void handle(ItemBoxMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                //DETRUIT TOUTES LES GREEN SHELL PROCHES DU KART
                List<Entity> nearbyEntities = kart.level().getEntities(kart, kart.getBoundingBox().inflate(2));
                for (Entity entity : nearbyEntities) {
                    if(entity instanceof ItemBox itemBox){
                        if(message.isFalse){
                            itemBox.remove(Entity.RemovalReason.KILLED);
                        }else{
                            itemBox.hasItem = false;
                            kart.kartItem = message.item;
                        }
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
