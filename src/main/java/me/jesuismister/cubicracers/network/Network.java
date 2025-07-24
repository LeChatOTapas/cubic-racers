package me.jesuismister.cubicracers.network;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.network.message.serverToClient.kartItem.BoostSyncMessage;
import me.jesuismister.cubicracers.network.message.clientToServer.InputSynchMessage;
import me.jesuismister.cubicracers.network.message.clientToServer.KartSynchMessage;
import me.jesuismister.cubicracers.network.message.clientToServer.kartItem.*;
import me.jesuismister.cubicracers.network.message.serverToClient.kartItem.ExplosionParticleMessage;
import me.jesuismister.cubicracers.network.message.serverToClient.KartItemSynchMessage;
import me.jesuismister.cubicracers.network.message.serverToClient.StunMessage;
import me.jesuismister.cubicracers.network.message.serverToClient.kartItem.KlaxonParticleMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class Network {
    private static int i = 0;

    public static int nextId() {
        return i++;
    }

    public static final String NETWORK_VERSION = "0.1.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry
            .newSimpleChannel(new ResourceLocation(CubicRacers.MODID, "network"), () -> NETWORK_VERSION,
                    version -> version.equals(NETWORK_VERSION), version -> version.equals(NETWORK_VERSION));

    public static void init() {
        // Packets client -> server
        CHANNEL.registerMessage(nextId(), InputSynchMessage.class, InputSynchMessage::encode, InputSynchMessage::decode, InputSynchMessage::handle);
        CHANNEL.registerMessage(nextId(), KartSynchMessage.class, KartSynchMessage::encode, KartSynchMessage::decode, KartSynchMessage::handle);
        CHANNEL.registerMessage(nextId(), BananaUseMessage.class, BananaUseMessage::encode, BananaUseMessage::decode, BananaUseMessage::handle);
        CHANNEL.registerMessage(nextId(), GreenShellUseMessage.class, GreenShellUseMessage::encode, GreenShellUseMessage::decode, GreenShellUseMessage::handle);
        CHANNEL.registerMessage(nextId(), FakeBoxUseMessage.class, FakeBoxUseMessage::encode, FakeBoxUseMessage::decode, FakeBoxUseMessage::handle);
        CHANNEL.registerMessage(nextId(), BobOmbUseMessage.class, BobOmbUseMessage::encode, BobOmbUseMessage::decode, BobOmbUseMessage::handle);
        CHANNEL.registerMessage(nextId(), StarUseMessage.class, StarUseMessage::encode, StarUseMessage::decode, StarUseMessage::handle);
        CHANNEL.registerMessage(nextId(), MushroomUseMessage.class, MushroomUseMessage::encode, MushroomUseMessage::decode, MushroomUseMessage::handle);
        CHANNEL.registerMessage(nextId(), BoostSyncMessage.class, BoostSyncMessage::encode, BoostSyncMessage::decode, BoostSyncMessage::handle);
        CHANNEL.registerMessage(nextId(), ThunderUseMessage.class, ThunderUseMessage::encode, ThunderUseMessage::decode, ThunderUseMessage::handle);
        CHANNEL.registerMessage(nextId(), KlaxonUseMessage.class, KlaxonUseMessage::encode, KlaxonUseMessage::decode, KlaxonUseMessage::handle);

        // Packets server -> client
        CHANNEL.registerMessage(nextId(), KartItemSynchMessage.class, KartItemSynchMessage::encode, KartItemSynchMessage::decode, KartItemSynchMessage::handle);
        CHANNEL.registerMessage(nextId(), ExplosionParticleMessage.class, ExplosionParticleMessage::encode, ExplosionParticleMessage::decode, ExplosionParticleMessage::handle);
        CHANNEL.registerMessage(nextId(), StunMessage.class, StunMessage::encode, StunMessage::decode, StunMessage::handle);
        CHANNEL.registerMessage(nextId(), KlaxonParticleMessage.class, KlaxonParticleMessage::encode, KlaxonParticleMessage::decode, KlaxonParticleMessage::handle);
    }
}