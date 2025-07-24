package me.jesuismister.cubicracers.network.message.serverToClient.kartItem;

import me.jesuismister.cubicracers.entity.custom.BobOmb;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class ExplosionParticleMessage {
    private final double x;
    private final double y;
    private final double z;

    public ExplosionParticleMessage(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static ExplosionParticleMessage decode(FriendlyByteBuf buf) {
        return new ExplosionParticleMessage(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // Client-side only
            if (context.get().getDirection().getReceptionSide().isClient()) {
                if (Minecraft.getInstance().player != null) {
                    spawnExplosionParticles(x, y, z);
                }
            }
        });
        context.get().setPacketHandled(true);
    }

    public static void spawnExplosionParticles(double x, double y, double z) {
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            double offsetX = random.nextGaussian() * BobOmb.RANGE;
            double offsetY = random.nextGaussian() * BobOmb.RANGE;
            double offsetZ = random.nextGaussian() * BobOmb.RANGE;

            Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.EXPLOSION, x, y, z, offsetX, offsetY, offsetZ);
        }
    }
}
