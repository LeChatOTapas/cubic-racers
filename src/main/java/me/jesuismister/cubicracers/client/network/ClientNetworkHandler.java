package me.jesuismister.cubicracers.client.network;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.event.EventNetworkChannel;

import java.util.function.Consumer;

/**
 * Gestionnaire des paquets personnalisés côté client
 * Cette classe s'occupe uniquement des paquets envoyés via ClientboundCustomPayloadPacket
 */
@Mod.EventBusSubscriber(modid = CubicRacers.MODID, value = Dist.CLIENT)
public class ClientNetworkHandler {

    // Identifiant du canal pour les messages de synchronisation d'items
    private static final ResourceLocation ITEM_SYNC_MESSAGE_ID =
            new ResourceLocation(CubicRacers.MODID, "item_sync_message");

    // Canal réseau pour recevoir les paquets personnalisés
    private static final EventNetworkChannel NETWORK_CHANNEL =
            NetworkRegistry.newEventChannel(ITEM_SYNC_MESSAGE_ID, () -> "1.0.0", s -> true, s -> true);

    /**
     * Initialise le gestionnaire d'événements réseau
     * Cette méthode est appelée au démarrage du mod
     */
    static {
        // Ajouter le listener pour les paquets entrants (côté client)
        NETWORK_CHANNEL.addListener((Consumer<NetworkEvent.ServerCustomPayloadEvent>) ClientNetworkHandler::onCustomPayload);
    }

    /**
     * Gère les paquets personnalisés reçus côté client
     */
    private static void onCustomPayload(NetworkEvent.ServerCustomPayloadEvent event) {
        FriendlyByteBuf buf = event.getPayload();

        // Lire le nom de l'item
        String item = buf.readUtf();

        // Mettre à jour le kart du joueur avec le nouvel item
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            Player player = mc.player;
            if (player.getVehicle() instanceof TestKart kart) {
                // Appliquer l'item au kart
                kart.setKartItem(item);
                System.out.println("[CubicRacers] Item kart mis à jour: " + item);
            }
        }

        // Marquer le message comme traité en utilisant la bonne méthode
        // Dans Forge 1.20.1, on doit utiliser le contexte pour marquer un paquet comme traité
        event.getSource().get().setPacketHandled(true);
    }
}
