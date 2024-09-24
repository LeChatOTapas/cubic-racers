package me.jesuismister.cubicracers.network.message;

import de.maxhenkel.corelib.net.Message;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class InputMessage {
    public boolean keyAccelerate;
    public boolean keyDeccelerate;
    public boolean keyForward;
    public boolean keyBackward;
    public boolean keyLeft;
    public boolean keyRight;
    public boolean keyDelta;
    public boolean keyDrift;
    public boolean keyItem;

    public InputMessage(){
    }

    public InputMessage(boolean keyAccelerate, boolean keyDeccelerate, boolean keyForward, boolean keyBackward, boolean keyLeft, boolean keyRight, boolean keyDelta, boolean keyDrift, boolean keyItem){
        this.keyAccelerate = keyAccelerate;
        this.keyDeccelerate = keyDeccelerate;
        this.keyForward = keyForward;
        this.keyBackward = keyBackward;
        this.keyLeft = keyLeft;
        this.keyRight = keyRight;
        this.keyDelta = keyDelta;
        this.keyDrift = keyDrift;
        this.keyItem = keyItem;
    }

    public static void encode(InputMessage message, FriendlyByteBuf buffer){
        buffer.writeBoolean(message.keyAccelerate);
        buffer.writeBoolean(message.keyDeccelerate);

        buffer.writeBoolean(message.keyForward);
        buffer.writeBoolean(message.keyBackward);
        buffer.writeBoolean(message.keyLeft);
        buffer.writeBoolean(message.keyRight);

        buffer.writeBoolean(message.keyDelta);
        buffer.writeBoolean(message.keyDrift);
        buffer.writeBoolean(message.keyItem);
    }

    public static InputMessage decode(FriendlyByteBuf buffer){
        return new InputMessage(buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
    }

    public static void handle(InputMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player!=null && player.getVehicle() != null && player.getVehicle() instanceof TestKart kart) {
                kart.setPressingKeyAccelerate(message.keyAccelerate);
                kart.setPressingKeyDeccelerate(message.keyDeccelerate);
                kart.setPressingKeyForward(message.keyForward);
                kart.setPressingKeyBackward(message.keyBackward);
                kart.setPressingKeyLeft(message.keyLeft);
                kart.setPressingKeyRight(message.keyRight);
                kart.setPressingKeyDelta(message.keyDelta);
                kart.setPressingKeyDrift(message.keyDrift);
                kart.setPressingKeyItem(message.keyItem);
            }
        });
        context.setPacketHandled(true);
    }
}
