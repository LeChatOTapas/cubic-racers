package me.jesuismister.cubicracers.event.network.message.remove;

import me.jesuismister.cubicracers.entity.custom.FakeBox;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Supplier;

public class FakeBoxRemoveMessage {
    public String item;

    public FakeBoxRemoveMessage(){
    }

    public FakeBoxRemoveMessage(String item){
        this.item = item;
    }


    public static void encode(FakeBoxRemoveMessage message, FriendlyByteBuf buffer){
        buffer.writeCharSequence(message.item.subSequence(0, message.item.length()), Charset.defaultCharset());
    }

    public static FakeBoxRemoveMessage decode(FriendlyByteBuf buffer){
        return new FakeBoxRemoveMessage(buffer.readCharSequence(buffer.readableBytes(), Charset.defaultCharset()).toString());
    }

    public static void handle(FakeBoxRemoveMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                List<Entity> nearbyEntities = kart.level().getEntities(kart, kart.getBoundingBox().inflate(0));
                for (Entity entity : nearbyEntities) {
                    if(entity instanceof FakeBox fakeBox){
                        fakeBox.remove(RemovalReason.KILLED);
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
