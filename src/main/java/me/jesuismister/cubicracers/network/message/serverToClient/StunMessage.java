package me.jesuismister.cubicracers.network.message.serverToClient;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StunMessage {
    private final int kartId;
    private final String stunType;

    public StunMessage(int kartId, String stunType) {
        this.kartId = kartId;
        this.stunType = stunType;
    }

    public static StunMessage decode(FriendlyByteBuf buf) {
        return new StunMessage(buf.readInt(), buf.readUtf());
    }

    public static void encode(StunMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.kartId);
        buf.writeUtf(message.stunType);
    }

    public static void handle(StunMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            // Utiliser DistExecutor pour exécuter le code client uniquement côté client
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleOnClient(message));
        });
        contextSupplier.get().setPacketHandled(true);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        StunMessage.handle(this, contextSupplier);
    }

    // Cette méthode ne sera chargée que côté client
    @net.minecraftforge.api.distmarker.OnlyIn(Dist.CLIENT)
    private static void handleOnClient(StunMessage message) {
        // Code sécurisé qui ne s'exécute que côté client
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        if (minecraft.level == null) return;

        net.minecraft.world.entity.Entity entity = minecraft.level.getEntity(message.kartId);
        if (entity instanceof TestKart kart) {
            TestKart.stunKart(kart, message.stunType);
        }
    }
}
