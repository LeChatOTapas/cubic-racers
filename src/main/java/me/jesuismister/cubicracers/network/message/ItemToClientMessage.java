package me.jesuismister.cubicracers.network.message;

import me.jesuismister.cubicracers.network.ClientPacketHandlerClass;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.Charset;
import java.util.function.Supplier;

public class ItemToClientMessage {
    public String item;

    public ItemToClientMessage(String item){
        this.item = item;
    }

    public static void encode(ItemToClientMessage message, FriendlyByteBuf buffer){
        buffer.writeCharSequence(message.item.subSequence(0, message.item.length()), Charset.defaultCharset());
    }

    public static ItemToClientMessage decode(FriendlyByteBuf buffer){
        return new ItemToClientMessage(buffer.readCharSequence(buffer.readableBytes(), Charset.defaultCharset()).toString());
    }

    public static void handle(ItemToClientMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlerClass.handlePacket(message, contextSupplier));
        context.setPacketHandled(true);
    }
}
