package me.jesuismister.cubicracers.entity.custom;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
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

    private static final int TICK_TO_DESPAWN = 20 * 20; //20s
    private int tickAlive = 0;
    private int bounceTime = 0;

    public GreenShell(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
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
    public void tick() {
        super.tick();
        //RECUPERER TOUTES LES ENTITES PROCHES DE LA CARAPACE
        List<Entity> nearbyEntities = level().getEntities(this, getBoundingBox().inflate(0));

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Kart kart) {
                if (kart.getCanMove()) {
                    Kart.stunKart(kart);
                }
                if(!level().isClientSide()) this.remove(RemovalReason.KILLED);
                return;
            }
        }

        //REBONDS
        if (this.horizontalCollision) bounce();

        //DEPLACEMENT DE LA CARAPACE
        setMovement(this);
        this.move(MoverType.SELF, new Vec3(this.getDeltaMovement().x, -1, this.getDeltaMovement().z));

        //DETRUIRE LA CARAPACE AU BOUT D'UN MOMENT
        tickAlive++;
        if (tickAlive > TICK_TO_DESPAWN) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    public static void setMovement(GreenShell green_shell) {
        green_shell.setSpeed(MAX_SPEED);

        double x = Math.sin(Math.toRadians(-green_shell.getYRot())) * MAX_SPEED;
        double z = Math.cos(Math.toRadians(-green_shell.getYRot())) * MAX_SPEED;
        Vec3 vec3 = new Vec3(x, 0, z);
        green_shell.setDeltaMovement(vec3);
    }

    @Override
    /**
     * La carapace peut comme le kart, monter au dessus des blocs de 1 de haut
     */
    public float getStepHeight() {
        return 1.2f;
    }

    private void bounce() {
        if (!this.level().getBlockState(this.blockPosition().relative(Direction.WEST)).is(Blocks.AIR) && !this.level().getBlockState(this.blockPosition().relative(Direction.WEST)).is(Blocks.WATER)) {
            this.setYRot(-this.getYRot());
            bounceTime++;
        } else if (!this.level().getBlockState(this.blockPosition().relative(Direction.EAST)).is(Blocks.AIR) && !this.level().getBlockState(this.blockPosition().relative(Direction.EAST)).is(Blocks.WATER)) {
            this.setYRot(-this.getYRot());
            bounceTime++;
        } else if (!this.level().getBlockState(this.blockPosition().relative(Direction.NORTH)).is(Blocks.AIR) && !this.level().getBlockState(this.blockPosition().relative(Direction.EAST)).is(Blocks.WATER)) {
            this.setYRot(-180 - this.getYRot());
            bounceTime++;
        } else if (!this.level().getBlockState(this.blockPosition().relative(Direction.SOUTH)).is(Blocks.AIR) && !this.level().getBlockState(this.blockPosition().relative(Direction.EAST)).is(Blocks.WATER)) {
            this.setYRot(180 - this.getYRot());
            bounceTime++;
        }

        if (bounceTime > 4) {
            this.remove(RemovalReason.KILLED);
        }
    }
}
