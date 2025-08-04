package me.jesuismister.cubicracers.particles;

import me.jesuismister.cubicracers.CubicRacers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CubicRacers.MODID, value = Dist.CLIENT)
public class DriftParticles extends TextureSheetParticle {

    protected DriftParticles(ClientLevel level, double xCoord, double yCoord, double zCoord,
                             SpriteSet spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        this.friction = 0.8F;
        //velocity
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        //size
        this.quadSize *= 1.2F;
        //life time in ticks
        this.lifetime = 5;
        //important sinon le jeu crash
        this.setSpriteFromAge(spriteSet);
        //couleurs
        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1F;
    }

    @Override
    public void tick() {
        super.tick();
        fadeOut();
    }

    private void fadeOut() {
        this.alpha = (-(1 / (float) lifetime) * age * 1);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new DriftParticles(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
