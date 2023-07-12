package me.jesuismister.cubicracers.network.message.itemsKart.particles;

import me.jesuismister.cubicracers.network.ClientPacketHandlerClass;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ExplosionParticleMessage {
    public double x;
    public double y;
    public double z;

    public ExplosionParticleMessage(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void encode(ExplosionParticleMessage message, FriendlyByteBuf buffer){
        buffer.writeDouble(message.x);
        buffer.writeDouble(message.y);
        buffer.writeDouble(message.z);
    }

    public static ExplosionParticleMessage decode(FriendlyByteBuf buffer){
        return new ExplosionParticleMessage(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    public static void handle(ExplosionParticleMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlerClass.handlePacket(message, contextSupplier));
        context.setPacketHandled(true);
    }
}
