package me.jesuismister.cubicracers;

import me.jesuismister.cubicracers.entity.client.renderer.BananaRenderer;
import me.jesuismister.cubicracers.entity.client.renderer.KartRenderer;
import me.jesuismister.cubicracers.entity.custom.Kart;
import me.jesuismister.cubicracers.init.*;
import me.jesuismister.cubicracers.particles.ParticlesInit;
import me.jesuismister.cubicracers.util.KeyBinds;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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

    public CubicRacers(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        KartInit.initAllKarts(); //IMPORTANT DE LE METTRE AVANT LE "EntityInit.ENTITY_TYPES.register(bus)"
        KartInit.ENTITY_TYPES.register(bus);

        KartObjectInit.KART_OBJECT_TYPES.register(bus);

        BlockInit.BLOCKS.register(bus);
        ItemInit.ITEMS.register(bus);
        ParticlesInit.PARTICLE_TYPES.register(bus);

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
            for(RegistryObject<EntityType<Kart>> kart : KartInit.KARTS){
                EntityRenderers.register(kart.get(), KartRenderer::new);
            }

            EntityRenderers.register(KartObjectInit.BANANA.get(), BananaRenderer::new);
        }

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event){
            event.register(KeyBinds.KART_UP_KEY);
            event.register(KeyBinds.KART_DOWN_KEY);
            event.register(KeyBinds.KART_LEFT_KEY);
            event.register(KeyBinds.KART_RIGHT_KEY);
            event.register(KeyBinds.KART_DELTA_KEY);
            event.register(KeyBinds.KART_DRIFT_KEY);
            event.register(KeyBinds.KART_OBJECT_KEY);
        }
    }
}
