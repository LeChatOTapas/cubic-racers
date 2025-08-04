package me.jesuismister.cubicracers.network.message.clientToServer.kartItem;

import io.netty.buffer.ByteBuf;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import me.jesuismister.cubicracers.network.message.serverToClient.kartItem.BoostSyncMessage;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Supplier;

public record MushroomUseMessage() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MushroomUseMessage> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, "MushroomUseMessage")
    );

    public static final StreamCodec<ByteBuf, MushroomUseMessage> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public void encode(ByteBuf buf, MushroomUseMessage msg) {
                }

                @Override
                public MushroomUseMessage decode(ByteBuf buf) {
                    return new MushroomUseMessage();
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MushroomUseMessage msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if (player.getVehicle() != null && player.getVehicle() instanceof TestKart kart){
                kart.setTimeBoost(5.f);
                kart.setSpeed(kart.getMAX_SPEED() + kart.getBOOST());
                kart.setKartItem("None");

                PacketDistributor.sendToAllPlayers(new BoostSyncMessage(kart.getId(), kart.getTimeBoost()));
            }
        });
    }
}