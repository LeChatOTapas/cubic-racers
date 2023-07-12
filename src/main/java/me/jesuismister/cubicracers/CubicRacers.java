package me.jesuismister.cubicracers;

import me.jesuismister.cubicracers.network.Network;
import me.jesuismister.cubicracers.init.*;
import me.jesuismister.cubicracers.particles.ParticlesInit;
import me.jesuismister.cubicracers.util.ClientRandom;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

@Mod(CubicRacers.MODID)
public class CubicRacers {
    public static final String MODID = "cubicracers";
    public static final long SEED = System.currentTimeMillis();

    public CubicRacers() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        KartInit.initAllKarts(); //IMPORTANT DE LE METTRE AVANT LE "KartInit.ENTITY_TYPES.register(bus)"
        KartInit.ENTITY_TYPES.register(bus);

        KartItemsInit.ENTITY_TYPES.register(bus);

        BlockInit.BLOCKS.register(bus);

        ItemInit.initSpawnKartItem(); //IMPORTANT DE LE METTRE AVANT LE "ItemInit.ITEMS.register(bus)"
        ItemInit.ITEMS.register(bus);

        ParticlesInit.PARTICLE_TYPES.register(bus);

        ModCreativeModeTabs.CREATIVE_MODE_TABS.register(bus);

        ClientRandom.initialize(SEED);
        bus.addListener(this::addCreativeTab);
        bus.addListener(this::commonSetup);
    }

    /**
     * Ajoute les items dans l'onglet créatif
     *
     * @param event
     */
    private void addCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModCreativeModeTabs.CUBIC_RACERS_TAB.get()) {
            event.accept(ItemInit.ITEM_BOX_SPAWN_ITEM);
            for (RegistryObject<Item> r : ItemInit.KARTS_SPAWN_ITEM) {
                event.accept(r);
            }
        }
    }

    public void commonSetup(final FMLCommonSetupEvent event){
        Network.init();
    }
}
