package me.jesuismister.cubicracers.network.message.clientToServer;

import io.netty.buffer.ByteBuf;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record InputSynchMessage(
        boolean keyAccelerate,
        boolean keyDeccelerate,
        boolean keyForward,
        boolean keyBackward,
        boolean keyLeft,
        boolean keyRight,
        boolean keyDrift,
        boolean keyItem
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<InputSynchMessage> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, "input_synch_message")
    );

    public static final StreamCodec<ByteBuf, InputSynchMessage> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public void encode(ByteBuf buf, InputSynchMessage msg) {
                    ByteBufCodecs.BOOL.encode(buf, msg.keyAccelerate());
                    ByteBufCodecs.BOOL.encode(buf, msg.keyDeccelerate());
                    ByteBufCodecs.BOOL.encode(buf, msg.keyForward());
                    ByteBufCodecs.BOOL.encode(buf, msg.keyBackward());
                    ByteBufCodecs.BOOL.encode(buf, msg.keyLeft());
                    ByteBufCodecs.BOOL.encode(buf, msg.keyRight());
                    ByteBufCodecs.BOOL.encode(buf, msg.keyDrift());
                    ByteBufCodecs.BOOL.encode(buf, msg.keyItem());
                }

                @Override
                public InputSynchMessage decode(ByteBuf buf) {
                    return new InputSynchMessage(
                            ByteBufCodecs.BOOL.decode(buf),
                            ByteBufCodecs.BOOL.decode(buf),
                            ByteBufCodecs.BOOL.decode(buf),
                            ByteBufCodecs.BOOL.decode(buf),
                            ByteBufCodecs.BOOL.decode(buf),
                            ByteBufCodecs.BOOL.decode(buf),
                            ByteBufCodecs.BOOL.decode(buf),
                            ByteBufCodecs.BOOL.decode(buf)
                    );
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(InputSynchMessage msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            System.out.println("RECU");
            var player = ctx.player();
            if (player.getVehicle() instanceof TestKart kart) {
                kart.setPressingKeyAccelerate(msg.keyAccelerate());
                kart.setPressingKeyDeccelerate(msg.keyDeccelerate());
                kart.setPressingKeyForward(msg.keyForward());
                kart.setPressingKeyBackward(msg.keyBackward());
                kart.setPressingKeyLeft(msg.keyLeft());
                kart.setPressingKeyRight(msg.keyRight());
                kart.setPressingKeyDrift(msg.keyDrift());
                kart.setPressingKeyItem(msg.keyItem());
            }
        });
    }
}