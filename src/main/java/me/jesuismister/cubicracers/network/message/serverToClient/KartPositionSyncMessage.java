package me.jesuismister.cubicracers.network.message.serverToClient;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Message envoyé du serveur vers tous les clients pour synchroniser la position d'un kart
 * Ce message est envoyé régulièrement pour assurer une synchronisation parfaite
 */
public class KartPositionSyncMessage {
    private final int kartId;
    private final double x;
    private final double y;
    private final double z;
    private final float yRot;
    private final float xRot;
    private final double velocityX;
    private final double velocityY;
    private final double velocityZ;
    private final float speed;

    public KartPositionSyncMessage(int kartId, double x, double y, double z, float yRot, float xRot,
                                   double velocityX, double velocityY, double velocityZ, float speed) {
        this.kartId = kartId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yRot = yRot;
        this.xRot = xRot;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.speed = speed;
    }

    public static void encode(KartPositionSyncMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.kartId);
        buffer.writeDouble(message.x);
        buffer.writeDouble(message.y);
        buffer.writeDouble(message.z);
        buffer.writeFloat(message.yRot);
        buffer.writeFloat(message.xRot);
        buffer.writeDouble(message.velocityX);
        buffer.writeDouble(message.velocityY);
        buffer.writeDouble(message.velocityZ);
        buffer.writeFloat(message.speed);
    }

    public static KartPositionSyncMessage decode(FriendlyByteBuf buffer) {
        return new KartPositionSyncMessage(
                buffer.readInt(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readFloat()
        );
    }

    public static void handle(KartPositionSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Exécuter uniquement côté client
            if (context.getDirection().getReceptionSide().isClient()) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleOnClient(message));
            }
        });
        context.setPacketHandled(true);
    }

    @net.minecraftforge.api.distmarker.OnlyIn(Dist.CLIENT)
    private static void handleOnClient(KartPositionSyncMessage message) {
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) return;

        // Ne pas synchroniser notre propre kart (le joueur local contrôle directement)
        net.minecraft.world.entity.Entity entity = minecraft.level.getEntity(message.kartId);

        if (entity instanceof TestKart kart) {
            // Ne synchroniser que si ce n'est pas notre kart
            if (kart.getFirstPassenger() != minecraft.player) {
                // Utiliser lerp pour une interpolation fluide (5 ticks = ~250ms)
                kart.lerpTo(message.x, message.y, message.z, message.yRot, message.xRot, 5, true);

                // Mettre à jour la vélocité pour des mouvements plus fluides
                kart.setDeltaMovement(message.velocityX, message.velocityY, message.velocityZ);

                // Synchroniser la vitesse affichée
                kart.setSpeed(message.speed);
            }
        }
    }
}

