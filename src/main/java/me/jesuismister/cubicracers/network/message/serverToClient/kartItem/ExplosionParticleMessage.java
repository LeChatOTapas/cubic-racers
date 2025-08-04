package me.jesuismister.cubicracers.network.message.serverToClient.kartItem;

import io.netty.buffer.ByteBuf;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.BobOmb;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Random;

public record ExplosionParticleMessage(
        double x,
        double y,
        double z
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ExplosionParticleMessage> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, "ExplosionParticleMessage")
    );

    public static final StreamCodec<ByteBuf, ExplosionParticleMessage> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public void encode(ByteBuf buf, ExplosionParticleMessage msg) {
                    ByteBufCodecs.DOUBLE.encode(buf, msg.x());
                    ByteBufCodecs.DOUBLE.encode(buf, msg.y());
                    ByteBufCodecs.DOUBLE.encode(buf, msg.z());
                }

                @Override
                public ExplosionParticleMessage decode(ByteBuf buf) {
                    return new ExplosionParticleMessage(
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

    public static void handle(ExplosionParticleMessage msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            // Client-side only
            if (ctx.player().level().isClientSide()) {
                if (Minecraft.getInstance().player != null) {
                    spawnExplosionParticles(msg.x(), msg.y(), msg.z());
                }
            }
        });
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
