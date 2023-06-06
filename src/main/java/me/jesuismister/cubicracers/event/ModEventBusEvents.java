package me.jesuismister.cubicracers.event;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.particles.ParticlesInit;
import me.jesuismister.cubicracers.particles.custom.DriftParticles;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CubicRacers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

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