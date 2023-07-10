package me.jesuismister.cubicracers.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public abstract class KartAbstract extends Entity {
    public static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT);

    //KEYS POUR LE KART
    public static final EntityDataAccessor<Boolean> isPressingKeyUp = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyDown = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyLeft = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyRight = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyDrift = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyItem = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyDelta = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> previousPressingKeyDelta = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);

    //ATTRIBUTS GENERAUX DES KARTS
    public static final float MIN_SPEED = 0.075f;
    public static final float FREINAGE_SPEED = 1.05f;
    public static final float BASE_FALL_SPEED = -0.5f;
    public static final float REDUCED_FALL_SPEED = -0.2f;
    public static final float FALL_SPEED_LIMIT = -3.0f;
    public static final float FALL_SPEED_MULTIPLIER = 1.05f;
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
    public static final EntityDataAccessor<Integer> deltaAnimationState = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> waterAnimationState = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> fallSpeed = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> pourcentage_inclinaison = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> actual_rotation_wheels = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT);

    //ANIMATION DEGATS
    public static final float SPINNING_ANIMATION_TIME = 20.0f * 2.0f;
    public static final EntityDataAccessor<Boolean> canMove = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> stunRotation = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT);

    //KART ITEM
    public static final EntityDataAccessor<String> kartItem = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Boolean> isInvinsible = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> starSpeedBoost = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT); //COEFF DE BOOST / 1 PAR DEFAUT / 1.5 SOUS ETOILE
    public static final EntityDataAccessor<Float> timeStar = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> isKlaxoning = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);

    public KartAbstract(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public float getSpeed() {
        return this.entityData.get(SPEED);
    }

    public void setSpeed(float value) {
        this.entityData.set(SPEED, value);
    }

    private int steps;
    private double clientX;
    private double clientY;
    private double clientZ;
    private double clientYaw;
    private double clientPitch;

    //

    public boolean getIsPressingKeyUp() {
        return this.entityData.get(isPressingKeyUp);
    }

    public void setIsPressingKeyUp(boolean value) {
        this.entityData.set(isPressingKeyUp, value);
    }

    public boolean getIsPressingKeyDown() {
        return this.entityData.get(isPressingKeyDown);
    }

    public void setIsPressingKeyDown(boolean value) {
        this.entityData.set(isPressingKeyDown, value);
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

    public int getDeltaAnimationState() {
        return this.entityData.get(deltaAnimationState);
    }

    public void setDeltaAnimationState(int value) {
        this.entityData.set(deltaAnimationState, value);
    }

    public int getWaterAnimationState() {
        return this.entityData.get(waterAnimationState);
    }

    public void setWaterAnimationState(int value) {
        this.entityData.set(waterAnimationState, value);
    }

    public float getFallSpeed() {
        return this.entityData.get(fallSpeed);
    }

    public void setFallSpeed(float value) {
        this.entityData.set(fallSpeed, value);
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
        return this.entityData.get(kartItem);
    }

    public void setKartItem(String value) {
        this.entityData.set(kartItem, value);
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

    public boolean getIsKlaxoning() {
        return this.entityData.get(isKlaxoning);
    }

    public void setIsKlaxoning(boolean value) {
        this.entityData.set(isKlaxoning, value);
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
            if (this != null && this.getFirstPassenger() != null && this.getFirstPassenger() instanceof Player) {
                this.getFirstPassenger().sendSystemMessage(Component.literal(msg));
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
        super.tick();
        tickLerp();
    }

    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.steps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.steps > 0) {
            double d0 = getX() + (clientX - getX()) / (double) steps;
            double d1 = getY() + (clientY - getY()) / (double) steps;
            double d2 = getZ() + (clientZ - getZ()) / (double) steps;
            double d3 = Mth.wrapDegrees(clientYaw - (double) getYRot());
            setYRot((float) ((double) getYRot() + d3 / (double) steps));
            setXRot((float) ((double) getXRot() + (clientPitch - (double) getXRot()) / (double) steps));
            --steps;
            setPos(d0, d1, d2);
            setRot(getYRot(), getXRot());
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.clientX = x;
        this.clientY = y;
        this.clientZ = z;
        this.clientYaw = yaw;
        this.clientPitch = pitch;
        this.steps = 10;
    }
}
