package me.jesuismister.cubicracers.network.message.clientToServer;

import io.netty.buffer.ByteBuf;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record KartSynchMessage(
        int id,
        double x,
        double y,
        double z,
        float yRot
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<KartSynchMessage> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, "kart_synch_message")
    );

    public static final StreamCodec<ByteBuf, KartSynchMessage> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public void encode(ByteBuf buf, KartSynchMessage msg) {
                    ByteBufCodecs.INT.encode(buf, msg.id());
                    ByteBufCodecs.DOUBLE.encode(buf, msg.x());
                    ByteBufCodecs.DOUBLE.encode(buf, msg.y());
                    ByteBufCodecs.DOUBLE.encode(buf, msg.z());
                    ByteBufCodecs.FLOAT.encode(buf, msg.yRot());
                }

                @Override
                public KartSynchMessage decode(ByteBuf buf) {
                    return new KartSynchMessage(
                            ByteBufCodecs.INT.decode(buf),
                            ByteBufCodecs.DOUBLE.decode(buf),
                            ByteBufCodecs.DOUBLE.decode(buf),
                            ByteBufCodecs.DOUBLE.decode(buf),
                            ByteBufCodecs.FLOAT.decode(buf)
                    );
                }
            };


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(KartSynchMessage msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            try {
                if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.level().isClientSide()) {
                    Player p = Minecraft.getInstance().player;
                    List<Entity> nearbyEntities = p.level().getEntities(p, p.getBoundingBox().inflate(200));
                    for (Entity e : nearbyEntities) {
                        if (e instanceof TestKart) {
                            TestKart kart = (TestKart) e;
                            if (kart.getId() == msg.id) {
                                kart.setPos(msg.x, msg.y, msg.z);
                                kart.setYRot(msg.yRot);
                                // p.sendSystemMessage(Component.literal("KARTS : " + kart.getX() + " / " + kart.getY() + " / " + kart.getZ()));
                                // p.sendSystemMessage(Component.literal("       " + kart.getYRot() + " / " + kart.getKartItem()));
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
        });
    }
}