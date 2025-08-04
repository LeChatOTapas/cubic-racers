package me.jesuismister.cubicracers.init;

import me.jesuismister.cubicracers.CubicRacers;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class ParticlesInit {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, CubicRacers.MODID);

    public static final Supplier<SimpleParticleType> DRIFT_BLUE_PARTICLES =
            PARTICLE_TYPES.register("drift_blue_particles", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> DRIFT_ORANGE_PARTICLES =
            PARTICLE_TYPES.register("drift_orange_particles", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> DRIFT_PURPLE_PARTICLES =
            PARTICLE_TYPES.register("drift_purple_particles", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
