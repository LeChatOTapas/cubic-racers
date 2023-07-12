package me.jesuismister.cubicracers.network;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.network.message.InputMessage;
import me.jesuismister.cubicracers.network.message.itemsKart.particles.ExplosionParticleMessage;
import me.jesuismister.cubicracers.network.message.itemsKart.particles.KlaxonParticleMessage;
import me.jesuismister.cubicracers.network.message.itemsKart.use.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class Network {
    private static int i = 0;
    public static final String NETWORK_VERSION = "0.1.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry
            .newSimpleChannel(new ResourceLocation(CubicRacers.MODID, "network"), () -> NETWORK_VERSION,
                    version -> version.equals(NETWORK_VERSION), version -> version.equals(NETWORK_VERSION));

    public static void init(){
        CHANNEL.registerMessage(i++, InputMessage.class, InputMessage::encode, InputMessage::decode, InputMessage::handle);

        CHANNEL.registerMessage(i++, BananaUseMessage.class, BananaUseMessage::encode, BananaUseMessage::decode, BananaUseMessage::handle);
        CHANNEL.registerMessage(i++, GreenShellUseMessage.class, GreenShellUseMessage::encode, GreenShellUseMessage::decode, GreenShellUseMessage::handle);
        CHANNEL.registerMessage(i++, BobOmbUseMessage.class, BobOmbUseMessage::encode, BobOmbUseMessage::decode, BobOmbUseMessage::handle);
        CHANNEL.registerMessage(i++, FakeBoxUseMessage.class, FakeBoxUseMessage::encode, FakeBoxUseMessage::decode, FakeBoxUseMessage::handle);
        CHANNEL.registerMessage(i++, ThunderUseMessage.class, ThunderUseMessage::encode, ThunderUseMessage::decode, ThunderUseMessage::handle);
        CHANNEL.registerMessage(i++, ThunderUseMessage.class, ThunderUseMessage::encode, ThunderUseMessage::decode, ThunderUseMessage::handle);
        CHANNEL.registerMessage(i++, KlaxonUseMessage.class, KlaxonUseMessage::encode, KlaxonUseMessage::decode, KlaxonUseMessage::handle);

        CHANNEL.registerMessage(i++, ExplosionParticleMessage.class, ExplosionParticleMessage::encode, ExplosionParticleMessage::decode, ExplosionParticleMessage::handle);
        CHANNEL.registerMessage(i++, KlaxonParticleMessage.class, KlaxonParticleMessage::encode, KlaxonParticleMessage::decode, KlaxonParticleMessage::handle);
    }

}
