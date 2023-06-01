package me.jesuismister.mckart;

import me.jesuismister.mckart.init.BlockInit;
import me.jesuismister.mckart.init.ItemInit;
import me.jesuismister.mckart.init.ModCreativeModeTabs;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MCKart.MODID)
public class MCKart {
    public static final String MODID = "mckart";

    public MCKart(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        BlockInit.BLOCKS.register(bus);
        ItemInit.ITEMS.register(bus);

        bus.addListener(this::addCreativeTab);
    }

    private void addCreativeTab(CreativeModeTabEvent.BuildContents event){
        if(event.getTab() == ModCreativeModeTabs.MCKART_TAB){
            event.accept(ItemInit.EXAMPLE_ITEM);
            event.accept(ItemInit.EXAMPLE_BLOCK_ITEM);
        }
    }
}
