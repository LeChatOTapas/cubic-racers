package me.jesuismister.cubicracers.network.message.serverToClient.kartItem;

import me.jesuismister.cubicracers.entity.custom.BobOmb;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

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

    public static void encode(ExplosionParticleMessage message, FriendlyByteBuf buf) {
        buf.writeDouble(message.x);
        buf.writeDouble(message.y);
        buf.writeDouble(message.z);
    }

    public static void handle(ExplosionParticleMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // Utiliser DistExecutor pour exécuter le code client uniquement côté client
            if (context.get().getDirection().getReceptionSide().isClient()) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleOnClient(message));
            }
        });
        context.get().setPacketHandled(true);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        ExplosionParticleMessage.handle(this, context);
    }

    // Cette méthode ne sera chargée que côté client
    @net.minecraftforge.api.distmarker.OnlyIn(Dist.CLIENT)
    private static void handleOnClient(ExplosionParticleMessage message) {
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        if (minecraft.player != null) {
            spawnExplosionParticles(message.x, message.y, message.z);
        }
    }

    // Cette méthode ne sera chargée que côté client
    @net.minecraftforge.api.distmarker.OnlyIn(Dist.CLIENT)
    private static void spawnExplosionParticles(double x, double y, double z) {
        java.util.Random random = new java.util.Random();

        for (int i = 0; i < 10; i++) {
            double offsetX = random.nextGaussian() * BobOmb.RANGE;
            double offsetY = random.nextGaussian() * BobOmb.RANGE;
            double offsetZ = random.nextGaussian() * BobOmb.RANGE;

            net.minecraft.client.Minecraft.getInstance().particleEngine.createParticle(
                net.minecraft.core.particles.ParticleTypes.EXPLOSION, x, y, z, offsetX, offsetY, offsetZ);
        }
    }
}
