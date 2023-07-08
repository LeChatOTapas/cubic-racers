package me.jesuismister.cubicracers.event.network.message;

import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KartMessage {
    public double posX;
    public double posY;
    public double posZ;
    public float rotY;

    public KartMessage(){
    }

    public KartMessage(double posX, double posY, double posZ, float rotY) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.rotY = rotY;
    }

    public static void encode(KartMessage message, FriendlyByteBuf buffer){
        buffer.writeDouble(message.posX);
        buffer.writeDouble(message.posY);
        buffer.writeDouble(message.posZ);
        buffer.writeFloat(message.rotY);
    }

    public static KartMessage decode(FriendlyByteBuf buffer){
        return new KartMessage(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readFloat());
    }

    public static void handle(KartMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player!=null && player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                kart.setPos(message.posX, message.posY, message.posZ);
                kart.setYRot(message.rotY);
            }
        });
        context.setPacketHandled(true);
    }
}
