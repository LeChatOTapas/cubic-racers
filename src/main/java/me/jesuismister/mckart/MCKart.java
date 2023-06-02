package me.jesuismister.mckart;

import me.jesuismister.mckart.entity.client.renderer.KartRenderer;
import me.jesuismister.mckart.init.BlockInit;
import me.jesuismister.mckart.init.EntityInit;
import me.jesuismister.mckart.init.ItemInit;
import me.jesuismister.mckart.init.ModCreativeModeTabs;
import me.jesuismister.mckart.util.KeyBinds;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MCKart.MODID)
public class MCKart {
    public static final String MODID = "mckart";

    public MCKart(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        EntityInit.ENTITY_TYPES.register(bus);
        BlockInit.BLOCKS.register(bus);
        ItemInit.ITEMS.register(bus);

        bus.addListener(this::addCreativeTab);
    }

    private void addCreativeTab(CreativeModeTabEvent.BuildContents event){
        if(event.getTab() == ModCreativeModeTabs.MCKART_TAB){
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
            event.register(KeyBinds.KART_JUMP_KEY);
        }
    }
}
