package me.jesuismister.cubicracers.entity.custom.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Gestionnaire d'événements côté client uniquement
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientEventHandler {

    /**
     * Initialise les gestionnaires d'événements client
     */
    public static void init() {
        MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);

        // Dans notre nouvelle architecture, nous n'avons plus besoin de setClientMethodsProvider
        // car nous utilisons DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ...) directement dans TestKart
    }

    /**
     * Configuration côté client
     */
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Initialiser les renderers, textures, etc.
    }

    /**
     * Enregistre les touches de contrôle
     */
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        // Enregistrer les touches ici
    }

    /**
     * Enregistre les renderers d'entités
     */
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Enregistrer les renderers d'entités
    }
}
