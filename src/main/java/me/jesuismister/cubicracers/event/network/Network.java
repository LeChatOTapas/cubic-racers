package me.jesuismister.cubicracers.event.network;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.event.network.message.BananaRemoveMessage;
import me.jesuismister.cubicracers.event.network.message.BobOmbRemoveMessage;
import me.jesuismister.cubicracers.event.network.message.GreenShellRemoveMessage;
import me.jesuismister.cubicracers.event.network.message.InputMessage;
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
    }

}
