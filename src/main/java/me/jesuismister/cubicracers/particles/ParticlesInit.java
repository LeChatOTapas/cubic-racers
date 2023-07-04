package me.jesuismister.cubicracers.particles;

import me.jesuismister.cubicracers.CubicRacers;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ParticlesInit {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, CubicRacers.MODID);

    public static final RegistryObject<SimpleParticleType> DRIFT_BLUE_PARTICLES =
            PARTICLE_TYPES.register("drift_blue_particles", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> DRIFT_ORANGE_PARTICLES =
            PARTICLE_TYPES.register("drift_orange_particles", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> DRIFT_PURPLE_PARTICLES =
            PARTICLE_TYPES.register("drift_purple_particles", () -> new SimpleParticleType(true));
}
