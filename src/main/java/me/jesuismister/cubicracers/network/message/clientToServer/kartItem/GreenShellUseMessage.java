package me.jesuismister.cubicracers.network.message.clientToServer.kartItem;

import me.jesuismister.cubicracers.entity.custom.Banana;
import me.jesuismister.cubicracers.entity.custom.GreenShell;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import me.jesuismister.cubicracers.init.KartItemsInit;
import me.jesuismister.cubicracers.util.UtilityMethod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GreenShellUseMessage {
    private double x;
    private double y;
    private double z;
    private boolean isBack;
    private float direction;
    private float kartSpeed;

    public GreenShellUseMessage(double x, double y, double z, boolean isBack, float direction, float kartSpeed) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.isBack = isBack;
        this.direction = direction;
        this.kartSpeed = kartSpeed;
    }

    public static void encode(GreenShellUseMessage message, FriendlyByteBuf buffer) {
        buffer.writeDouble(message.x);
        buffer.writeDouble(message.y);
        buffer.writeDouble(message.z);
        buffer.writeBoolean(message.isBack);
        buffer.writeFloat(message.direction);
        buffer.writeFloat(message.kartSpeed);
    }

    public static GreenShellUseMessage decode(FriendlyByteBuf buffer) {
        return new GreenShellUseMessage(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readBoolean(), buffer.readFloat(), buffer.readFloat());
    }

    public static void handle(GreenShellUseMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            if (player.getVehicle() != null && player.getVehicle() instanceof TestKart kart)
                kart.setKartItem("None");

            Level level = player.serverLevel();
            GreenShell entity = new GreenShell(KartItemsInit.GREEN_SHELL.get(), level);
            entity.setPos(UtilityMethod.getCoordToSpawnItem(message.x, message.y + 0.5, message.z, message.direction + 180, message.isBack));

            double yawRad = Math.toRadians(message.direction);
            double speedH = 1.5;
            double xDir = message.isBack ? Math.sin(yawRad) : -Math.sin(yawRad);
            double zDir = message.isBack ? -Math.cos(yawRad) : Math.cos(yawRad);

            Vec3 velocity = new Vec3(xDir * speedH, 0, zDir * speedH);
            entity.setDeltaMovement(velocity);
            entity.setNoGravity(false);
            level.addFreshEntity(entity);
        });
        context.setPacketHandled(true);
    }
}
