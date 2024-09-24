package me.jesuismister.cubicracers.network.message.itemsKart.use;

import me.jesuismister.cubicracers.entity.custom.FakeBox;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import me.jesuismister.cubicracers.init.KartItemsInit;
import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.util.ClientUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
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
            if (player.getVehicle() != null && player.getVehicle() instanceof TestKart kart) {
                if (message.isPressingKeyForward) {
                    FakeBox.spawnItemFront(kart, new FakeBox(KartItemsInit.FAKE_BOX.get(), kart.level()));
                    ClientUtil.playSoundToAll(player.level(), player.getX(), player.getY(), player.getZ(), 8, SoundsInit.THROWING_ITEM.get(), SoundSource.RECORDS, 1f, 0.95f);
                } else {
                    FakeBox.spawnItemBack(kart, new FakeBox(KartItemsInit.FAKE_BOX.get(), kart.level()));
                    ClientUtil.playSoundToAll(player.level(), player.getX(), player.getY(), player.getZ(), 8, SoundsInit.SPAWN_ITEM_BELOW.get(), SoundSource.RECORDS, 1f, 0.95f);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
