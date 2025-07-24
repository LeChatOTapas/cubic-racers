package me.jesuismister.cubicracers.network.message.serverToClient;

import me.jesuismister.cubicracers.entity.custom.BobOmb;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class StunMessage {
    private int kartId;
    private String stunType;

    public StunMessage(int kartId, String stunType) {
        this.kartId = kartId;
        this.stunType = stunType;
    }

    public static StunMessage decode(FriendlyByteBuf buf) {
        return new StunMessage(buf.readInt(), buf.readUtf());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(kartId);
        buf.writeUtf(stunType);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if (level == null) return;

            Entity entity = level.getEntity(kartId);
            if (entity instanceof TestKart kart) {
                TestKart.stunKart(kart, stunType);
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
