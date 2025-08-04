package me.jesuismister.cubicracers.entity.custom;


import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.util.ClientUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
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

public class Banana extends ItemKartAbstract {
    public static final float HITBOX = 1f;

    public Banana(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void checkEndOfLife() {
        if (removeDelay <= 0) {
            this.remove(RemovalReason.KILLED);
            return;
        }
        removeDelay--;
    }

    @Override
    protected void applyCollision() {
        List<Entity> nearbyEntities = level().getEntities(this, getBoundingBox().inflate(0));
        for (Entity entity : nearbyEntities) {
            if (entity instanceof TestKart kart && kart.getFirstPassenger() != null) {
                if (!kart.isStun()) {
                    TestKart.stunKart(kart, "Banana");
                    if (!level().isClientSide)
                        ClientUtil.playSoundToAll(level(), getX(), getY(), getZ(), 8, SoundsInit.BANANA_HIT_KART.get(), SoundSource.RECORDS, 1f, 0.95f);
                }
                removeDelay = 2;
                break;
            }
        }
    }

    @Override
    protected int getMaxTimeAlive() {
        return 20 * 30; // 30s
    }

    //////////////
    // GECKOLIB //
    //////////////
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation DANCING = RawAnimation.begin().thenLoop("dancing");

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationTest<GeoAnimatable> geoAnimatableAnimationTest) {
        return geoAnimatableAnimationTest.setAndContinue(DANCING);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
