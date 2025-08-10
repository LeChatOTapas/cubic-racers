package me.jesuismister.cubicracers.network;

import me.jesuismister.cubicracers.network.message.clientToServer.*;
import me.jesuismister.cubicracers.network.message.serverToClient.KartItemSynchMessage;
import me.jesuismister.cubicracers.network.message.serverToClient.StunMessage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class NetworkEventBus {
    private static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(PROTOCOL_VERSION);
        // client -> server
        registrar.playToServer(
                InputSynchMessage.TYPE,
                InputSynchMessage.STREAM_CODEC,
                InputSynchMessage::handle
        );
        registrar.playToServer(
                KartSynchMessage.TYPE,
                KartSynchMessage.STREAM_CODEC,
                KartSynchMessage::handle
        );

        // server -> client
        registrar.playToClient(
                KartItemSynchMessage.TYPE,
                KartItemSynchMessage.STREAM_CODEC,
                KartItemSynchMessage::handle
        );
        registrar.playToClient(
                StunMessage.TYPE,
                StunMessage.STREAM_CODEC,
                StunMessage::handle
        );
    }
}