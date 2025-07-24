package me.jesuismister.cubicracers.network.message.clientToServer;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
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
            try {
                if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.level().isClientSide()) {
                    Player p = Minecraft.getInstance().player;
                    List<Entity> nearbyEntities = p.level().getEntities(p, p.getBoundingBox().inflate(200));
                    for (Entity e : nearbyEntities) {
                        if (e instanceof TestKart) {
                            TestKart kart = (TestKart) e;
                            if (kart.getId() == message.id) {
                                kart.setPos(message.x, message.y, message.z);
                                kart.setYRot(message.yRot);
                                // p.sendSystemMessage(Component.literal("KARTS : " + kart.getX() + " / " + kart.getY() + " / " + kart.getZ()));
                                // p.sendSystemMessage(Component.literal("       " + kart.getYRot() + " / " + kart.getKartItem()));
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
        });
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
