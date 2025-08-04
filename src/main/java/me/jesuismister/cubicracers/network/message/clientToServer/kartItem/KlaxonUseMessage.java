
package me.jesuismister.cubicracers.network.message.clientToServer.kartItem;

import io.netty.buffer.ByteBuf;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.network.message.serverToClient.kartItem.KlaxonParticleMessage;
import me.jesuismister.cubicracers.util.ClientUtil;
import me.jesuismister.cubicracers.util.UtilityMethod;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record KlaxonUseMessage() implements CustomPacketPayload {
    public static final int KLAXON_RANGE = 4;

    public static final CustomPacketPayload.Type<KlaxonUseMessage> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, "KlaxonUseMessage")
    );

    public static final StreamCodec<ByteBuf, KlaxonUseMessage> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public void encode(ByteBuf buf, KlaxonUseMessage msg) {
                }

                @Override
                public KlaxonUseMessage decode(ByteBuf buf) {
                    return new KlaxonUseMessage();
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(KlaxonUseMessage msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if (player.getVehicle() != null && player.getVehicle() instanceof TestKart kart) {
                kart.setKartItem("None");

                UtilityMethod.stunKartAround(kart, KLAXON_RANGE, "Klaxon");

                PacketDistributor.sendToAllPlayers(new KlaxonParticleMessage(kart.getX(), kart.getY(), kart.getZ()));

                ClientUtil.playSoundToAll(kart.level(), kart.getX(), kart.getY(), kart.getZ(), 8 + KLAXON_RANGE, SoundsInit.KLAXON.get(), SoundSource.RECORDS, 1f, 0.95f);
            }
        });
    }
}