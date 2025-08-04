package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.network.message.serverToClient.kartItem.ExplosionParticleMessage;
import me.jesuismister.cubicracers.util.ClientUtil;
import me.jesuismister.cubicracers.util.UtilityMethod;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class BobOmb extends ItemKartAbstract implements GeoEntity {
    public static final float HITBOX = 1f;
    public static final int RANGE = 4;

    public BobOmb(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void applyCollision() {
        List<Entity> nearbyEntities = level().getEntities(this, getBoundingBox().inflate(0));
        for (Entity entity : nearbyEntities) {
            if (entity instanceof TestKart kart && kart.getFirstPassenger() != null) {
                if (!kart.isStun()) {
                    TestKart.stunKart(kart, "BobOmb");
                }
                removeDelay = 2;
                break;
            }
        }
    }

    @Override
    protected int getMaxTimeAlive() {
        return 20 * 4; // 4s
    }

    @Override
    protected void checkEndOfLife() {
        if (removeDelay <= 0) {
            UtilityMethod.stunKartAround(this, RANGE, "Bob_omb");
            this.remove(RemovalReason.KILLED);
            ClientUtil.playSoundToAll(level(), getX(), getY(), getZ(), 8 + RANGE, SoundsInit.BOB_OMB_EXPLOSION.get(), SoundSource.RECORDS, 1f, 0.95f);
            PacketDistributor.sendToAllPlayers(new ExplosionParticleMessage(this.getX(), this.getY(), this.getZ()));
            return;
        }
        removeDelay--;
    }

    //////////////
    // GECKOLIB //
    //////////////
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation NORMAL = RawAnimation.begin().thenLoop("normal");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>("controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationTest<GeoAnimatable> geoAnimatableAnimationTest) {
        return geoAnimatableAnimationTest.setAndContinue(NORMAL);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
