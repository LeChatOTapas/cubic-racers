package me.jesuismister.cubicracers.network.message;

import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KartPositionMessage {
    private double xPos;
    private double yPos;
    private double zPos;

    public KartPositionMessage(double xPos, double yPos, double zPos){
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
    }

    public static void encode(KartPositionMessage message, FriendlyByteBuf buffer){
        buffer.writeDouble(message.xPos);
        buffer.writeDouble(message.yPos);
        buffer.writeDouble(message.zPos);
    }

    public static KartPositionMessage decode(FriendlyByteBuf buffer){
        return new KartPositionMessage(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    public static void handle(KartPositionMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                //System.out.println(player.getName().getString() + " / " + kart.getFirstPassenger().getName().getString() + " " + (player.getName().getString().equals(kart.getFirstPassenger().getName().getString())));
                if(player.getName().getString().equals(kart.getFirstPassenger().getName().getString())) {
                    System.out.println(player.getName().getString() + " => " + kart.getPickResult().getDisplayName().getString() + " / " + message.xPos + " " + message.yPos + " " + message.zPos);
                    kart.setPos(message.xPos, Math.floor(message.yPos), message.zPos);
                }
            }
        });
        context.setPacketHandled(true);
    }
}