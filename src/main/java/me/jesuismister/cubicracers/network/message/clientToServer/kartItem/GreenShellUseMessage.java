package me.jesuismister.cubicracers.network.message.clientToServer.kartItem;

import io.netty.buffer.ByteBuf;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.GreenShell;
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

public record GreenShellUseMessage(
        double x,
        double y,
        double z,
        boolean isBack,
        float direction,
        float kartSpeed
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GreenShellUseMessage> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, "GreenShellUseMessage")
    );

    public static final StreamCodec<ByteBuf, GreenShellUseMessage> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public void encode(ByteBuf buf, GreenShellUseMessage msg) {
                    ByteBufCodecs.DOUBLE.encode(buf, msg.x());
                    ByteBufCodecs.DOUBLE.encode(buf, msg.y());
                    ByteBufCodecs.DOUBLE.encode(buf, msg.z());
                    ByteBufCodecs.BOOL.encode(buf, msg.isBack());
                    ByteBufCodecs.FLOAT.encode(buf, msg.direction());
                    ByteBufCodecs.FLOAT.encode(buf, msg.kartSpeed());
                }

                @Override
                public GreenShellUseMessage decode(ByteBuf buf) {
                    return new GreenShellUseMessage(
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

    public static void handle(GreenShellUseMessage msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if (player.getVehicle() != null && player.getVehicle() instanceof TestKart kart)
                kart.setKartItem("None");

            Level level = player.level();
            GreenShell entity = new GreenShell(KartItemsInit.GREEN_SHELL.get(), level);
            entity.setPos(UtilityMethod.getCoordToSpawnItem(msg.x(), msg.y() + 0.5, msg.z(), msg.direction() + 180, msg.isBack()));

            double yawRad = Math.toRadians(msg.direction());
            double speedH = 1.5;
            double xDir = msg.isBack() ? Math.sin(yawRad) : -Math.sin(yawRad);
            double zDir = msg.isBack() ? -Math.cos(yawRad) : Math.cos(yawRad);

            Vec3 velocity = new Vec3(xDir * speedH, 0, zDir * speedH);
            entity.setDeltaMovement(velocity);
            entity.setNoGravity(false);
            level.addFreshEntity(entity);
        });
    }
}