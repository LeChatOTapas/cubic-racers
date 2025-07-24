package me.jesuismister.cubicracers.network.message.serverToClient.kartItem;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
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
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;
            Entity entity = mc.level.getEntity(message.entityId);
            if (entity instanceof TestKart kart) {
                kart.setTimeBoost(message.timeBoost);
            }
        });
        context.setPacketHandled(true);
    }
}
