package me.jesuismister.cubicracers.network.message.serverToClient.kartItem;

import io.netty.buffer.ByteBuf;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.network.message.clientToServer.kartItem.KlaxonUseMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Random;

public record KlaxonParticleMessage(
        double x,
        double y,
        double z
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<KlaxonParticleMessage> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, "KlaxonParticleMessage")
    );

    public static final StreamCodec<ByteBuf, KlaxonParticleMessage> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public void encode(ByteBuf buf, KlaxonParticleMessage msg) {
                    ByteBufCodecs.DOUBLE.encode(buf, msg.x());
                    ByteBufCodecs.DOUBLE.encode(buf, msg.y());
                    ByteBufCodecs.DOUBLE.encode(buf, msg.z());
                }

                @Override
                public KlaxonParticleMessage decode(ByteBuf buf) {
                    return new KlaxonParticleMessage(
                            ByteBufCodecs.DOUBLE.decode(buf),
                            ByteBufCodecs.DOUBLE.decode(buf),
                            ByteBufCodecs.DOUBLE.decode(buf)
                    );
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(KlaxonParticleMessage msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            // Client-side only
            if (ctx.player().level().isClientSide()) {
                if (Minecraft.getInstance().player != null) {
                    spawnKlaxonParticles(msg.x(), msg.y(), msg.z());
                }
            }
        });
    }

    public static void spawnKlaxonParticles(double centerX, double centerY, double centerZ) {
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
