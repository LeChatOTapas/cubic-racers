package me.jesuismister.cubicracers;

import de.maxhenkel.corelib.CommonRegistry;
import me.jesuismister.cubicracers.commands.TestCommand;
import me.jesuismister.cubicracers.config.ClientConfig;
import me.jesuismister.cubicracers.config.Config;
import me.jesuismister.cubicracers.entity.KartData;
import me.jesuismister.cubicracers.entity.custom.client.ClientEventHandler;
import me.jesuismister.cubicracers.init.*;
import me.jesuismister.cubicracers.network.Network;
import me.jesuismister.cubicracers.particles.ParticlesInit;
import me.jesuismister.cubicracers.util.ClientRandom;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

@Mod(CubicRacers.MODID)
public class CubicRacers {
    public static final String MODID = "cubicracers";
    public static final long SEED = System.currentTimeMillis();
    public static final List<KartData> KARTS_DATA = new ArrayList<>();

    public CubicRacers() {
        initKartData();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        SoundsInit.SOUND_REGISTER.register(bus);

        KartInit.initAllKarts(); //IMPORTANT DE LE METTRE AVANT LE "KartInit.ENTITY_TYPES.register(bus)"
        KartInit.ENTITY_TYPES.register(bus);

        ItemInit.initSpawnKartItem(); //IMPORTANT DE LE METTRE AVANT LE "ItemInit.ITEMS.register(bus)"
        ItemInit.ITEMS.register(bus);

        KartItemsInit.ENTITY_TYPES.register(bus);

        BlockInit.BLOCKS.register(bus);

        ParticlesInit.PARTICLE_TYPES.register(bus);

        ModCreativeModeTabs.CREATIVE_MODE_TAB_REGISTAR.register(bus);

        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);

        ClientRandom.initialize(SEED);
        bus.addListener(this::addCreativeTab);
        bus.addListener(this::commonSetup);

        // Enregistrer les événements spécifiques au client
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(this::clientSetup);
        });

        Config.register();
    }

    private void onServerStarting(ServerStartingEvent event) {
        TestCommand.register(event.getServer().getCommands().getDispatcher());
    }

    /**
     * Ajoute les items dans l'onglet créatif
     *
     * @param event
     */
    private void addCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModCreativeModeTabs.CUBIC_RACERS_TAB.get()) {
            event.accept(ItemInit.ROAD_MAKER);
            event.accept(ItemInit.ITEM_BOX_SPAWN_ITEM.get());

            event.accept(BlockInit.BOOSTER.get());
            event.accept(BlockInit.GLIDE_TRIGGER_BLOCK.get());
            event.accept(BlockInit.KART_CONTROLLER.get());

            event.accept(BlockInit.STARTING_BLOCK.get());
            event.accept(BlockInit.ROAD_BLOCK.get());
            event.accept(BlockInit.ROAD_BLOCK_DIRT.get());
            event.accept(BlockInit.ROAD_BLOCK_SAND.get());
            event.accept(BlockInit.ROAD_BLOCK_SNOW.get());

            event.accept(BlockInit.RED_BOUNCING_MUSHROOM_BLOCK.get());


            for (RegistryObject<Item> r : ItemInit.KARTS_SPAWN_ITEM) {
                event.accept(r.get());
            }
        }
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        // Initialisation commune (client et serveur)
        Network.init();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // Initialisation côté client uniquement
        ClientEventHandler.init();
    }

    public void initKartData() {
        int i = 0;
        //Les karts doivent faire <= 2.0f en hitboxX et = à 2.0f en hitboxY
        KARTS_DATA.add(new KartData(i++, "standard_kart", "Maxmos", 0, 1.8f, 2.0f));
        KARTS_DATA.add(new KartData(i++, "flame_flyer", "TurboMooze3000", 0.3F, 1.6f, 2.0f));
        KARTS_DATA.add(new KartData(i++, "b_dasher", "Maxmos", 0.1F, 1.9f, 2.0f));
        KARTS_DATA.add(new KartData(i++, "zipper", "TurboMooze3000", 0.2F, 1.6f, 2.0f));
        KARTS_DATA.add(new KartData(i++, "mach_celere", "Maxmos", 0.1F, 1.9f, 2.0f));
        KARTS_DATA.add(new KartData(i++, "rally_romper", "TurboMooze3000", 0.8F, 2.0f, 2.0f));
        KARTS_DATA.add(new KartData(i++, "wild_wiggler", "Zitro & Maxmos", 0.7F, 1.5f, 2.0f));
        KARTS_DATA.add(new KartData(i++, "wild_wing", "Maxmos", 0.2F, 1.7f, 2.0f));
        //KARTS_DATA.add(new KartData(i++,"trash_kart",    "JeSuisMister", -0.7f, 2.5f, 1.8f));
    }
}
