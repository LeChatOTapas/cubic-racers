package me.jesuismister.cubicracers.event.network;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.event.network.message.*;
import me.jesuismister.cubicracers.event.network.message.remove.BananaRemoveMessage;
import me.jesuismister.cubicracers.event.network.message.remove.BobOmbRemoveMessage;
import me.jesuismister.cubicracers.event.network.message.remove.GreenShellRemoveMessage;
import me.jesuismister.cubicracers.event.network.message.remove.ItemBoxConsumeMessage;
import me.jesuismister.cubicracers.event.network.message.use.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class Network {
    public static final String NETWORK_VERSION = "0.1.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry
            .newSimpleChannel(new ResourceLocation(CubicRacers.MODID, "network"), () -> NETWORK_VERSION,
                    version -> version.equals(NETWORK_VERSION), version -> version.equals(NETWORK_VERSION));

    public static void init(){
        CHANNEL.registerMessage(0, InputMessage.class, InputMessage::encode, InputMessage::decode, InputMessage::handle);

        CHANNEL.registerMessage(1, BananaRemoveMessage.class, BananaRemoveMessage::encode, BananaRemoveMessage::decode, BananaRemoveMessage::handle);
        CHANNEL.registerMessage(2, GreenShellRemoveMessage.class, GreenShellRemoveMessage::encode, GreenShellRemoveMessage::decode, GreenShellRemoveMessage::handle);
        CHANNEL.registerMessage(3, BobOmbRemoveMessage.class, BobOmbRemoveMessage::encode, BobOmbRemoveMessage::decode, BobOmbRemoveMessage::handle);
        CHANNEL.registerMessage(4, ItemBoxConsumeMessage.class, ItemBoxConsumeMessage::encode, ItemBoxConsumeMessage::decode, ItemBoxConsumeMessage::handle);

        CHANNEL.registerMessage(5, KartMessage.class, KartMessage::encode, KartMessage::decode, KartMessage::handle);

        CHANNEL.registerMessage(6, BananaUseMessage.class, BananaUseMessage::encode, BananaUseMessage::decode, BananaUseMessage::handle);
        CHANNEL.registerMessage(10, GreenShellUseMessage.class, GreenShellUseMessage::encode, GreenShellUseMessage::decode, GreenShellUseMessage::handle);
        CHANNEL.registerMessage(9, BobOmbUseMessage.class, BobOmbUseMessage::encode, BobOmbUseMessage::decode, BobOmbUseMessage::handle);
        CHANNEL.registerMessage(11, FakeBoxUseMessage.class, FakeBoxUseMessage::encode, FakeBoxUseMessage::decode, FakeBoxUseMessage::handle);

        CHANNEL.registerMessage(7, ThunderUseMessage.class, ThunderUseMessage::encode, ThunderUseMessage::decode, ThunderUseMessage::handle);
        CHANNEL.registerMessage(8, KlaxonUseMessage.class, KlaxonUseMessage::encode, KlaxonUseMessage::decode, KlaxonUseMessage::handle);
    }

}
