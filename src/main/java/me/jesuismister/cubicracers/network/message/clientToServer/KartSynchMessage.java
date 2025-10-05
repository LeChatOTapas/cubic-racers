package me.jesuismister.cubicracers.network.message.clientToServer;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KartSynchMessage {
    public int id;
    public double x;
    public double y;
    public double z;
    public float yRot;

    public KartSynchMessage(int id, double x, double y, double z, float yRot) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yRot = yRot;
    }

    public static void handle(KartSynchMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Utiliser DistExecutor pour isoler le code client
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleOnClient(message));
        });
        context.setPacketHandled(true);
    }

    // Cette méthode ne sera chargée que côté client
    @net.minecraftforge.api.distmarker.OnlyIn(Dist.CLIENT)
    private static void handleOnClient(KartSynchMessage message) {
        try {
            net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
            if (minecraft.player != null && minecraft.player.level().isClientSide()) {
                net.minecraft.world.entity.player.Player p = minecraft.player;
                java.util.List<net.minecraft.world.entity.Entity> nearbyEntities = p.level().getEntities(p, p.getBoundingBox().inflate(200));
                for (net.minecraft.world.entity.Entity e : nearbyEntities) {
                    if (e instanceof TestKart) {
                        TestKart kart = (TestKart) e;
                        if (kart.getId() == message.id) {
                            kart.setPos(message.x, message.y, message.z);
                            kart.setYRot(message.yRot);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Ignorer les erreurs
        }
    }

    public static void encode(KartSynchMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.id);
        buffer.writeDouble(message.x);
        buffer.writeDouble(message.y);
        buffer.writeDouble(message.z);
        buffer.writeFloat(message.yRot);
    }

    public static KartSynchMessage decode(FriendlyByteBuf buffer) {
        return new KartSynchMessage(buffer.readInt(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readFloat());
    }
}
