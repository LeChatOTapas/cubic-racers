package me.jesuismister.cubicracers.network.message.clientToServer.kartItem;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.Banana;
import me.jesuismister.cubicracers.entity.custom.ItemKartAbstract;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import me.jesuismister.cubicracers.init.ItemInit;
import me.jesuismister.cubicracers.init.KartInit;
import me.jesuismister.cubicracers.init.KartItemsInit;
import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.util.ClientUtil;
import me.jesuismister.cubicracers.util.UtilityMethod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BananaUseMessage {
    private double x;
    private double y;
    private double z;
    private boolean isLobShot;
    private float direction;
    private float kartSpeed;

    public BananaUseMessage(double x, double y, double z, boolean isLobShot, float direction, float kartSpeed) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.isLobShot = isLobShot;
        this.direction = direction;
        this.kartSpeed = kartSpeed;
    }

    public static void encode(BananaUseMessage message, FriendlyByteBuf buffer) {
        buffer.writeDouble(message.x);
        buffer.writeDouble(message.y);
        buffer.writeDouble(message.z);
        buffer.writeBoolean(message.isLobShot);
        buffer.writeFloat(message.direction);
        buffer.writeFloat(message.kartSpeed);
    }

    public static BananaUseMessage decode(FriendlyByteBuf buffer) {
        return new BananaUseMessage(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readBoolean(), buffer.readFloat(), buffer.readFloat());
    }

    public static void handle(BananaUseMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            if (player.getVehicle() != null && player.getVehicle() instanceof TestKart kart)
                kart.setKartItem("None");

            Level level = player.serverLevel();
            Banana entity = new Banana(KartItemsInit.BANANA.get(), level);
            entity.setPos(UtilityMethod.getCoordToSpawnItem(message.x, message.y + 0.5, message.z, message.direction, message.isLobShot));

            // si tir en cloche, on prépare la vélocité AVANT le spawn
            if (message.isLobShot) {
                double yawRad = Math.toRadians(message.direction);
                double xDir = -Math.sin(yawRad);
                double zDir = Math.cos(yawRad);
                double speedH = 2.5 * (message.kartSpeed + 0.25);
                double speedV = 1.75;
                Vec3 velocity = new Vec3(xDir * speedH, speedV, zDir * speedH);

                entity.setDeltaMovement(velocity);
            }
            entity.setNoGravity(false);
            level.addFreshEntity(entity);
        });
        context.setPacketHandled(true);
    }
}
