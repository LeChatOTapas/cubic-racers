package me.jesuismister.cubicracers.network.message.itemsKart.particles;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KlaxonParticleMessage {
    public double x;
    public double y;
    public double z;

    public KlaxonParticleMessage(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void encode(KlaxonParticleMessage message, FriendlyByteBuf buffer){
        buffer.writeDouble(message.x);
        buffer.writeDouble(message.y);
        buffer.writeDouble(message.z);
    }

    public static KlaxonParticleMessage decode(FriendlyByteBuf buffer){
        return new KlaxonParticleMessage(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    public static void handle(KlaxonParticleMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlerClass.handlePacket(message, contextSupplier));
        context.setPacketHandled(true);
    }
}
