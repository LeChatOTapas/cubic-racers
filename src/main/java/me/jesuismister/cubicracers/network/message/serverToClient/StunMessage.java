package me.jesuismister.cubicracers.network.message.serverToClient;

import io.netty.buffer.ByteBuf;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record StunMessage(
        int kartId,
        String stunType
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StunMessage> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, "StunMessage")
    );

    public static final StreamCodec<ByteBuf, StunMessage> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public void encode(ByteBuf buf, StunMessage msg) {
                    ByteBufCodecs.INT.encode(buf, msg.kartId());
                    ByteBufCodecs.STRING_UTF8.encode(buf, msg.stunType());
                }

                @Override
                public StunMessage decode(ByteBuf buf) {
                    return new StunMessage(
                            ByteBufCodecs.INT.decode(buf),
                            ByteBufCodecs.STRING_UTF8.decode(buf)
                    );
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(StunMessage msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if (level == null) return;

            Entity entity = level.getEntity(msg.kartId());
            if (entity instanceof TestKart kart) {
                TestKart.stunKart(kart, msg.stunType());
            }
        });
    }
}
