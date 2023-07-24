package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.sounds.SoundEngineIdleLoop;
import me.jesuismister.cubicracers.sounds.SoundLoopKart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public abstract class KartAbstract extends Entity {
    public static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT);

    //KEYS POUR LE KART
    public static final EntityDataAccessor<Boolean> isPressingKeyAccelerate = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyDeccelerate = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyForward = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyBackward = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyLeft = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyRight = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyDrift = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyItem = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyDelta = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> previousPressingKeyDelta = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);

    //ATTRIBUTS GENERAUX DES KARTS
    public static final float MIN_SPEED = 0.075f;
    public static final float FREINAGE_SPEED = 1.05f;
    public static final float BASE_FALL_SPEED = -1.2f;
    public static final float REDUCED_FALL_SPEED = -0.2f;
    public static final float COEFF_FROTTEMENT = 0.85f;

    //ATTRIBUTS DU DRIFT
    public static final float DRIFT_ANGLE = 1.5f;
    public static final EntityDataAccessor<Boolean> isDrifting = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> driftingSens = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Float> driftingTime = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> driftTimeBoost = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> timeBoost = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT);

    //ATTRIBUTS DE CONDUITE
    public static final EntityDataAccessor<Boolean> deltaOn = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> pourcentage_inclinaison = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> actual_rotation_wheels = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT);

    //ANIMATION DEGATS
    public static final EntityDataAccessor<Boolean> canMove = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> stunRotation = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT);

    //KART ITEM
    private String kartItem;
    public static final EntityDataAccessor<Boolean> isInvinsible = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> starSpeedBoost = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT); //COEFF DE BOOST / 1 PAR DEFAUT / 1.5 SOUS ETOILE
    public static final EntityDataAccessor<Float> timeStar = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT);

    //LOCK
    public static final EntityDataAccessor<Boolean> isLock = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);

    public KartAbstract(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public float getSpeed() {
        return this.entityData.get(SPEED);
    }

    public void setSpeed(float value) {
        this.entityData.set(SPEED, value);
    }

    private int steps = 0;
    private double clientX;
    private double clientY;
    private double clientZ;

    //

    public boolean getIsPressingKeyAccelerate() {
        return this.entityData.get(isPressingKeyAccelerate);
    }

    public void setIsPressingKeyAccelerate(boolean value) {
        this.entityData.set(isPressingKeyAccelerate, value);
    }

    public boolean getIsPressingKeyDeccelerate() {
        return this.entityData.get(isPressingKeyDeccelerate);
    }

    public void setIsPressingKeyDeccelerate(boolean value) {
        this.entityData.set(isPressingKeyDeccelerate, value);
    }

    public boolean getIsPressingKeyForward() {
        return this.entityData.get(isPressingKeyForward);
    }

    public void setIsPressingKeyFoward(boolean value) {
        this.entityData.set(isPressingKeyForward, value);
    }

    public boolean getIsPressingKeyBackward() {
        return this.entityData.get(isPressingKeyBackward);
    }

    public void setIsPressingKeyBackward(boolean value) {
        this.entityData.set(isPressingKeyBackward, value);
    }

    public boolean getIsPressingKeyLeft() {
        return this.entityData.get(isPressingKeyLeft);
    }

    public void setIsPressingKeyLeft(boolean value) {
        this.entityData.set(isPressingKeyLeft, value);
    }

    public boolean getIsPressingKeyRight() {
        return this.entityData.get(isPressingKeyRight);
    }

    public void setIsPressingKeyRight(boolean value) {
        this.entityData.set(isPressingKeyRight, value);
    }

    public boolean getIsPressingKeyDrift() {
        return this.entityData.get(isPressingKeyDrift);
    }

    public void setIsPressingKeyDrift(boolean value) {
        this.entityData.set(isPressingKeyDrift, value);
    }

    public boolean getIsPressingKeyItem() {
        return this.entityData.get(isPressingKeyItem);
    }

    public void setIsPressingKeyItem(boolean value) {
        this.entityData.set(isPressingKeyItem, value);
    }

    public boolean getIsPressingKeyDelta() {
        return this.entityData.get(isPressingKeyDelta);
    }

    public void setIsPressingKeyDelta(boolean value) {
        this.entityData.set(isPressingKeyDelta, value);
    }

    public boolean getPreviousPressingKeyDelta() {
        return this.entityData.get(previousPressingKeyDelta);
    }

    public void setPreviousPressingKeyDelta(boolean value) {
        this.entityData.set(previousPressingKeyDelta, value);
    }

    //

    public boolean getIsDrifting() {
        return this.entityData.get(isDrifting);
    }

    public void setIsDrifting(boolean value) {
        this.entityData.set(isDrifting, value);
    }

    public String getDriftingSens() {
        return this.entityData.get(driftingSens);
    }

    public void setDriftingSens(String value) {
        this.entityData.set(driftingSens, value);
    }

    public float getDriftingTime() {
        return this.entityData.get(driftingTime);
    }

    public void setDriftingTime(float value) {
        this.entityData.set(driftingTime, value);
    }

    public float getDriftTimeBoost() {
        return this.entityData.get(driftTimeBoost);
    }

    public void setDriftTimeBoost(float value) {
        this.entityData.set(driftTimeBoost, value);
    }

    public float getTimeBoost() {
        return this.entityData.get(timeBoost);
    }

    public void setTimeBoost(float value) {
        this.entityData.set(timeBoost, value);
    }

    //

    public boolean getDeltaOn() {
        return this.entityData.get(deltaOn);
    }

    public void setDeltaOn(boolean value) {
        this.entityData.set(deltaOn, value);
    }

    public float getPourcentageInclinaison() {
        return this.entityData.get(pourcentage_inclinaison);
    }

    public void setPourcentageInclinaison(float value) {
        this.entityData.set(pourcentage_inclinaison, value);
    }

    public float getActualRotationWheels() {
        return this.entityData.get(actual_rotation_wheels);
    }

    public void setActualRotationWheels(float value) {
        this.entityData.set(actual_rotation_wheels, value);
    }

    //

    public boolean getCanMove() {
        return this.entityData.get(canMove);
    }

    public void setCanMove(boolean value) {
        this.entityData.set(canMove, value);
    }

    public float getStunRotation() {
        return this.entityData.get(stunRotation);
    }

    public void setStunRotation(float value) {
        this.entityData.set(stunRotation, value);
    }

    //

    public String getKartItem() {
        return kartItem;
    }

    public void setKartItem(String value) {
        kartItem = value;
    }

    public boolean getIsInvinsible() {
        return this.entityData.get(isInvinsible);
    }

    public void setIsInvinsible(boolean value) {
        this.entityData.set(isInvinsible, value);
    }

    public float getStarSpeedBoost() {
        return this.entityData.get(starSpeedBoost);
    }

    public void setStarSpeedBoost(float value) {
        this.entityData.set(starSpeedBoost, value);
    }

    public float getTimeStar() {
        return this.entityData.get(timeStar);
    }

    public void setTimeStar(float value) {
        this.entityData.set(timeStar, value);
    }

    //

    public boolean getIsLock() {
        return this.entityData.get(isLock);
    }

    public void setisLock(boolean value) {
        this.entityData.set(isLock, value);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(SPEED, 0.0f);

        entityData.define(isPressingKeyAccelerate, false);
        entityData.define(isPressingKeyDeccelerate, false);
        entityData.define(isPressingKeyForward, false);
        entityData.define(isPressingKeyBackward, false);
        entityData.define(isPressingKeyLeft, false);
        entityData.define(isPressingKeyRight, false);
        entityData.define(isPressingKeyDrift, false);
        entityData.define(isPressingKeyItem, false);
        entityData.define(isPressingKeyDelta, false);
        entityData.define(previousPressingKeyDelta, false);

        entityData.define(isDrifting, false);
        entityData.define(driftingSens, "None");
        entityData.define(driftingTime, 0.0f);
        entityData.define(driftTimeBoost, 0.0f);
        entityData.define(timeBoost, 0.0f);

        entityData.define(deltaOn, false);
        entityData.define(pourcentage_inclinaison, 0.f);
        entityData.define(actual_rotation_wheels, 0.f);

        entityData.define(canMove, true);
        entityData.define(stunRotation, 0.f);

        kartItem = "None";
        entityData.define(isInvinsible, false);
        entityData.define(starSpeedBoost, 1f); //COEFF DE BOOST / 1 PAR DEFAUT / 1.5 SOUS ETOILE
        entityData.define(timeStar, 0.f);

        entityData.define(isLock, false);
    }

    ////////////
    // SOUNDS //
    ////////////

    @OnlyIn(Dist.CLIENT)
    private SoundEngineIdleLoop engineIdleLoop;

    private void playSoundEffect(SoundEvent event, double x, double y, double z, Player entity){
        SoundsInit.playSound(event, level(), new BlockPos((int)x, (int)y, (int)z), entity, SoundSource.RECORDS);
    }

    @OnlyIn(Dist.CLIENT)
    public void updateSounds() {
        if (getSpeed() == 0) {
            if (!isSoundPlaying(engineIdleLoop)) {
                engineIdleLoop = new SoundEngineIdleLoop(this, SoundsInit.ENGINE_IDLE.get(), SoundSource.RECORDS);
                SoundsInit.playSoundLoop(engineIdleLoop, level());
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

    ////////////
    // AUTRES //
    ////////////

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag p_20052_) {
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag p_20139_) {
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
    public float getStepHeight() {
        return 1.2f;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    /**
     * Gestion des interraction avec le kart
     */
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        //SI LE JOUEUR N'EST PAS DANS LE KART
        if (this.getFirstPassenger() == null) {
            //ALORS LE JOUEUR MONTE DANS LE KART
            player.startRiding(this);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    /**
     * installe le joueur sur le kart
     */
    public boolean startRiding(@NotNull Entity rider) {
        Player player = (Player) rider;
        return super.startRiding(player);
    }

    @Override
    /**
     * Le conducteur peut interragir (??? - tester de voir à false ce que ça fait)
     */
    public boolean canRiderInteract() {
        return true;
    }

    @Override
    /**
     * Peut monter le kart
     */
    protected boolean canRide(@NotNull Entity rider) {
        return true;
    }

    /**
     * FONCTION POUR DEBUG : ENVOIE UN MESSAGE AU CONDUCTEUR DU VEHICULE
     *
     * @param msg
     */
    public void sendConductorMessage(String msg) {
        try {
            if (this != null && this.getFirstPassenger() != null && this.getFirstPassenger() instanceof Player player) {
                player.sendSystemMessage(Component.literal(msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendAll(String msg) {
        try {
            if (Minecraft.getInstance()!=null && Minecraft.getInstance().player!=null) {
                Minecraft.getInstance().player.sendSystemMessage(Component.literal(msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////
    // FIX DES ROLLBACKS SERVEUR //
    ///////////////////////////////

    @Override
    public void tick() {
        if (!level().isClientSide) {
            this.xo = getX();
            this.yo = getY();
            this.zo = getZ();
        }

        super.tick();

        tickLerp();
        updateSounds();
    }

    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.steps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.steps > 0) {
            double d0 = getX() + (clientX - getX()) / (double) steps;
            double d2 = getZ() + (clientZ - getZ()) / (double) steps;
            --steps;
            setPos(d0, getY(), d2);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.clientX = x;
        this.clientY = y;
        this.clientZ = z;
        this.steps = 10;
    }
}
