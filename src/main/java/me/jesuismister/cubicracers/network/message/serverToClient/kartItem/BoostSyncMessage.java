package me.jesuismister.cubicracers.network.message.serverToClient.kartItem;

import io.netty.buffer.ByteBuf;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record BoostSyncMessage(
        int entityId,
        float timeBoost
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BoostSyncMessage> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, "BoostSyncMessage")
    );

    public static final StreamCodec<ByteBuf, BoostSyncMessage> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public void encode(ByteBuf buf, BoostSyncMessage msg) {
                    ByteBufCodecs.INT.encode(buf, msg.entityId());
                    ByteBufCodecs.FLOAT.encode(buf, msg.timeBoost());
                }

                @Override
                public BoostSyncMessage decode(ByteBuf buf) {
                    return new BoostSyncMessage(
                            ByteBufCodecs.INT.decode(buf),
                            ByteBufCodecs.FLOAT.decode(buf)
                    );
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(BoostSyncMessage msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;
            Entity entity = mc.level.getEntity(msg.entityId());
            if (entity instanceof TestKart kart) {
                kart.setTimeBoost(msg.timeBoost());
            }
        });
    }
}