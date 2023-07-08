package me.jesuismister.cubicracers.event.network.message;

import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class InputMessage {
    public boolean keyUp;
    public boolean keyDown;
    public boolean keyLeft;
    public boolean keyRight;
    public boolean keyDelta;
    public boolean keyDrift;
    public boolean keyItem;

    public InputMessage(){
    }

    public InputMessage(boolean keyUp, boolean keyDown, boolean keyLeft, boolean keyRight, boolean keyDelta, boolean keyDrift, boolean keyItem){
        this.keyUp = keyUp;
        this.keyDown = keyDown;
        this.keyLeft = keyLeft;
        this.keyRight = keyRight;
        this.keyDelta = keyDelta;
        this.keyDrift = keyDrift;
        this.keyItem = keyItem;
    }

    public static void encode(InputMessage message, FriendlyByteBuf buffer){
        buffer.writeBoolean(message.keyUp);
        buffer.writeBoolean(message.keyDown);
        buffer.writeBoolean(message.keyLeft);
        buffer.writeBoolean(message.keyRight);
        buffer.writeBoolean(message.keyDelta);
        buffer.writeBoolean(message.keyDrift);
        buffer.writeBoolean(message.keyItem);
    }

    public static InputMessage decode(FriendlyByteBuf buffer){
        return new InputMessage(buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
    }

    public static void handle(InputMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player!=null && player.getVehicle() != null && player.getVehicle() instanceof Kart kart) {
                kart.isPressingKeyUp = message.keyUp;
                kart.isPressingKeyDown = message.keyDown;
                kart.isPressingKeyLeft = message.keyLeft;
                kart.isPressingKeyRight = message.keyRight;
                kart.isPressingKeyDelta = message.keyDelta;
                kart.isPressingKeyDrift = message.keyDrift;
                kart.isPressingKeyItem = message.keyItem;
            }
        });
        context.setPacketHandled(true);
    }
}
