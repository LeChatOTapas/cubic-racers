package me.jesuismister.cubicracers.network.message.serverToClient;

import io.netty.buffer.ByteBuf;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record KartItemSynchMessage(
        String item
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<KartItemSynchMessage> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, "KartItemSynchMessage")
    );

    public static final StreamCodec<ByteBuf, KartItemSynchMessage> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public void encode(ByteBuf buf, KartItemSynchMessage msg) {
                    ByteBufCodecs.STRING_UTF8.encode(buf, msg.item());
                }

                @Override
                public KartItemSynchMessage decode(ByteBuf buf) {
                    return new KartItemSynchMessage(
                            ByteBufCodecs.STRING_UTF8.decode(buf)
                    );
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(KartItemSynchMessage msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            // Client-side only
            if (ctx.player().level().isClientSide()) {
                if(Minecraft.getInstance().player != null){
                    Player player = Minecraft.getInstance().player;
                    if(player.getVehicle()!=null && player.getVehicle() instanceof TestKart kart){
                        kart.setKartItem(msg.item());
                    }
                }
            }
        });
    }
}
