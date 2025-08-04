package me.jesuismister.cubicracers.network;

import me.jesuismister.cubicracers.network.message.clientToServer.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class NetworkEventBus {
    private static final String PROTOCOL_VERSION = "0.1.0";

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(PROTOCOL_VERSION);
        // client → serveur
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

        // serveur → client
        // registrar.playClient()
        // ... et tous tes autres serveur → client
    }
}

//
//
// PacketDistributor.sendToServer
//
//