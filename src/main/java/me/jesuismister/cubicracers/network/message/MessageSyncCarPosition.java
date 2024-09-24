package me.jesuismister.cubicracers.network.message;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;
import de.maxhenkel.corelib.net.Message;

import java.util.Objects;
import java.util.function.Supplier;

public class MessageSyncCarPosition {
    public int id;
    public double x;
    public double y;
    public double z;
    
    public MessageSyncCarPosition(int id, double x, double y, double z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void handle(MessageSyncCarPosition message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            try {
                if (Objects.requireNonNull(context.getSender()).getVehicle() instanceof TestKart kart) {
                    kart.setPos(message.x, message.y, message.z);
                }
            } catch (Exception e) {
                System.out.println("ALED");
            }
        });
    }

    public static void encode(MessageSyncCarPosition message, FriendlyByteBuf buffer){
        buffer.writeInt(message.id);
        buffer.writeDouble(message.x);
        buffer.writeDouble(message.y);
        buffer.writeDouble(message.z);
    }

    public static MessageSyncCarPosition decode(FriendlyByteBuf buffer){
        return new MessageSyncCarPosition(buffer.readInt(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }
}
