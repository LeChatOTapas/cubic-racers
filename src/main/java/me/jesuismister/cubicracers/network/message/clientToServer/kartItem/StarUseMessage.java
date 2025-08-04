package me.jesuismister.cubicracers.network.message.clientToServer.kartItem;

import io.netty.buffer.ByteBuf;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Supplier;

public record StarUseMessage() implements CustomPacketPayload {
    public static final int KLAXON_RANGE = 4;

    public static final CustomPacketPayload.Type<StarUseMessage> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, "StarUseMessage")
    );

    public static final StreamCodec<ByteBuf, StarUseMessage> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public void encode(ByteBuf buf, StarUseMessage msg) {
                }

                @Override
                public StarUseMessage decode(ByteBuf buf) {
                    return new StarUseMessage();
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(StarUseMessage msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if (player.getVehicle() != null && player.getVehicle() instanceof TestKart kart) {
                kart.setTimeStar(20f);
                kart.setStarSpeedBoost(1.5f);
                kart.setInvincible(true);
                kart.setKartItem("None");
            }
        });
    }
}