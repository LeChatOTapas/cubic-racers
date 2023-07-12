package me.jesuismister.cubicracers.network.message.itemsKart.use;

import me.jesuismister.cubicracers.entity.custom.BobOmb;
import me.jesuismister.cubicracers.entity.custom.Kart;
import me.jesuismister.cubicracers.init.KartItemsInit;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BobOmbUseMessage {
    private boolean isPressingKeyForward;

    public BobOmbUseMessage(boolean isPressingKeyForward) {
        this.isPressingKeyForward = isPressingKeyForward;
    }

    public static void encode(BobOmbUseMessage message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.isPressingKeyForward);
    }

    public static BobOmbUseMessage decode(FriendlyByteBuf buffer) {
        return new BobOmbUseMessage(buffer.readBoolean());
    }

    public static void handle(BobOmbUseMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                if (message.isPressingKeyForward) {
                    BobOmb.spawnItemFront(kart, new BobOmb(KartItemsInit.BOMB_OMB.get(), kart.level()));
                } else {
                    BobOmb.spawnItemBack(kart, new BobOmb(KartItemsInit.BOMB_OMB.get(), kart.level()));
                }
            }
        });
        context.setPacketHandled(true);
    }
}
