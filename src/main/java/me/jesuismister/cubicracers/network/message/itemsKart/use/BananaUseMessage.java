package me.jesuismister.cubicracers.network.message.itemsKart.use;

import me.jesuismister.cubicracers.entity.custom.Banana;
import me.jesuismister.cubicracers.entity.custom.ItemKartAbstract;
import me.jesuismister.cubicracers.entity.custom.Kart;
import me.jesuismister.cubicracers.init.KartItemsInit;
import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.util.ClientUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BananaUseMessage {
    private boolean isPressingKeyForward;

    public BananaUseMessage(boolean isPressingKeyForward){
        this.isPressingKeyForward = isPressingKeyForward;
    }

    public static void encode(BananaUseMessage message, FriendlyByteBuf buffer){
        buffer.writeBoolean(message.isPressingKeyForward);
    }

    public static BananaUseMessage decode(FriendlyByteBuf buffer){
        return new BananaUseMessage(buffer.readBoolean());
    }

    public static void handle(BananaUseMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                if(message.isPressingKeyForward){
                    ItemKartAbstract.spawnItemFront(kart, new Banana(KartItemsInit.BANANA.get(), kart.level()));
                    ClientUtil.playSoundToAll(player.level(), player.getX(), player.getY(), player.getZ(), 8, SoundsInit.THROWING_ITEM.get(), SoundSource.RECORDS, 1f, 0.95f);
                }else{
                    ItemKartAbstract.spawnItemBack(kart, new Banana(KartItemsInit.BANANA.get(), kart.level()));
                    ClientUtil.playSoundToAll(player.level(), player.getX(), player.getY(), player.getZ(), 8, SoundsInit.SPAWN_ITEM_BELOW.get(), SoundSource.RECORDS, 1f, 0.95f);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
