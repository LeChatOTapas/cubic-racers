package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.init.KartItemsInit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
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
        this.entityData.define(SPEED, 0.0f);
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
        //RECUPERER TOUTES LES ENTITES PROCHES DE LA BANANE
        List<Entity> nearbyEntities = level.getEntities(this, getBoundingBox().inflate(0.5f)); // Ajustez la valeur de l'inflation selon vos besoins

        //PARCOURIR LA LISTE DES ENTITES PROCHES
        for (Entity entity : nearbyEntities) {
            //ON CHECK QUE LES ENTITES "PLAYER"
            if (entity instanceof Player) {
                //ON CHECK QUE LES "PLAYER" DANS UN "KART"
                if(entity.getVehicle()!=null && entity.getVehicle() instanceof Kart kart){
                    //ON ENCLENCHE LA PROCEDURE DE STUN
                    if (kart.canMove) {
                        Kart.listeStunKart.add(kart.getUUID());
                        kart.animationTime = Kart.SPINNING_ANIMATION_TIME;

                        kart.deltaOn = false;
                        kart.timeBoost = 0;
                        kart.resetDrift();

                        //POUR EVITER DE STUN PLUSIEURS VEHICULES SUR UNE MEME BANANE
                        return;
                    }

                    //SUPPRIMER LA BANANE APRES LA COLLISION
                    this.remove(RemovalReason.DISCARDED);
                }
            }
        }

        //DETRUIRE LA BANANE AU BOUT D'UN MOMENT
        if (tickAlive > TICK_TO_DESPAWN) {
            this.remove(RemovalReason.DISCARDED);
        }
        tickAlive++;
    }

    /**
     * Spawn la banane derrière le kart
     * @param level
     * @param kart
     */
    public static void spawnBanana(Level level, Kart kart) {
        if (level != null) {
            Banana banana = new Banana(KartItemsInit.BANANA.get(), level);
            double angle = Math.toRadians(kart.getYRot());
            banana.setPos(kart.getX() + (Math.sin(angle) * 2f), kart.getY(), kart.getZ() + (-Math.cos(angle) * 2f));
            level.addFreshEntity(banana);
        }
    }
}
