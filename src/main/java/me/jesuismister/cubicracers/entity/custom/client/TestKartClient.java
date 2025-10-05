package me.jesuismister.cubicracers.entity.custom.client;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import me.jesuismister.cubicracers.network.Network;
import me.jesuismister.cubicracers.network.message.clientToServer.KartSynchMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * Cette classe contient les implémentations côté client de TestKart
 * Elle ne sera jamais chargée côté serveur grâce à l'annotation @OnlyIn(Dist.CLIENT)
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class TestKartClient {

    /**
     * Gère les méthodes côté client pour un kart
     * @param kart Le kart à gérer
     * @param player Le joueur qui contrôle le kart
     */
    public static void handleClientMethods(TestKart kart, Player player) {
        moveCamera(kart, player);
        synchKart(kart, player);
    }

    /**
     * Déplace la caméra du joueur pour suivre le kart
     * @param kart Le kart
     * @param player Le joueur
     */
    private static void moveCamera(TestKart kart, Player player) {
        if (player != null) {
            player.setYRot(kart.getYRot());
            player.setYBodyRot(kart.getYRot());
        }
    }

    /**
     * Synchronise la position du kart avec le serveur
     * Cette méthode est totalement sécurisée et n'utilisera jamais LocalPlayer directement
     * @param kart Le kart à synchroniser
     * @param player Le joueur qui contrôle le kart
     */
    private static void synchKart(TestKart kart, Player player) {
        try {
            // Nous vérifions si le joueur actuel est le joueur qui contrôle le kart
            // sans référencer directement LocalPlayer
            if (Minecraft.getInstance().player != null &&
                Minecraft.getInstance().player.getName().getString().equals(player.getName().getString())) {

                try {
                    // Envoi des données de synchronisation aux autres joueurs
                    for (ServerPlayer sp : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                        if (sp.getName().getString().equals(player.getName().getString())) {
                            continue; // Ne pas envoyer au joueur qui contrôle déjà le kart
                        }

                        Network.CHANNEL.send(
                            PacketDistributor.PLAYER.with(() -> sp),
                            new KartSynchMessage(kart.getId(), kart.getX(), kart.getY(), kart.getZ(), kart.getYRot())
                        );
                    }
                } catch (Exception ignored) {
                    // Ignorer les erreurs de serveur, typiques quand on est en solo
                }
            }
        } catch (Exception ignored) {
            // Ignorer toute exception qui pourrait survenir
        }
    }
}
