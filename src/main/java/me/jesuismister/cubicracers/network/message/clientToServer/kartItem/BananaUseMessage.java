package me.jesuismister.cubicracers.network.message.clientToServer.kartItem;

import io.netty.buffer.ByteBuf;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.Banana;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import me.jesuismister.cubicracers.init.KartItemsInit;
import me.jesuismister.cubicracers.util.UtilityMethod;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BananaUseMessage(
        double x,
        double y,
        double z,
        boolean isLobShot,
        float direction,
        float kartSpeed
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BananaUseMessage> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, "BananaUseMessage")
    );

    public static final StreamCodec<ByteBuf, BananaUseMessage> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public void encode(ByteBuf buf, BananaUseMessage msg) {
                    ByteBufCodecs.DOUBLE.encode(buf, msg.x());
                    ByteBufCodecs.DOUBLE.encode(buf, msg.y());
                    ByteBufCodecs.DOUBLE.encode(buf, msg.z());
                    ByteBufCodecs.BOOL.encode(buf, msg.isLobShot());
                    ByteBufCodecs.FLOAT.encode(buf, msg.direction());
                    ByteBufCodecs.FLOAT.encode(buf, msg.kartSpeed());
                }

                @Override
                public BananaUseMessage decode(ByteBuf buf) {
                    return new BananaUseMessage(
                            ByteBufCodecs.DOUBLE.decode(buf),
                            ByteBufCodecs.DOUBLE.decode(buf),
                            ByteBufCodecs.DOUBLE.decode(buf),
                            ByteBufCodecs.BOOL.decode(buf),
                            ByteBufCodecs.FLOAT.decode(buf),
                            ByteBufCodecs.FLOAT.decode(buf)
                    );
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(BananaUseMessage msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if (player.getVehicle() != null && player.getVehicle() instanceof TestKart kart)
                kart.setKartItem("None");

            Level level = player.level();
            Banana entity = new Banana(KartItemsInit.BANANA.get(), level);
            entity.setPos(UtilityMethod.getCoordToSpawnItem(msg.x(), msg.y() + 0.5, msg.z(), msg.direction(), msg.isLobShot()));

            // si tir en cloche, on prépare la vélocité AVANT le spawn
            if (msg.isLobShot()) {
                double yawRad = Math.toRadians(msg.direction());
                double xDir = -Math.sin(yawRad);
                double zDir = Math.cos(yawRad);
                double speedH = 2.5 * (msg.kartSpeed() + 0.25);
                double speedV = 1.75;
                Vec3 velocity = new Vec3(xDir * speedH, speedV, zDir * speedH);

                entity.setDeltaMovement(velocity);
            }
            entity.setNoGravity(false);
            level.addFreshEntity(entity);
        });
    }
}