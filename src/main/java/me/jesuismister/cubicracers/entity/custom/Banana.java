package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.init.KartItemsInit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class Banana extends Entity implements GeoEntity {
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(Banana.class, EntityDataSerializers.FLOAT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final String TEXTURE = "textures/entity/banana.png";
    public static final String MODEL = "geo/banana.geo.json";
    public static final String ANIMATION = "animations/banana.animation.json";
    public static final float HITBOX = 1f;

    public static final EntityDataAccessor<Boolean> isPropulsing = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    private float propulsionY = -1f;

    private static final int TICK_TO_DESPAWN = 20 * 90; //1min 30s
    private int tickAlive = 0;


    public Banana(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        return PlayState.CONTINUE;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(SPEED, 0.0f);
        entityData.define(isPropulsing, false);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag p_20052_) {
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag p_20139_) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isNoGravity() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected boolean canRide(@NotNull Entity rider) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (getIsPropulsing() && !onGround()) {
            double x = Math.sin(Math.toRadians(-getYRot())) * 3.5;
            double z = Math.cos(Math.toRadians(-getYRot())) * 3.5;
            Vec3 vec3 = new Vec3(x, 0, z);
            setDeltaMovement(vec3);
            this.move(MoverType.SELF, new Vec3(getDeltaMovement().x, (1-Math.sqrt(propulsionY))*3, getDeltaMovement().z));
            propulsionY += 0.3f;
        } else {
            //RECUPERER TOUTES LES ENTITES PROCHES DE LA BANANE
            List<Entity> nearbyEntities = level().getEntities(this, getBoundingBox().inflate(0));

            for (Entity entity : nearbyEntities) {
                if (entity instanceof Kart kart) {
                    if (kart.getFirstPassenger() != null) {
                        if (kart.getCanMove()) {
                            Kart.stunKart(kart);
                        }
                        this.remove(RemovalReason.KILLED);
                        return;
                    }
                }
            }

            //DETRUIRE LA BANANE AU BOUT D'UN MOMENT
            tickAlive++;
            if (tickAlive > TICK_TO_DESPAWN) {
                this.remove(RemovalReason.DISCARDED);
            }

            if (getIsPropulsing()) setIsPropulsing(false);
            this.move(MoverType.SELF, new Vec3(0, -1, 0));
        }
    }

    /**
     * Spawn la banane devant le kart
     *
     * @param kart
     */
    public static void spawnBananaFront(Kart kart) {
        if (kart.level() != null) {
            Banana banana = new Banana(KartItemsInit.BANANA.get(), kart.level());
            double angle = Math.toRadians(kart.getYRot());
            banana.setPos(kart.getX() + (-Math.sin(angle) * 3f), kart.getY() + 1, kart.getZ() + (Math.cos(angle) * 3f));
            banana.setYRot(kart.getYRot());
            banana.setIsPropulsing(true);
            kart.level().addFreshEntity(banana);
        }
    }

    /**
     * Spawn la banane derrière le kart
     *
     * @param kart
     */
    public static void spawnBananaBack(Kart kart) {
        if (kart.level() != null) {
            Banana banana = new Banana(KartItemsInit.BANANA.get(), kart.level());
            double angle = Math.toRadians(kart.getYRot());
            banana.setPos(kart.getX() + (Math.sin(angle) * 2.5f), kart.getY(), kart.getZ() + (-Math.cos(angle) * 2.5f));
            banana.setYRot(kart.getYRot() + 180);
            kart.level().addFreshEntity(banana);
        }
    }

    public boolean getIsPropulsing(){
        return this.entityData.get(isPropulsing);
    }

    public void setIsPropulsing(boolean value){
        this.entityData.set(isPropulsing, value);
    }
}
