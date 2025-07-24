package me.jesuismister.cubicracers.network.message.serverToClient;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KartItemSynchMessage {
    private final String item;

    public KartItemSynchMessage(String value) {
        this.item = value;
    }

    public static KartItemSynchMessage decode(FriendlyByteBuf buf) {
        return new KartItemSynchMessage(buf.readUtf());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(item);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // Client-side only
            if (context.get().getDirection().getReceptionSide().isClient()) {
                if(Minecraft.getInstance().player != null){
                    Player player = Minecraft.getInstance().player;
                    if(player.getVehicle()!=null && player.getVehicle() instanceof TestKart kart){
                        kart.setKartItem(item);
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
