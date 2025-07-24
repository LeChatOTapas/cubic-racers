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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class GreenShell extends ItemKartAbstract implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final float MAX_SPEED = 1.5f;

    public static final String TEXTURE = "textures/entity/green_shell.png";
    public static final String MODEL = "geo/green_shell.geo.json";
    public static final String ANIMATION = "animations/green_shell.animation.json";
    public static final float HITBOX = 1f;

    private static final int TICK_TO_DESPAWN = 20 * 15; //20s
    private int tickAlive = 0;
    private int bounceTime = 0;

    public GreenShell(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public float getStepHeight() {
        return 1.2f;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        tAnimationState.getController().setAnimation(RawAnimation.begin()
                .then("spin", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
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


    ///////////
    // SOUND //
    ///////////

    @OnlyIn(Dist.CLIENT)
    private SoundGreenShellMoving greenShellMoving;

    @OnlyIn(Dist.CLIENT)
    public void playMovingSound() {
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

    @OnlyIn(Dist.CLIENT)
    public boolean isSoundPlaying(SoundInstance sound) {
        if (sound == null) {
            return false;
        }
        return Minecraft.getInstance().getSoundManager().isActive(sound);
    }
}
