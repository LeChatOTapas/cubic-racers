package me.jesuismister.cubicracers.event;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.client.ItemHudOverlay;
import me.jesuismister.cubicracers.client.SpeedHudOverlay;
import me.jesuismister.cubicracers.entity.client.renderer.*;
import me.jesuismister.cubicracers.entity.custom.Kart;
import me.jesuismister.cubicracers.event.network.Network;
import me.jesuismister.cubicracers.event.network.message.InputMessage;
import me.jesuismister.cubicracers.init.KartInit;
import me.jesuismister.cubicracers.init.KartItemsInit;
import me.jesuismister.cubicracers.particles.ParticlesInit;
import me.jesuismister.cubicracers.particles.custom.DriftParticles;
import me.jesuismister.cubicracers.util.KeyBinds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

public class ModEvents {

    @Mod.EventBusSubscriber(modid = CubicRacers.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void registerGuiOverlay(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("item_box_hud", ItemHudOverlay.HUD_ITEM_BOX);
            event.registerAboveAll("speed_hud", SpeedHudOverlay.HUD_SPEED);
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            //REGISTER TOUS LES KARTS
            for (RegistryObject<EntityType<Kart>> kart : KartInit.KARTS.values()) {
                EntityRenderers.register(kart.get(), KartRenderer::new);
            }

            //REGISTER TOUS LES KART_ITEMS
            EntityRenderers.register(KartItemsInit.ITEM_BOX.get(), ItemBoxRenderer::new);
            EntityRenderers.register(KartItemsInit.BANANA.get(), BananaRenderer::new);
            EntityRenderers.register(KartItemsInit.BOMB_OMB.get(), BobOmbRenderer::new);
            EntityRenderers.register(KartItemsInit.GREEN_SHELL.get(), GreenShellRenderer::new);
            EntityRenderers.register(KartItemsInit.FAKE_BOX.get(), FakeBoxRenderer::new);
        }

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBinds.KART_ACCELERATE_KEY);
            event.register(KeyBinds.KART_DECCELERATE_KEY);

            event.register(KeyBinds.KART_FORWARD_KEY);
            event.register(KeyBinds.KART_BACKWARD_KEY);
            event.register(KeyBinds.KART_LEFT_KEY);
            event.register(KeyBinds.KART_RIGHT_KEY);

            event.register(KeyBinds.KART_DELTA_KEY);
            event.register(KeyBinds.KART_DRIFT_KEY);
            event.register(KeyBinds.KART_ITEM_KEY);
        }

        @SubscribeEvent
        public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
            Minecraft.getInstance().particleEngine.register(ParticlesInit.DRIFT_BLUE_PARTICLES.get(),
                    DriftParticles.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.DRIFT_ORANGE_PARTICLES.get(),
                    DriftParticles.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.DRIFT_PURPLE_PARTICLES.get(),
                    DriftParticles.Provider::new);
        }
    }

    @Mod.EventBusSubscriber(modid = CubicRacers.MODID)
    public class ClientForgeEvent {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (Minecraft.getInstance()!=null && Minecraft.getInstance().player!=null && Minecraft.getInstance().player.getVehicle() != null && Minecraft.getInstance().player.getVehicle() instanceof Kart kart) {
                if(kart.getCanMove()){
                    kart.setIsPressingKeyAccelerate(KeyBinds.KART_ACCELERATE_KEY.isDown());
                    kart.setIsPressingKeyDeccelerate(KeyBinds.KART_DECCELERATE_KEY.isDown());

                    kart.setIsPressingKeyFoward(KeyBinds.KART_FORWARD_KEY.isDown());
                    kart.setIsPressingKeyBackward(KeyBinds.KART_BACKWARD_KEY.isDown());
                    kart.setIsPressingKeyLeft(KeyBinds.KART_LEFT_KEY.isDown());
                    kart.setIsPressingKeyRight(KeyBinds.KART_RIGHT_KEY.isDown());

                    kart.setIsPressingKeyDelta(KeyBinds.KART_DELTA_KEY.isDown());
                    kart.setIsPressingKeyDrift(KeyBinds.KART_DRIFT_KEY.isDown());
                    kart.setIsPressingKeyItem(KeyBinds.KART_ITEM_KEY.isDown());

                    Network.CHANNEL.sendToServer(new InputMessage(
                            kart.getIsPressingKeyAccelerate(), kart.getIsPressingKeyDeccelerate(),
                            kart.getIsPressingKeyForward(), kart.getIsPressingKeyBackward(),
                            kart.getIsPressingKeyLeft(), kart.getIsPressingKeyRight(),
                            kart.getIsPressingKeyDelta(), kart.getIsPressingKeyDrift(), kart.getIsPressingKeyItem()));
                }else{
                    Network.CHANNEL.sendToServer(new InputMessage(false, false, false, false, false, false, false, false, false));
                }
            }
        }
    }
}