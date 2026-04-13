package me.jesuismister.cubicracers.entity.custom.client;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

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
}
