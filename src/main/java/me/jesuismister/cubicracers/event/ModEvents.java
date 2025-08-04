package me.jesuismister.cubicracers.event;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.client.renderer.*;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import me.jesuismister.cubicracers.network.message.clientToServer.InputSynchMessage;
import me.jesuismister.cubicracers.init.KartInit;
import me.jesuismister.cubicracers.init.KartItemsInit;
import me.jesuismister.cubicracers.init.ParticlesInit;
import me.jesuismister.cubicracers.particles.DriftParticles;
import me.jesuismister.cubicracers.init.KeyBinds;
import me.jesuismister.cubicracers.util.KartItemUseMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.function.Supplier;

import static me.jesuismister.cubicracers.init.KeyBinds.KART_ITEM_KEY;

public class ModEvents {

    @EventBusSubscriber(modid = CubicRacers.MODID, value = Dist.CLIENT)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void registerGuiOverlay(RegisterGuiLayersEvent event) {
            // event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, "item_box_hud"), new ItemBoxHudLayer());
            // event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, "speed_hud"), new SpeedHudLayer());
        }

        @SubscribeEvent // on the mod event bus
        public static void register(final RegisterPayloadHandlersEvent event) {
            // Sets the current network version
            final PayloadRegistrar registrar = event.registrar("1");
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            /*event.enqueueWork(() -> {
                Set<Item> set = new HashSet<>();
                set.addAll(ClientLevel.MARKER_PARTICLE_ITEMS);
                set.add(BlockInit.TRANSPARENT_ROAD_BLOCK.get().asItem());
                ClientLevel.MARKER_PARTICLE_ITEMS = set;
            });*/

            //REGISTER TOUS LES KARTS
            for (Supplier<EntityType<TestKart>> kart : KartInit.KARTS.values()) {
                EntityRenderers.register(kart.get(), TestKartRenderer::new);
            }

            //REGISTER TOUS LES KART_ITEMS
            EntityRenderers.register(KartItemsInit.ITEM_BOX.get(), ItemBoxRenderer::new);
            EntityRenderers.register(KartItemsInit.BANANA.get(), BananaRenderer::new);
            EntityRenderers.register(KartItemsInit.BOMB_OMB.get(), BobOmbRenderer::new);
            EntityRenderers.register(KartItemsInit.GREEN_SHELL.get(), GreenShellRenderer::new);
            EntityRenderers.register(KartItemsInit.FAKE_BOX.get(), FakeBoxRenderer::new);
        }

        @SubscribeEvent
        public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ParticlesInit.DRIFT_BLUE_PARTICLES.get(), DriftParticles.Provider::new);
            event.registerSpriteSet(ParticlesInit.DRIFT_ORANGE_PARTICLES.get(), DriftParticles.Provider::new);
            event.registerSpriteSet(ParticlesInit.DRIFT_PURPLE_PARTICLES.get(), DriftParticles.Provider::new);
        }
    }

    @EventBusSubscriber(modid = CubicRacers.MODID)
    public static class ClientForgeEvent {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getVehicle() != null && Minecraft.getInstance().player.getVehicle() instanceof TestKart kart) {
                if (kart.getCanMove()) {
                    kart.setPressingKeyAccelerate(KeyBinds.KART_ACCELERATE_KEY.isDown());
                    kart.setPressingKeyDeccelerate(KeyBinds.KART_DECCELERATE_KEY.isDown());

                    kart.setPressingKeyForward(KeyBinds.KART_FORWARD_KEY.isDown());
                    kart.setPressingKeyBackward(KeyBinds.KART_BACKWARD_KEY.isDown());
                    kart.setPressingKeyLeft(KeyBinds.KART_LEFT_KEY.isDown());
                    kart.setPressingKeyRight(KeyBinds.KART_RIGHT_KEY.isDown());

                    kart.setPressingKeyDrift(KeyBinds.KART_DRIFT_KEY.isDown());
                    kart.setPressingKeyItem(KART_ITEM_KEY.isDown());

                    ClientPacketDistributor.sendToServer(new InputSynchMessage(
                            kart.isPressingKeyAccelerate(), kart.isPressingKeyDeccelerate(),
                            kart.isPressingKeyForward(), kart.isPressingKeyBackward(),
                            kart.isPressingKeyLeft(), kart.isPressingKeyRight(),
                            kart.isPressingKeyDrift(), kart.isPressingKeyItem()));
                } else {
                    ClientPacketDistributor.sendToServer(new InputSynchMessage(false, false, false, false, false, false, false, false));
                }
            }
        }

        // Ajoutez la méthode manquante et corrigez l'annotation
        @SubscribeEvent
        public static void cancelFallDamageInKart(LivingFallEvent event) {
            if (event.getEntity() instanceof Player player && player.getVehicle() instanceof TestKart) {
                event.setCanceled(true);
            }
        }

        private static boolean wasItemKeyDown = false;

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            boolean isItemKeyDown = KART_ITEM_KEY.isDown();

            // Détection du premier tick où la touche est pressée
            if (isItemKeyDown && !wasItemKeyDown) {
                if (mc.player.getVehicle() != null && mc.player.getVehicle() instanceof TestKart kart) {
                    KartItemUseMethods.useItem(kart);
                }
            }

            // Mise à jour de l’état précédent
            wasItemKeyDown = isItemKeyDown;
        }

    }
}