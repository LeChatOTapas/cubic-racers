package me.jesuismister.cubicracers;

import me.jesuismister.cubicracers.entity.client.renderer.KartRenderer;
import me.jesuismister.cubicracers.init.BlockInit;
import me.jesuismister.cubicracers.init.EntityInit;
import me.jesuismister.cubicracers.init.ItemInit;
import me.jesuismister.cubicracers.init.ModCreativeModeTabs;
import me.jesuismister.cubicracers.util.KeyBinds;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CubicRacers.MODID)
public class CubicRacers {
    public static final String MODID = "cubicracers";

    public CubicRacers(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        EntityInit.ENTITY_TYPES.register(bus);
        BlockInit.BLOCKS.register(bus);
        ItemInit.ITEMS.register(bus);

        bus.addListener(this::addCreativeTab);
    }

    private void addCreativeTab(CreativeModeTabEvent.BuildContents event){
        if(event.getTab() == ModCreativeModeTabs.CUBIC_RACERS_TAB){
            event.accept(ItemInit.EXAMPLE_ITEM);
            event.accept(ItemInit.EXAMPLE_SWORD);
            event.accept(ItemInit.EXAMPLE_BLOCK_ITEM);
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            EntityRenderers.register(EntityInit.KART.get(), KartRenderer::new);
        }

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event){
            event.register(KeyBinds.KART_UP_KEY);
            event.register(KeyBinds.KART_DOWN_KEY);
            event.register(KeyBinds.KART_LEFT_KEY);
            event.register(KeyBinds.KART_RIGHT_KEY);
            event.register(KeyBinds.KART_DELTA_KEY);
            event.register(KeyBinds.KART_DRIFT_KEY);
        }
    }
}
