package me.jesuismister.cubicracers.entity.custom.client;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.network.Network;
import me.jesuismister.cubicracers.network.message.clientToServer.KartSynchMessage;
import me.jesuismister.cubicracers.sounds.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * Classe qui gère toutes les fonctionnalités spécifiques au client pour TestKart
 * Cette classe n'est jamais chargée côté serveur
 */
@OnlyIn(Dist.CLIENT)
public class TestKartClientHandler {

    // Variables de son
    private static SoundEngineIdle engineIdleLoop;
    private static SoundEngineMax engineMaxLoop;
    private static SoundStarMode starModeLoop;
    private static SoundKartGliding kartGliding;
    private static SoundKartDrifting kartDrifting;
    private static SoundKartOffRoad kartOffRoad;

    /**
     * Gère le tick côté client pour un kart
     * @param kart Le kart à gérer
     */
    public static void handleClientTick(TestKart kart) {
        updateSounds(kart);
    }

    /**
     * Gère les contrôles joueur côté client
     * @param kart Le kart
     * @param player Le joueur qui contrôle le kart
     */
    public static void handlePlayerControl(TestKart kart, Player player) {
        // Déplace la caméra du joueur
        moveCamera(kart, player);

        // Synchronise la position avec le serveur
        syncKartPosition(kart, player);
    }

    /**
     * Déplace la caméra du joueur pour suivre le kart
     * @param kart Le kart
     * @param player Le joueur
     */
    private static void moveCamera(TestKart kart, Player player) {
        player.setYRot(kart.getYRot());
        player.setYBodyRot(kart.getYRot());
    }

    /**
     * Synchronise la position du kart avec les autres joueurs via le réseau
     * @param kart Le kart à synchroniser
     * @param player Le joueur qui contrôle le kart
     */
    private static void syncKartPosition(TestKart kart, Player player) {
        try {
            if (Minecraft.getInstance().player != null &&
                Minecraft.getInstance().player.getName().getString().equals(player.getName().getString())) {

                try {
                    // Envoie la position aux autres joueurs
                    if (ServerLifecycleHooks.getCurrentServer() != null) {
                        for (ServerPlayer sp : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                            if (!sp.getName().getString().equals(player.getName().getString())) {
                                Network.CHANNEL.send(
                                    PacketDistributor.PLAYER.with(() -> sp),
                                    new KartSynchMessage(kart.getId(), kart.getX(), kart.getY(), kart.getZ(), kart.getYRot())
                                );
                            }
                        }
                    }
                } catch (Exception ignored) {
                    // Cette exception est ignorée car elle se produit en mode solo
                }
            }
        } catch (Exception ignored) {
            // Ignore les exceptions potentielles
        }
    }

    /**
     * Met à jour les sons du kart
     * @param kart Le kart
     */
    private static void updateSounds(TestKart kart) {
        if (!kart.level().isClientSide) return;

        if (kart.isInvinsible()) {
            if (!isSoundPlaying(starModeLoop)) {
                starModeLoop = new SoundStarMode(kart, SoundsInit.STAR_MODE.get(), SoundSource.RECORDS);
                SoundsInit.playSoundLoop(starModeLoop, kart.level());
            }
        } else if (kart.isDeltaOn()) {
            if (!isSoundPlaying(kartGliding)) {
                kartGliding = new SoundKartGliding(kart, SoundsInit.KART_GLIDING.get(), SoundSource.RECORDS);
                SoundsInit.playSoundLoop(kartGliding, kart.level());
            }
        } else {
            // ARRET OU EN MOUVEMENT
            if (Math.abs(kart.getSpeed()) <= 0.2f) {
                if (!isSoundPlaying(engineIdleLoop)) {
                    engineIdleLoop = new SoundEngineIdle(kart, SoundsInit.ENGINE_IDLE.get(), SoundSource.RECORDS);
                    SoundsInit.playSoundLoop(engineIdleLoop, kart.level());
                }
            } else if (!kart.isOnRoadBlock() && kart.onGround() && Math.abs(kart.getSpeed()) > 0.2f) {
                if (!isSoundPlaying(kartOffRoad)) {
                    kartOffRoad = new SoundKartOffRoad(kart, SoundsInit.KART_OFF_ROAD.get(), SoundSource.RECORDS);
                    SoundsInit.playSoundLoop(kartOffRoad, kart.level());
                }
            } else if (kart.isOnRoadBlock() && kart.onGround() && Math.abs(kart.getSpeed()) > 0.2f) {
                if (!isSoundPlaying(engineMaxLoop)) {
                    engineMaxLoop = new SoundEngineMax(kart, SoundsInit.ENGINE_MAX.get(), SoundSource.RECORDS);
                    SoundsInit.playSoundLoop(engineMaxLoop, kart.level());
                }

                // DRIFTING OU PAS DRIFTING
                if (kart.isDrifting()) {
                    if (!isSoundPlaying(kartDrifting)) {
                        kartDrifting = new SoundKartDrifting(kart, SoundsInit.KART_DRIFTING.get(), SoundSource.RECORDS);
                        SoundsInit.playSoundLoop(kartDrifting, kart.level());
                    }
                }
            }
        }

        // BOOST DE VITESSE
        if (kart.boostFini && (kart.getTimeBoost() > 0 || kart.getDriftTimeBoost() > 0)) {
            kart.boostFini = false;
            if (kart.getFirstPassenger() != null && kart.getFirstPassenger() instanceof Player player)
                SoundsInit.playSound(SoundsInit.KART_SPEED_BOOST.get(), kart.level(), new BlockPos((int) kart.getX(), (int) kart.getY(), (int) kart.getZ()), player, SoundSource.RECORDS, 1f);
        } else if (!kart.boostFini && (kart.getTimeBoost() <= 0 && kart.getDriftTimeBoost() <= 0)) {
            kart.boostFini = true;
        }
    }

    /**
     * Génère des particules pour le kart
     * @param kart Le kart
     * @param particle Type de particule
     * @param x1 Position X relative 1
     * @param y1 Position Y relative 1
     * @param z1 Position Z relative 1
     * @param x2 Vitesse X
     * @param y2 Vitesse Y
     * @param z2 Vitesse Z
     */
    public static void spawnParticules(TestKart kart, SimpleParticleType particle, double x1, double y1, double z1, double x2, double y2, double z2) {
        Minecraft minecraft = Minecraft.getInstance();
        double x = kart.getX();
        double y = kart.getY();
        double z = kart.getZ();

        // SPAWN PARTICULES GAUCHES
        minecraft.particleEngine.createParticle(particle, x - x1, y - y1, z - z1, x2, y2, z2);
        // SPAWN PARTICULES DROITES
        minecraft.particleEngine.createParticle(particle, x + x1, y + y1, z + z1, x2, y2, z2);
    }

    /**
     * Vérifie si un son est en cours de lecture
     * @param sound Le son à vérifier
     * @return true si le son est actif
     */
    private static boolean isSoundPlaying(SoundInstance sound) {
        if (sound == null) {
            return false;
        }
        return Minecraft.getInstance().getSoundManager().isActive(sound);
    }
}
