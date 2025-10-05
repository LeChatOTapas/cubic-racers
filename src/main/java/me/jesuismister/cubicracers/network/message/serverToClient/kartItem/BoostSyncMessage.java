package me.jesuismister.cubicracers.network.message.serverToClient.kartItem;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BoostSyncMessage {
    private final int entityId;
    private final float timeBoost;

    public BoostSyncMessage(int entityId, float timeBoost) {
        this.entityId = entityId;
        this.timeBoost = timeBoost;
    }

    public static void encode(BoostSyncMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.entityId);
        buffer.writeFloat(message.timeBoost);
    }

    public static BoostSyncMessage decode(FriendlyByteBuf buffer) {
        return new BoostSyncMessage(buffer.readInt(), buffer.readFloat());
    }

    public static void handle(BoostSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Utiliser DistExecutor pour exécuter le code client uniquement côté client
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleOnClient(message));
        });
        context.setPacketHandled(true);
    }

    // Cette méthode ne sera chargée que côté client
    @net.minecraftforge.api.distmarker.OnlyIn(Dist.CLIENT)
    private static void handleOnClient(BoostSyncMessage message) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.level == null) return;
        net.minecraft.world.entity.Entity entity = mc.level.getEntity(message.entityId);
        if (entity instanceof TestKart kart) {
            kart.setTimeBoost(message.timeBoost);
        }
    }
}
