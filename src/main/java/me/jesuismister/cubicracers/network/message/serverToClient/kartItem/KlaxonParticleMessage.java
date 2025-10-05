package me.jesuismister.cubicracers.network.message.serverToClient.kartItem;

import me.jesuismister.cubicracers.network.message.clientToServer.kartItem.KlaxonUseMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KlaxonParticleMessage {
    private final double x;
    private final double y;
    private final double z;

    public KlaxonParticleMessage(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static KlaxonParticleMessage decode(FriendlyByteBuf buf) {
        return new KlaxonParticleMessage(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static void encode(KlaxonParticleMessage message, FriendlyByteBuf buf) {
        buf.writeDouble(message.x);
        buf.writeDouble(message.y);
        buf.writeDouble(message.z);
    }

    public static void handle(KlaxonParticleMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // Utiliser DistExecutor pour exécuter le code client uniquement côté client
            if (context.get().getDirection().getReceptionSide().isClient()) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleOnClient(message));
            }
        });
        context.get().setPacketHandled(true);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        KlaxonParticleMessage.handle(this, context);
    }

    // Cette méthode ne sera chargée que côté client
    @net.minecraftforge.api.distmarker.OnlyIn(Dist.CLIENT)
    private static void handleOnClient(KlaxonParticleMessage message) {
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        if (minecraft.player != null) {
            spawnKlaxonParticles(message.x, message.y, message.z);
        }
    }

    // Cette méthode ne sera chargée que côté client
    @net.minecraftforge.api.distmarker.OnlyIn(Dist.CLIENT)
    private static void spawnKlaxonParticles(double centerX, double centerY, double centerZ) {
        java.util.Random random = new java.util.Random();
        double outerRadius = KlaxonUseMessage.KLAXON_RANGE;
        double innerRadius = outerRadius / 2;
        int particlesPerCircle = 150; // plus de particules = cercle plus net

        for (int i = 0; i < particlesPerCircle; i++) {
            // --- Cercle extérieur ---
            double angle1 = 2 * Math.PI * random.nextDouble();
            double r1 = outerRadius - 0.3 + random.nextDouble() * 0.6; // épaisseur de bordure ~0.6 bloc

            double x1 = centerX + r1 * Math.cos(angle1);
            double z1 = centerZ + r1 * Math.sin(angle1);
            double y1 = centerY + 0.2 + random.nextDouble() * 0.3;

            net.minecraft.client.Minecraft.getInstance().particleEngine.createParticle(
                    net.minecraft.core.particles.ParticleTypes.CRIT, x1, y1, z1,
                    0, 0.05, 0
            );

            // --- Cercle intérieur ---
            double angle2 = 2 * Math.PI * random.nextDouble();
            double r2 = innerRadius - 0.2 + random.nextDouble() * 0.4; // épaisseur ~0.4 bloc

            double x2 = centerX + r2 * Math.cos(angle2);
            double z2 = centerZ + r2 * Math.sin(angle2);
            double y2 = centerY + 0.2 + random.nextDouble() * 0.3;

            net.minecraft.client.Minecraft.getInstance().particleEngine.createParticle(
                    net.minecraft.core.particles.ParticleTypes.CRIT, x2, y2, z2,
                    0, 0.5, 0
            );
        }
    }
}
