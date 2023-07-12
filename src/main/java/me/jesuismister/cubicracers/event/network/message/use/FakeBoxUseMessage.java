package me.jesuismister.cubicracers.event.network.message.use;

import me.jesuismister.cubicracers.entity.custom.FakeBox;
import me.jesuismister.cubicracers.entity.custom.Kart;
import me.jesuismister.cubicracers.init.KartItemsInit;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FakeBoxUseMessage {
    private boolean isPressingKeyForward;

    public FakeBoxUseMessage(boolean isPressingKeyForward){
        this.isPressingKeyForward = isPressingKeyForward;
    }

    public static void encode(FakeBoxUseMessage message, FriendlyByteBuf buffer){
        buffer.writeBoolean(message.isPressingKeyForward);
    }

    public static FakeBoxUseMessage decode(FriendlyByteBuf buffer){
        return new FakeBoxUseMessage(buffer.readBoolean());
    }

    public static void handle(FakeBoxUseMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                if (message.isPressingKeyForward) {
                    FakeBox.spawnItemFront(kart, new FakeBox(KartItemsInit.FAKE_BOX.get(), kart.level()));
                } else {
                    FakeBox.spawnItemBack(kart, new FakeBox(KartItemsInit.FAKE_BOX.get(), kart.level()));
                }
            }
        });
        context.setPacketHandled(true);
    }
}
