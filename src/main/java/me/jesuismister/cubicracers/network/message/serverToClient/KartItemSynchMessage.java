package me.jesuismister.cubicracers.network.message.serverToClient;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
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

    public static void encode(KartItemSynchMessage message, FriendlyByteBuf buf) {
        buf.writeUtf(message.item);
    }

    public static void handle(KartItemSynchMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // On vérifie si on est côté client, mais sans charger de classes client
            if (context.get().getDirection().getReceptionSide().isClient()) {
                // Utiliser DistExecutor pour exécuter le code client uniquement côté client
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleOnClient(message));
            }
        });
        context.get().setPacketHandled(true);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        KartItemSynchMessage.handle(this, context);
    }

    // Cette méthode ne sera chargée que côté client
    @net.minecraftforge.api.distmarker.OnlyIn(Dist.CLIENT)
    private static void handleOnClient(KartItemSynchMessage message) {
        // Code sécurisé qui ne s'exécute que côté client
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        if (minecraft.player != null && minecraft.player.getVehicle() instanceof TestKart kart) {
            kart.setKartItem(message.item);
        }
    }
}
