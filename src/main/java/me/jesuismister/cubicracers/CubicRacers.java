package me.jesuismister.cubicracers;

import me.jesuismister.cubicracers.commands.TestCommand;
import me.jesuismister.cubicracers.config.Config;
import me.jesuismister.cubicracers.entity.KartData;
import me.jesuismister.cubicracers.init.*;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod(CubicRacers.MODID)
public class CubicRacers {
    public static final String MODID = "cubicracers";
    public static final long SEED = System.currentTimeMillis();

    public static final List<KartData> KARTS_DATA = new ArrayList<>();

    public CubicRacers(IEventBus modEventBus, ModContainer modContainer) {
        initKartData();

        // NeoForge.EVENT_BUS.register(this);

        ModCreativeModeTabs.register(modEventBus);
        ItemInit.register(modEventBus);
        BlockInit.register(modEventBus);
        KartInit.register(modEventBus);
        KartItemsInit.register(modEventBus);
        ParticlesInit.register(modEventBus);
        SoundsInit.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        // modEventBus.addListener(this::onServerStarting);
        modEventBus.addListener(this::addToCreativeTab);

        // Config
        Config.register();
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SERVER);

        /*
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        SoundsInit.SOUND_REGISTER.register(bus);

        KartInit.initAllKarts(); //IMPORTANT DE LE METTRE AVANT LE "KartInit.ENTITY_TYPES.register(bus)"
        KartInit.ENTITY_TYPES.register(bus);

        ItemInit.initSpawnKartItem(); //IMPORTANT DE LE METTRE AVANT LE "ItemInit.ITEMS.register(bus)"
        ItemInit.ITEMS.register(bus);

        KartItemsInit.ENTITY_TYPES.register(bus);

        BlockInit.BLOCKS.register(bus);

        ParticlesInit.PARTICLE_TYPES.register(bus);

        ModCreativeModeTabs.CREATIVE_MODE_TABS.register(bus);

        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);

        ClientRandom.initialize(SEED);
        bus.addListener(this::addCreativeTab);
        bus.addListener(this::commonSetup);

        Config.register();
        */
    }

    private void onServerStarting(ServerStartingEvent event) {
        TestCommand.register(event.getServer().getCommands().getDispatcher());
    }

    private void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ModCreativeModeTabs.CUBIC_RACERS_TAB) {
            event.accept(ItemInit.ROAD_MAKER);
            event.accept(ItemInit.ITEM_BOX_SPAWN_ITEM);

            event.accept(BlockInit.BOOSTER);
            event.accept(BlockInit.GLIDE_TRIGGER_BLOCK);
            event.accept(BlockInit.KART_CONTROLLER);

            event.accept(BlockInit.STARTING_BLOCK);
            event.accept(BlockInit.ROAD_BLOCK);
            event.accept(BlockInit.ROAD_BLOCK_DIRT);
            event.accept(BlockInit.ROAD_BLOCK_SAND);
            event.accept(BlockInit.ROAD_BLOCK_SNOW);

            event.accept(BlockInit.RED_BOUNCING_MUSHROOM_BLOCK);


            for (Supplier<Item> r : ItemInit.KARTS_SPAWN_ITEM) {
                event.accept(r.get());
            }
        }
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
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
