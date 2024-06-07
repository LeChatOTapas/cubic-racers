package me.jesuismister.cubicracers;

import de.maxhenkel.corelib.CommonRegistry;
import me.jesuismister.cubicracers.config.ClientConfig;
import me.jesuismister.cubicracers.config.Config;
import me.jesuismister.cubicracers.entity.KartData;
import me.jesuismister.cubicracers.init.*;
import me.jesuismister.cubicracers.network.Network;
import me.jesuismister.cubicracers.particles.ParticlesInit;
import me.jesuismister.cubicracers.util.ClientRandom;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
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

        ModCreativeModeTabs.CREATIVE_MODE_TABS.register(bus);

        ClientRandom.initialize(SEED);
        bus.addListener(this::addCreativeTab);
        bus.addListener(this::commonSetup);

        Config.register();
    }

    /**
     * Ajoute les items dans l'onglet créatif
     *
     * @param event
     */
    private void addCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModCreativeModeTabs.CUBIC_RACERS_TAB.get()) {
            event.accept(ItemInit.ITEM_BOX_SPAWN_ITEM.get());

            event.accept(BlockInit.BOOSTER.get());
            event.accept(BlockInit.KART_CONTROLLER.get());

            event.accept(BlockInit.ROAD_BLOCK.get());
            event.accept(BlockInit.ROAD_BLOCK_DIRT.get());
            event.accept(BlockInit.ROAD_BLOCK_SAND.get());
            event.accept(BlockInit.ROAD_BLOCK_SNOW.get());
            event.accept(BlockInit.HOLLOW_ROAD_BLOCK.get());


            for (RegistryObject<Item> r : ItemInit.KARTS_SPAWN_ITEM) {
                event.accept(r.get());
            }
        }
    }

    public void commonSetup(final FMLCommonSetupEvent event){
        Network.init();
    }

    public void initKartData(){
        int i = 0;
        KARTS_DATA.add(new KartData(i++,"standard_kart", "Maxmos",         -1.0f, 2.2f, 1.3f));
        KARTS_DATA.add(new KartData(i++,"flame_flyer",   "TurboMooze3000", -0.9f, 2.1f, 1.4f));
        KARTS_DATA.add(new KartData(i++,"b_dasher",      "Maxmos",         -0.8f, 2.3f, 1.0f));
        KARTS_DATA.add(new KartData(i++,"zipper",        "TurboMooze3000", -0.8f, 1.7f, 1.2f));
        KARTS_DATA.add(new KartData(i++,"mach_celere",   "Maxmos",         -0.8f, 2.3f, 1.0f));
        KARTS_DATA.add(new KartData(i++,"rally_romper",  "TurboMooze3000", -0.7f, 2.5f, 1.8f));
        //KARTS_DATA.add(new KartData(i++,"trash_kart",    "JeSuisMister", -0.7f, 2.5f, 1.8f));
    }
}
