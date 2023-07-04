package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.event.network.Network;
import me.jesuismister.cubicracers.event.network.message.BananaRemoveMessage;
import me.jesuismister.cubicracers.event.network.message.GreenShellRemoveMessage;
import me.jesuismister.cubicracers.init.KartItemsInit;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
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

public class GreenShell extends Entity implements GeoEntity {
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(GreenShell.class, EntityDataSerializers.FLOAT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final float MAX_SPEED = 1.2f;
    private int nbUp = 0;

    public static final String TEXTURE = "textures/entity/green_shell.png";
    public static final String MODEL = "geo/green_shell.geo.json";
    public static final String ANIMATION = "animations/green_shell.animation.json";
    public static final float HITBOX = 1f;

    private static final int TICK_TO_DESPAWN = 20 * 20; //20s
    private int tickAlive = 0;


    public GreenShell(EntityType<?> p_19870_, Level p_19871_) {
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
        //COTE CLIENT
        if(this.getLevel().isClientSide()){
            //RECUPERER TOUTES LES ENTITES PROCHES DE LA CARAPACE
            List<Entity> nearbyEntities = level.getEntities(this, getBoundingBox().inflate(0));

            for (Entity entity : nearbyEntities) {
                if (entity instanceof Kart kart) {
                    if(kart.getFirstPassenger()!=null){
                        Network.CHANNEL.sendToServer(new GreenShellRemoveMessage());
                        if(kart.canMove){
                            Kart.stunKart(kart);
                        }
                        this.remove(RemovalReason.KILLED);
                        return;
                    }
                }
            }
        }

        //DEPLACEMENT DE LA CARAPACE
        if(this.horizontalCollision){
            this.setPos(this.getX(), this.getY() + 1f, this.getZ());
            nbUp++;
            if(nbUp>=2){
                this.remove(RemovalReason.KILLED);
            }
        }else{
            nbUp = 0;
        }
        setMovement(this);
        float fallSpeed = 0;
        if(!this.isOnGround()){
            fallSpeed = -1;
        }
        this.move(MoverType.SELF, new Vec3(this.getDeltaMovement().x, fallSpeed, this.getDeltaMovement().z));

        //DETRUIRE LA BANANE AU BOUT D'UN MOMENT
        tickAlive++;
        if (tickAlive > TICK_TO_DESPAWN) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    /*
    @Override
    public void tick() {
        super.tick();
        if(!this.getLevel().isClientSide()) return;

        if(this.horizontalCollision){
            this.setPos(this.getX(), this.getY() + 1f, this.getZ());
            nbUp++;
            if(nbUp>=2){
                this.remove(RemovalReason.KILLED);
            }
        }else{
            nbUp = 0;
        }

        //RECUPERER TOUTES LES ENTITES PROCHES DE LA CARAPACE VERTE
        List<Entity> nearbyEntities = level.getEntities(this, getBoundingBox().inflate(0)); // Ajustez la valeur de l'inflation selon vos besoins

        //PARCOURIR LA LISTE DES ENTITES PROCHES
        for (Entity entity : nearbyEntities) {
            //ON CHECK QUE LES ENTITES "KART"
            if (entity instanceof Kart kart) {
                //ON ENCLENCHE LA PROCEDURE DE STUN
                if (kart.canMove) {
                    Kart.stunKart(kart);
                    return; //POUR EVITER DE STUN PLUSIEURS VEHICULES SUR UNE MEME CARAPACE VERTE
                }
                //SUPPRIMER LA CARAPACE VERTE APRES LA COLLISION
                this.remove(RemovalReason.DISCARDED);
            }else if(entity instanceof GreenShell greenShell){
                this.remove(RemovalReason.KILLED);
                greenShell.remove(RemovalReason.KILLED);
            }
        }

        //DETRUIRE LA CARAPACE VERTE AU BOUT D'UN MOMENT
        if (tickAlive > TICK_TO_DESPAWN) {
            this.remove(RemovalReason.DISCARDED);
        }
        tickAlive++;

        setMovement(this);
        float fallSpeed = 0;
        if(!this.isOnGround()){
            fallSpeed = -1;
        }
        this.move(MoverType.SELF, new Vec3(this.getDeltaMovement().x, fallSpeed, this.getDeltaMovement().z)); //ON APPLIQUE LE VECTEUR DE VITESSE
    }
*/

    /**
     * Spawn la carapace verte derrière le kart
     *
     * @param kart
     */
    public static void spawnGreenShell(Kart kart) {
        if (kart.getLevel() != null) {
            GreenShell green_shell = new GreenShell(KartItemsInit.GREEN_SHELL.get(), kart.getLevel());
            float angle = (float) Math.toRadians(kart.getYRot());
            green_shell.setPos(kart.getX() + (-Math.sin(angle) * (2f + 2f * kart.getSpeed()/kart.MAX_SPEED)), kart.getY(), kart.getZ() + (Math.cos(angle) * (2f + 2f * kart.getSpeed()/kart.MAX_SPEED)));
            green_shell.setYRot(kart.getYRot());
            kart.getLevel().addFreshEntity(green_shell);
        }
    }

    public static void setMovement(GreenShell green_shell) {
        green_shell.setSpeed(MAX_SPEED);

        System.out.println(green_shell.getLevel() + " / " + green_shell.getYRot());

        double x = Math.sin(Math.toRadians(-green_shell.getYRot())) * MAX_SPEED;
        double z = Math.cos(Math.toRadians(-green_shell.getYRot())) * MAX_SPEED;
        Vec3 vec3 = new Vec3(x, 0, z);
        green_shell.setDeltaMovement(vec3);
    }

    public void setSpeed(float new_speed) {
        this.entityData.set(SPEED, new_speed);
    }

    @Override
    public float getStepHeight() {
        return 10.0f;
    }
}
