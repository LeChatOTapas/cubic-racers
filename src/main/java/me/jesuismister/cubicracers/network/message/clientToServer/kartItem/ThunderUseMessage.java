package me.jesuismister.cubicracers.network.message.clientToServer.kartItem;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import me.jesuismister.cubicracers.network.Network;
import me.jesuismister.cubicracers.network.message.serverToClient.StunMessage;
import me.jesuismister.cubicracers.util.UtilityMethod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.function.Supplier;

public class ThunderUseMessage {
    private static final int THUNDER_RANGE = 500;

    public ThunderUseMessage() {
    }

    public static void encode(ThunderUseMessage message, FriendlyByteBuf buffer) {
    }

    public static ThunderUseMessage decode(FriendlyByteBuf buffer) {
        return new ThunderUseMessage();
    }

    public static void handle(ThunderUseMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (player.getVehicle() != null && player.getVehicle() instanceof TestKart kart) {
                kart.setKartItem("None");

                List<Entity> nearbyEntities = kart.level().getEntities(kart, kart.getBoundingBox().inflate(THUNDER_RANGE));
                for (Entity e : nearbyEntities) {
                    if (e instanceof TestKart target && target.getFirstPassenger() != null) {
                        if (target.getCanMove() && !target.equals(kart)) {
                            TestKart.stunKart(target, "Thunder");
                            // Envoi du message à tous les clients (pour affichage visuel)
                            Network.CHANNEL.send(
                                    PacketDistributor.TRACKING_ENTITY.with(() -> kart),
                                    new StunMessage(target.getId(), "Thunder")
                            );
                        }
                        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(kart.level());
                        if (lightning != null) {
                            lightning.setPos(target.getX(), target.getY(), target.getZ());
                            lightning.setVisualOnly(true); // ne fait pas de dégâts
                            target.level().addFreshEntity(lightning);
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
