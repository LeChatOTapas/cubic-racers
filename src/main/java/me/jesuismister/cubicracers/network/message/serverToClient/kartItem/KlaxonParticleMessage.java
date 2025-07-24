package me.jesuismister.cubicracers.network.message.serverToClient.kartItem;

import me.jesuismister.cubicracers.entity.custom.BobOmb;
import me.jesuismister.cubicracers.network.message.clientToServer.kartItem.KlaxonUseMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
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
                    spawnKlaxonParticles(x, y, z);
                }
            }
        });
        context.get().setPacketHandled(true);
    }

    public void spawnKlaxonParticles(double centerX, double centerY, double centerZ) {
        Random random = new Random();
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

            Minecraft.getInstance().particleEngine.createParticle(
                    ParticleTypes.CRIT, x1, y1, z1,
                    0, 0.05, 0
            );

            // --- Cercle intérieur ---
            double angle2 = 2 * Math.PI * random.nextDouble();
            double r2 = innerRadius - 0.2 + random.nextDouble() * 0.4; // épaisseur ~0.4 bloc

            double x2 = centerX + r2 * Math.cos(angle2);
            double z2 = centerZ + r2 * Math.sin(angle2);
            double y2 = centerY + 0.2 + random.nextDouble() * 0.3;

            Minecraft.getInstance().particleEngine.createParticle(
                    ParticleTypes.CRIT, x2, y2, z2,
                    0, 0.5, 0
            );
        }
    }
}
