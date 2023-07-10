package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.init.KartItemsInit;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
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
import java.util.Random;

public class BobOmb extends Entity implements GeoEntity {
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(BobOmb.class, EntityDataSerializers.FLOAT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final String TEXTURE = "textures/entity/bob_omb.png";
    public static final String MODEL = "geo/bob_omb.geo.json";
    public static final String ANIMATION = "animations/bob_omb.animation.json";

    public static final float HITBOX = 1f;
    private static final float RANGE = 4;

    private static final float TICK_TO_DESPAWN = 20f * 4f; //5s

    public static final EntityDataAccessor<Boolean> isPropulsing = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> shouldExplode = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    private float propulsionY = -1f;

    private float tickAlive = 0;

    public BobOmb(EntityType<?> p_19870_, Level p_19871_) {
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
        this.entityData.define(SPEED, 0.0f);
        this.entityData.define(shouldExplode, false);
        this.entityData.define(isPropulsing, false);
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
        }else{
            if (getShouldExplode()) {
                stun();
                this.remove(RemovalReason.KILLED);
                return;
            }

            //RECUPERER TOUTES LES ENTITES PROCHES DE LA BOB OMB
            List<Entity> nearbyEntities = level().getEntities(this, getBoundingBox().inflate(0));
            for (Entity entity : nearbyEntities) {
                if (entity instanceof Kart) {
                    setShouldExplode(true);
                    break;
                }
            }

            tickAlive++;
            if (tickAlive > TICK_TO_DESPAWN) {
                setShouldExplode(true);
            }
            this.move(MoverType.SELF, new Vec3(0, -1, 0));

            if (getIsPropulsing()) setIsPropulsing(false);
            this.move(MoverType.SELF, new Vec3(0, -1, 0));
        }
    }

    /**
     * Spawn la bob omb devant le kart
     *
     * @param kart
     */
    public static void spawnBobOmbFront(Kart kart) {
        if (kart.level() != null) {
            BobOmb bobOmb = new BobOmb(KartItemsInit.BOMB_OMB.get(), kart.level());
            double angle = Math.toRadians(kart.getYRot());
            bobOmb.setPos(kart.getX() + (-Math.sin(angle) * 3f), kart.getY() + 1, kart.getZ() + (Math.cos(angle) * 3f));
            bobOmb.setYRot(kart.getYRot());
            bobOmb.setIsPropulsing(true);
            kart.level().addFreshEntity(bobOmb);
        }
    }

    /**
     * Spawn la bob omb derrière le kart
     *
     * @param kart
     */
    public static void spawnBobOmbBack(Kart kart) {
        if (kart.level() != null) {
            BobOmb bobOmb = new BobOmb(KartItemsInit.BOMB_OMB.get(), kart.level());
            double angle = Math.toRadians(kart.getYRot());
            bobOmb.setPos(kart.getX() + (Math.sin(angle) * 2.5f), kart.getY(), kart.getZ() + (-Math.cos(angle) * 2.5f));
            bobOmb.setYRot(kart.getYRot() + 180);
            kart.level().addFreshEntity(bobOmb);
        }
    }

    /**
     * Stun tous les karts proches
     */
    public void stun() {
        spawnExplosionParticles(this, this.getX(), this.getY(), this.getZ(), RANGE);

        List<Entity> nearbyEntities = this.level().getEntities(this, this.getBoundingBox().inflate(RANGE));
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Kart kart) {
                if (kart.getCanMove()) Kart.stunKart(kart);
            }
        }
    }

    public static void spawnExplosionParticles(BobOmb bobOmb, double x, double y, double z, float size) {
        if (!bobOmb.level().isClientSide()) return;

        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            double offsetX = random.nextGaussian() * size;
            double offsetY = random.nextGaussian() * size;
            double offsetZ = random.nextGaussian() * size;

            Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.EXPLOSION, x, y, z, offsetX, offsetY, offsetZ);
        }
    }

    public boolean getShouldExplode(){
        return this.entityData.get(shouldExplode);
    }

    public void setShouldExplode(boolean value){
        this.entityData.set(shouldExplode, value);
    }

    public boolean getIsPropulsing(){
        return this.entityData.get(isPropulsing);
    }

    public void setIsPropulsing(boolean value){
        this.entityData.set(isPropulsing, value);
    }
}
