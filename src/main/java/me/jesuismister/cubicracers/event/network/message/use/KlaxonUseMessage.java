package me.jesuismister.cubicracers.event.network.message.use;

import me.jesuismister.cubicracers.entity.custom.Kart;
import me.jesuismister.cubicracers.itemKart.Klaxon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KlaxonUseMessage {


    public KlaxonUseMessage(){
    }

    public static void encode(KlaxonUseMessage message, FriendlyByteBuf buffer){
    }

    public static KlaxonUseMessage decode(FriendlyByteBuf buffer){
        return new KlaxonUseMessage();
    }

    public static void handle(KlaxonUseMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                Klaxon.applyKlaxonToOthersKarts(kart);
            }
        });
        context.setPacketHandled(true);
    }
}
