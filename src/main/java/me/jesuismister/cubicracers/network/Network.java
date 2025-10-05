package me.jesuismister.cubicracers.network;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.network.message.clientToServer.InputSynchMessage;
import me.jesuismister.cubicracers.network.message.clientToServer.KartSynchMessage;
import me.jesuismister.cubicracers.network.message.clientToServer.kartItem.*;
import me.jesuismister.cubicracers.network.message.serverToClient.KartItemSynchMessage;
import me.jesuismister.cubicracers.network.message.serverToClient.KartPositionSyncMessage;
import me.jesuismister.cubicracers.network.message.serverToClient.StunMessage;
import me.jesuismister.cubicracers.network.message.serverToClient.kartItem.BoostSyncMessage;
import me.jesuismister.cubicracers.network.message.serverToClient.kartItem.ExplosionParticleMessage;
import me.jesuismister.cubicracers.network.message.serverToClient.kartItem.KlaxonParticleMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Système réseau de Cubic Racers corrigé pour Forge 1.20.1
 * Utilise messageBuilder() pour un enregistrement propre des messages
 */
public class Network {
    private static int packetId = 0;

    private static int nextId() {
        return packetId++;
    }

    public static final String NETWORK_VERSION = "1.0.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CubicRacers.MODID, "network"),
            () -> NETWORK_VERSION,
            NETWORK_VERSION::equals,
            NETWORK_VERSION::equals
    );

    /**
     * Initialise tous les messages réseau
     * Cette méthode doit être appelée pendant FMLCommonSetupEvent
     */
    public static void init() {
        // Messages Client -> Server
        CHANNEL.messageBuilder(InputSynchMessage.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(InputSynchMessage::encode)
                .decoder(InputSynchMessage::decode)
                .consumerMainThread(InputSynchMessage::handle)
                .add();

        CHANNEL.messageBuilder(KartSynchMessage.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(KartSynchMessage::encode)
                .decoder(KartSynchMessage::decode)
                .consumerMainThread(KartSynchMessage::handle)
                .add();

        CHANNEL.messageBuilder(BananaUseMessage.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(BananaUseMessage::encode)
                .decoder(BananaUseMessage::decode)
                .consumerMainThread(BananaUseMessage::handle)
                .add();

        CHANNEL.messageBuilder(GreenShellUseMessage.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(GreenShellUseMessage::encode)
                .decoder(GreenShellUseMessage::decode)
                .consumerMainThread(GreenShellUseMessage::handle)
                .add();

        CHANNEL.messageBuilder(FakeBoxUseMessage.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(FakeBoxUseMessage::encode)
                .decoder(FakeBoxUseMessage::decode)
                .consumerMainThread(FakeBoxUseMessage::handle)
                .add();

        CHANNEL.messageBuilder(BobOmbUseMessage.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(BobOmbUseMessage::encode)
                .decoder(BobOmbUseMessage::decode)
                .consumerMainThread(BobOmbUseMessage::handle)
                .add();

        CHANNEL.messageBuilder(StarUseMessage.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(StarUseMessage::encode)
                .decoder(StarUseMessage::decode)
                .consumerMainThread(StarUseMessage::handle)
                .add();

        CHANNEL.messageBuilder(MushroomUseMessage.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(MushroomUseMessage::encode)
                .decoder(MushroomUseMessage::decode)
                .consumerMainThread(MushroomUseMessage::handle)
                .add();

        CHANNEL.messageBuilder(ThunderUseMessage.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(ThunderUseMessage::encode)
                .decoder(ThunderUseMessage::decode)
                .consumerMainThread(ThunderUseMessage::handle)
                .add();

        CHANNEL.messageBuilder(KlaxonUseMessage.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(KlaxonUseMessage::encode)
                .decoder(KlaxonUseMessage::decode)
                .consumerMainThread(KlaxonUseMessage::handle)
                .add();

        // Messages Server -> Client
        CHANNEL.messageBuilder(BoostSyncMessage.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(BoostSyncMessage::encode)
                .decoder(BoostSyncMessage::decode)
                .consumerMainThread(BoostSyncMessage::handle)
                .add();

        CHANNEL.messageBuilder(KartItemSynchMessage.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(KartItemSynchMessage::encode)
                .decoder(KartItemSynchMessage::decode)
                .consumerMainThread((msg, ctx) -> KartItemSynchMessage.handle(msg, ctx))
                .add();

        CHANNEL.messageBuilder(ExplosionParticleMessage.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ExplosionParticleMessage::encode)
                .decoder(ExplosionParticleMessage::decode)
                .consumerMainThread((msg, ctx) -> ExplosionParticleMessage.handle(msg, ctx))
                .add();

        CHANNEL.messageBuilder(StunMessage.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(StunMessage::encode)
                .decoder(StunMessage::decode)
                .consumerMainThread((msg, ctx) -> StunMessage.handle(msg, ctx))
                .add();

        CHANNEL.messageBuilder(KlaxonParticleMessage.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(KlaxonParticleMessage::encode)
                .decoder(KlaxonParticleMessage::decode)
                .consumerMainThread((msg, ctx) -> KlaxonParticleMessage.handle(msg, ctx))
                .add();

        // Nouveau message de synchronisation de position (Server -> Client)
        CHANNEL.messageBuilder(KartPositionSyncMessage.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(KartPositionSyncMessage::encode)
                .decoder(KartPositionSyncMessage::decode)
                .consumerMainThread(KartPositionSyncMessage::handle)
                .add();

        System.out.println("[CubicRacers] Network channels registered successfully! Total messages: " + packetId);
    }

    /**
     * Envoie un message de synchronisation d'item de kart à un joueur spécifique
     * @param player Le joueur qui doit recevoir le message
     * @param item L'item à synchroniser
     */
    public static void sendKartItemSynchMessage(ServerPlayer player, String item) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new KartItemSynchMessage(item));
    }

    /**
     * Envoie un message de stun à tous les joueurs
     * @param kartId L'ID de l'entité kart qui est stunned
     * @param stunType Le type de stun
     */
    public static void sendStunMessage(int kartId, String stunType) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), new StunMessage(kartId, stunType));
    }

    /**
     * Envoie la position d'un kart à tous les joueurs qui le trackent
     * @param kart L'entité kart à synchroniser
     */
    public static void sendKartPositionSync(net.minecraft.world.entity.Entity kart) {
        if (kart.level().isClientSide()) return; // Ne pas envoyer depuis le client

        Vec3 velocity = kart.getDeltaMovement();
        float speed = 0;

        // Récupérer la vitesse si c'est un TestKart
        if (kart instanceof me.jesuismister.cubicracers.entity.custom.TestKart testKart) {
            speed = testKart.getSpeed();
        }

        CHANNEL.send(
            PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> kart),
            new KartPositionSyncMessage(
                kart.getId(),
                kart.getX(), kart.getY(), kart.getZ(),
                kart.getYRot(), kart.getXRot(),
                velocity.x, velocity.y, velocity.z,
                speed
            )
        );
    }
}
