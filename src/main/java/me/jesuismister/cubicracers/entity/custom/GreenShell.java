package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.sounds.SoundEngineIdle;
import me.jesuismister.cubicracers.sounds.SoundGreenShellMoving;
import me.jesuismister.cubicracers.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
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

public class GreenShell extends ItemKartAbstract implements GeoEntity {
    public static final float HITBOX = 1f;
    private int bounceTime = 0;

    public GreenShell(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    /*
    @Override
    public float getStepHeight() {
        return 1.2f;
    }
     */

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
                    TestKart.stunKart(kart, "GreenShell");
                    if (!level().isClientSide)
                        ClientUtil.playSoundToAll(level(), getX(), getY(), getZ(), 8, SoundsInit.GREEN_SHELL_HIT_KART.get(), SoundSource.RECORDS, 1f, 0.95f);
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

    @Override
    protected void applyVelocity() {
        Vec3 velocity = getDeltaMovement();
        move(MoverType.SELF, velocity);
        setDeltaMovement(velocity.x, velocity.y - 0.3, velocity.z);

        if (horizontalCollision) bounce();
        if (level().isClientSide()) playMovingSound();
    }

    private void bounce() {
        Vec3 velocity = getDeltaMovement(); // Récupère la vélocité actuelle
        BlockPos pos = this.blockPosition();

        boolean bounced = false;

        if (!level().getBlockState(pos.relative(Direction.WEST)).isAir() && !level().getBlockState(pos.relative(Direction.WEST)).is(Blocks.WATER)) {
            setDeltaMovement(-velocity.x, velocity.y, velocity.z); // inverse X
            bounced = true;
        } else if (!level().getBlockState(pos.relative(Direction.EAST)).isAir() && !level().getBlockState(pos.relative(Direction.EAST)).is(Blocks.WATER)) {
            setDeltaMovement(-velocity.x, velocity.y, velocity.z); // inverse X
            bounced = true;
        } else if (!level().getBlockState(pos.relative(Direction.NORTH)).isAir() && !level().getBlockState(pos.relative(Direction.NORTH)).is(Blocks.WATER)) {
            setDeltaMovement(velocity.x, velocity.y, -velocity.z); // inverse Z
            bounced = true;
        } else if (!level().getBlockState(pos.relative(Direction.SOUTH)).isAir() && !level().getBlockState(pos.relative(Direction.SOUTH)).is(Blocks.WATER)) {
            setDeltaMovement(velocity.x, velocity.y, -velocity.z); // inverse Z
            bounced = true;
        }

        if (bounced) {
            bounceTime++;
        }

        if (bounceTime > 4) {
            this.remove(RemovalReason.KILLED);
            // ClientUtil.playSoundToAll(level(), getX(), getY(), getZ(), 8, SoundsInit.GREEN_SHELL_HIT_KART.get(), SoundSource.RECORDS, 1f, 0.95f);
        }
    }

    //////////////
    // GECKOLIB //
    //////////////
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation SPIN = RawAnimation.begin().thenLoop("spin");

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationTest<GeoAnimatable> geoAnimatableAnimationTest) {
        return geoAnimatableAnimationTest.setAndContinue(SPIN);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    ///////////
    // SOUND //
    private SoundGreenShellMoving greenShellMoving;

    public void playMovingSound() {
        if (!level().isClientSide) return;

        List<Entity> nearbyEntities = level().getEntities(this, getBoundingBox().inflate(15));
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player) {
                if (!isSoundPlaying(greenShellMoving)) {
                    greenShellMoving = new SoundGreenShellMoving(this, SoundsInit.GREEN_SHELL_MOVING.get(), SoundSource.RECORDS);
                    SoundsInit.playSoundLoop(greenShellMoving, level());
                }
            }
        }
    }

    public boolean isSoundPlaying(SoundInstance sound) {
        if (sound == null) {
            return false;
        }
        return Minecraft.getInstance().getSoundManager().isActive(sound);
    }
}
