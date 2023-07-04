package me.jesuismister.cubicracers;

import me.jesuismister.cubicracers.event.network.Network;
import me.jesuismister.cubicracers.init.*;
import me.jesuismister.cubicracers.particles.ParticlesInit;
import me.jesuismister.cubicracers.util.ClientRandom;
import me.jesuismister.cubicracers.util.ServerRandom;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

@Mod(CubicRacers.MODID)
public class CubicRacers {
    public static final String MODID = "cubicracers";

    public CubicRacers() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::commonSetup);

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

    public void commonSetup(final FMLCommonSetupEvent event){
        Network.init();
    }
}
