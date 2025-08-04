package me.jesuismister.cubicracers.network.message.clientToServer.kartItem;

import io.netty.buffer.ByteBuf;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import me.jesuismister.cubicracers.network.message.serverToClient.StunMessage;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record ThunderUseMessage() implements CustomPacketPayload {
    private static final int THUNDER_RANGE = 500;

    public static final CustomPacketPayload.Type<ThunderUseMessage> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, "ThunderUseMessage")
    );

    public static final StreamCodec<ByteBuf, ThunderUseMessage> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public void encode(ByteBuf buf, ThunderUseMessage msg) {
                }

                @Override
                public ThunderUseMessage decode(ByteBuf buf) {
                    return new ThunderUseMessage();
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ThunderUseMessage msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if (player.getVehicle() != null && player.getVehicle() instanceof TestKart kart) {
                kart.setKartItem("None");

                List<Entity> nearbyEntities = kart.level().getEntities(kart, kart.getBoundingBox().inflate(THUNDER_RANGE));
                for (Entity e : nearbyEntities) {
                    if (e instanceof TestKart target && target.getFirstPassenger() != null) {
                        if (target.getCanMove() && !target.equals(kart)) {
                            TestKart.stunKart(target, "Thunder");
                            // Envoi du message à tous les clients (pour affichage visuel)
                            PacketDistributor.sendToPlayer((ServerPlayer)target.getFirstPassenger(), new StunMessage(target.getId(), "Thunder"));
                        }
                        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(kart.level(), EntitySpawnReason.COMMAND);
                        if (lightning != null) {
                            lightning.setPos(target.getX(), target.getY(), target.getZ());
                            lightning.setVisualOnly(true); // ne fait pas de dégâts
                            target.level().addFreshEntity(lightning);
                        }
                    }
                }
            }
        });
    }
}