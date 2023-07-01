package me.jesuismister.cubicracers;

import me.jesuismister.cubicracers.entity.client.renderer.*;
import me.jesuismister.cubicracers.entity.custom.Kart;
import me.jesuismister.cubicracers.init.*;
import me.jesuismister.cubicracers.particles.ParticlesInit;
import me.jesuismister.cubicracers.util.ClientRandom;
import me.jesuismister.cubicracers.util.KeyBinds;
import me.jesuismister.cubicracers.util.ServerRandom;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

@Mod(CubicRacers.MODID)
public class CubicRacers {
    public static final String MODID = "cubicracers";

    public CubicRacers() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        KartInit.initAllKarts(); //IMPORTANT DE LE METTRE AVANT LE "KartInit.ENTITY_TYPES.register(bus)"
        KartInit.ENTITY_TYPES.register(bus);

        KartItemsInit.ENTITY_TYPES.register(bus);

        BlockInit.BLOCKS.register(bus);

        ItemInit.initSpawnKartItem(); //IMPORTANT DE LE METTRE AVANT LE "ItemInit.ITEMS.register(bus)"
        ItemInit.ITEMS.register(bus);

        ParticlesInit.PARTICLE_TYPES.register(bus);

        bus.addListener(this::addCreativeTab);

        long seed = System.currentTimeMillis();
        ClientRandom.initialize(seed);
        ServerRandom.initialize(seed);
    }

    /**
     * Ajoute les items dans l'onglet créatif
     *
     * @param event
     */
    private void addCreativeTab(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == ModCreativeModeTabs.CUBIC_RACERS_TAB) {
            event.accept(ItemInit.ITEM_BOX_SPAWN_ITEM);
            for (RegistryObject<Item> r : ItemInit.KARTS_SPAWN_ITEM) {
                event.accept(r);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            //REGISTER TOUS LES KARTS
            for (RegistryObject<EntityType<Kart>> kart : KartInit.KARTS.values()) {
                EntityRenderers.register(kart.get(), KartRenderer::new);
            }

            //REGISTER TOUS LES ITEMS DE KART
            EntityRenderers.register(KartItemsInit.ITEM_BOX.get(), ItemBoxRenderer::new);
            EntityRenderers.register(KartItemsInit.BANANA.get(), BananaRenderer::new);
            EntityRenderers.register(KartItemsInit.BOMB_OMB.get(), BobOmbRenderer::new);
            EntityRenderers.register(KartItemsInit.GREEN_SHELL.get(), GreenShellRenderer::new);
        }

        @SubscribeEvent
        /**
         * Enregistre les conctrôles du kart
         */
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBinds.KART_UP_KEY);
            event.register(KeyBinds.KART_DOWN_KEY);
            event.register(KeyBinds.KART_LEFT_KEY);
            event.register(KeyBinds.KART_RIGHT_KEY);
            event.register(KeyBinds.KART_DELTA_KEY);
            event.register(KeyBinds.KART_DRIFT_KEY);
            event.register(KeyBinds.KART_ITEM_KEY);
        }
    }
}
