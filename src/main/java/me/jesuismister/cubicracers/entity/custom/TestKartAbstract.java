package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.block.KartController;
import me.jesuismister.cubicracers.config.RoadBlockConfig;
import me.jesuismister.cubicracers.init.BlockInit;
import me.jesuismister.cubicracers.init.ItemInit;
import me.jesuismister.cubicracers.tags.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
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

import java.util.function.Supplier;

public abstract class TestKartAbstract extends Entity implements GeoEntity {
    public static final EntityDataAccessor<Float> Speed = SynchedEntityData.defineId(TestKartAbstract.class, EntityDataSerializers.FLOAT);
    public int id;

    //ATTRIBUTS DU KART
    public static final EntityDataAccessor<Float> MAX_SPEED = SynchedEntityData.defineId(TestKartAbstract.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> DELTA_SPEED = SynchedEntityData.defineId(TestKartAbstract.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> ACCELERATION_BOOST = SynchedEntityData.defineId(TestKartAbstract.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> BOOST = SynchedEntityData.defineId(TestKartAbstract.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> MANIABILITE_COEEF = SynchedEntityData.defineId(TestKartAbstract.class, EntityDataSerializers.FLOAT);

    //KEYS POUR LE KART
    public static final EntityDataAccessor<Boolean> isPressingKeyAccelerate = SynchedEntityData.defineId(TestKartAbstract.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyDeccelerate = SynchedEntityData.defineId(TestKartAbstract.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyForward = SynchedEntityData.defineId(TestKartAbstract.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyBackward = SynchedEntityData.defineId(TestKartAbstract.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyLeft = SynchedEntityData.defineId(TestKartAbstract.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyRight = SynchedEntityData.defineId(TestKartAbstract.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyDrift = SynchedEntityData.defineId(TestKartAbstract.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyItem = SynchedEntityData.defineId(TestKartAbstract.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> isPressingKeyDelta = SynchedEntityData.defineId(TestKartAbstract.class, EntityDataSerializers.BOOLEAN);

    //ATTRIBUTS GENERAUX DES KARTS
    public static final float MIN_SPEED = 0.075f;
    public static final float FREINAGE_SPEED = 1.05f;
    public static final float GRAVITY = 0.07f;
    public static final float COEFF_FROTTEMENT = 0.85f;
    public static final double TERMINAL_VELOCITY = 5f;

    //ATTRIBUTS DU DRIFT
    public static final float DRIFT_ANGLE = 1.5f;
    public boolean isDrifting = false;
    public String driftingSens = "None";
    public float driftingTime = 0;
    public float driftTimeBoost = 0;
    public float timeBoost = 0;

    //ATTRIBUTS DE CONDUITE
    public boolean deltaOn = false;
    public float pourcentage_inclinaison = 0;
    public float actual_rotation_wheels = 0;
    public static final EntityDataAccessor<String> stunMotif = SynchedEntityData.defineId(TestKartAbstract.class, EntityDataSerializers.STRING);

    //ANIMATION DEGATS
    public boolean canMove = true;
    public float stunRotation = 0;

    //KART ITEM
    private String kartItem = "None";
    public boolean isInvincible = false;
    public float starSpeedBoost = 1.5f; //COEFF DE BOOST / 1 PAR DEFAUT / 1.5 SOUS ETOILE
    public float timeStar = 0;

    //LOCK
    public boolean isLock = false;

    public TestKartAbstract(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean canBeCollidedWith(@Nullable Entity p_423669_) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_376157_) {
        // super.defineSynchedData(p_376157_);
        entityData.set(MAX_SPEED, 0.0f);
        entityData.set(DELTA_SPEED, 0.0f);
        entityData.set(ACCELERATION_BOOST, 0.0f);
        entityData.set(BOOST, 0.0f);
        entityData.set(MANIABILITE_COEEF, 0.0f);

        entityData.set(isPressingKeyAccelerate, false);
        entityData.set(isPressingKeyDeccelerate, false);
        entityData.set(isPressingKeyForward, false);
        entityData.set(isPressingKeyBackward, false);
        entityData.set(isPressingKeyLeft, false);
        entityData.set(isPressingKeyRight, false);
        entityData.set(isPressingKeyDrift, false);
        entityData.set(isPressingKeyItem, false);
        entityData.set(isPressingKeyDelta, false);

        entityData.set(Speed, 0.0f);

        entityData.set(stunMotif, "None");
    }

    @Override
    public boolean hurtClient(DamageSource damage) {
        if (damage.getEntity() instanceof Player player) {
            if (this.getFirstPassenger() == null) {
                remove(RemovalReason.KILLED);
                if (!player.isCreative() && !level().isClientSide()) {
                    Item spawn_item = ItemInit.KARTS_SPAWN_ITEM.get(id).get();
                    player.level().addFreshEntity(new ItemEntity(player.level(), getX(), getY(), getZ(), new ItemStack(spawn_item)));
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float v) {
        return hurtClient(damageSource);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
    }

    @Override
    protected float nextStep() {
        return 1.2f;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ItemInit.KARTS_SPAWN_ITEM.get(id).get());
    }

    public float getMAX_SPEED() {
        return this.entityData.get(MAX_SPEED);
    }

    public void setMAX_SPEED(float value) {
        this.entityData.set(MAX_SPEED, value, true);
    }

    public float getDELTA_SPEED() {
        return this.entityData.get(DELTA_SPEED);
    }

    public void setDELTA_SPEED(float value) {
        this.entityData.set(DELTA_SPEED, value, true);
    }

    public float getACCELERATION_BOOST() {
        return this.entityData.get(ACCELERATION_BOOST);
    }

    public void setACCELERATION_BOOST(float value) {
        this.entityData.set(ACCELERATION_BOOST, value, true);
    }

    public float getBOOST() {
        return this.entityData.get(BOOST);
    }

    public void setBOOST(float value) {
        this.entityData.set(BOOST, value, true);
    }

    public float getMANIABILITE_COEEF() {
        return this.entityData.get(MANIABILITE_COEEF);
    }

    public void setMANIABILITE_COEEF(float value) {
        this.entityData.set(MANIABILITE_COEEF, value, true);
    }

    public String getStunMotif() {
        return this.entityData.get(stunMotif);
    }

    public void setStunMotif(String value) {
        this.entityData.set(stunMotif, value, true);
    }

    public float getSpeed() {
        return this.entityData.get(Speed);
    }

    public void setSpeed(float value) {
        this.entityData.set(Speed, value, true);
    }

    public boolean isPressingKeyAccelerate() {
        return this.entityData.get(isPressingKeyAccelerate);
    }

    public void setPressingKeyAccelerate(boolean value) {
        this.entityData.set(isPressingKeyAccelerate, value, true);
    }

    public boolean isPressingKeyDeccelerate() {
        return this.entityData.get(isPressingKeyDeccelerate);
    }

    public void setPressingKeyDeccelerate(boolean value) {
        this.entityData.set(isPressingKeyDeccelerate, value, true);
    }

    public boolean isPressingKeyForward() {
        return this.entityData.get(isPressingKeyForward);
    }

    public void setPressingKeyForward(boolean value) {
        this.entityData.set(isPressingKeyForward, value, true);
    }

    public boolean isPressingKeyBackward() {
        return this.entityData.get(isPressingKeyBackward);
    }

    public void setPressingKeyBackward(boolean value) {
        this.entityData.set(isPressingKeyBackward, value, true);
    }

    public boolean isPressingKeyLeft() {
        return this.entityData.get(isPressingKeyLeft);
    }

    public void setPressingKeyLeft(boolean value) {
        this.entityData.set(isPressingKeyLeft, value, true);
    }

    public boolean isPressingKeyRight() {
        return this.entityData.get(isPressingKeyRight);
    }

    public void setPressingKeyRight(boolean value) {
        this.entityData.set(isPressingKeyRight, value, true);
    }

    public boolean isPressingKeyDrift() {
        return this.entityData.get(isPressingKeyDrift);
    }

    public void setPressingKeyDrift(boolean value) {
        this.entityData.set(isPressingKeyDrift, value, true);
    }

    public boolean isPressingKeyItem() {
        return this.entityData.get(isPressingKeyItem);
    }

    public void setPressingKeyItem(boolean value) {
        this.entityData.set(isPressingKeyItem, value, true);
    }

    public boolean isPressingKeyDelta() {
        return this.entityData.get(isPressingKeyDelta);
    }

    public void setPressingKeyDelta(boolean value) {
        this.entityData.set(isPressingKeyDelta, value, true);
    }

    public boolean isDrifting() {
        return isDrifting;
    }

    public void setDrifting(boolean drifting) {
        isDrifting = drifting;
    }

    public String getDriftingSens() {
        return driftingSens;
    }

    public void setDriftingSens(String driftingSens) {
        this.driftingSens = driftingSens;
    }

    public float getDriftingTime() {
        return driftingTime;
    }

    public void setDriftingTime(float driftingTime) {
        this.driftingTime = driftingTime;
    }

    public float getDriftTimeBoost() {
        return driftTimeBoost;
    }

    public void setDriftTimeBoost(float driftTimeBoost) {
        this.driftTimeBoost = driftTimeBoost;
    }

    public float getTimeBoost() {
        return timeBoost;
    }

    public void setTimeBoost(float timeBoost) {
        this.timeBoost = timeBoost;
    }

    public boolean isDeltaOn() {
        return deltaOn;
    }

    public void setDeltaOn(boolean deltaOn) {
        this.deltaOn = deltaOn;
    }

    public float getPourcentage_inclinaison() {
        return pourcentage_inclinaison;
    }

    public void setPourcentage_inclinaison(float pourcentage_inclinaison) {
        this.pourcentage_inclinaison = pourcentage_inclinaison;
    }

    public float getActual_rotation_wheels() {
        return actual_rotation_wheels;
    }

    public void setActual_rotation_wheels(float actual_rotation_wheels) {
        this.actual_rotation_wheels = actual_rotation_wheels;
    }

    public boolean getCanMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public float getStunRotation() {
        return stunRotation;
    }

    public void setStunRotation(float stunRotation) {
        this.stunRotation = stunRotation;
    }

    public String getKartItem() {
        return kartItem;
    }

    public void setKartItem(String kartItem) {
        this.kartItem = kartItem;
    }

    public boolean isInvincible() {
        return isInvincible;
    }

    public void setInvincible(boolean invincible) {
        isInvincible = invincible;
    }

    public float getStarSpeedBoost() {
        return starSpeedBoost;
    }

    public void setStarSpeedBoost(float starSpeedBoost) {
        this.starSpeedBoost = starSpeedBoost;
    }

    public float getTimeStar() {
        return timeStar;
    }

    public void setTimeStar(float timeStar) {
        this.timeStar = timeStar;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    ////////////
    // AUTRES //
    ////////////

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
    public boolean isPushedByFluid() {
        return true;
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

    public BlockState getBlock(int x, int y, int z) {
        return this.level().getBlockState(new BlockPos(x, y, z));
    }

    public boolean isOnRoadBlock() {
        if (!RoadBlockConfig.ROAD_BLOCK_REQUIRE.get()) return true;

        int blockX = (int) Math.floor(getX());
        int blockY = (int) Math.floor(getY());
        int blockZ = (int) Math.floor(getZ());

        if (getBlock(blockX, blockY - 1, blockZ).is(ModTags.Blocks.ROAD_BLOCK_TAG) || RoadBlockConfig.ROAD_BLOCKS.get().contains(getBlock(blockX, blockY - 1, blockZ).getBlock().getName().toString())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isOnKartController() {
        int blockX = (int) Math.floor(getX());
        int blockY = (int) Math.floor(getY()) - 1;
        int blockZ = (int) Math.floor(getZ());
        BlockState blockState = getBlock(blockX, blockY, blockZ);

        if (blockState.is(BlockInit.KART_CONTROLLER.get())) {
            if (blockState.getValue(KartController.LIT)) {
                setSpeed(0);
                if (blockState.getValue(KartController.FACING).getOpposite().equals(Direction.NORTH)) {
                    setYRot(180);
                } else if (blockState.getValue(KartController.FACING).getOpposite().equals(Direction.WEST)) {
                    setYRot(90);
                } else if (blockState.getValue(KartController.FACING).getOpposite().equals(Direction.EAST)) {
                    setYRot(-90);
                } else if (blockState.getValue(KartController.FACING).getOpposite().equals(Direction.SOUTH)) {
                    setYRot(0);
                }
                setPos(blockX + 0.5d, blockY + 1d, blockZ + 0.5d);
                return true;
            }
        }
        return false;
    }

    //////////////
    // GECKOLIB //
    //////////////
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation ENGINE = RawAnimation.begin().thenLoop("engine");
    protected static final RawAnimation PROPELLER_ON = RawAnimation.begin().thenPlayAndHold("propeller_on");
    protected static final RawAnimation PROPELLER_OFF = RawAnimation.begin().thenPlayAndHold("propeller_off");
    protected static final RawAnimation GLIDER_ON = RawAnimation.begin().thenPlayAndHold("glider_on");
    protected static final RawAnimation GLIDER_OFF = RawAnimation.begin().thenPlayAndHold("glider_off");
    protected static final RawAnimation DRIFT_OFF = RawAnimation.begin().thenLoop("drift_off");
    protected static final RawAnimation DRIFT_B = RawAnimation.begin().thenLoop("drift_b");
    protected static final RawAnimation DRIFT_O = RawAnimation.begin().thenLoop("drift_o");
    protected static final RawAnimation DRIFT_V = RawAnimation.begin().thenLoop("drift_v");

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("controller_engine", 0, this::predicate_engine));
        controllers.add(new AnimationController<>("controller_propeller", 0, this::predicate_propeller));
        controllers.add(new AnimationController<>("controller_glider", 0, this::predicade_glider));
        controllers.add(new AnimationController<>("controller_drift", 0, this::predicate_drift));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    private PlayState predicate_engine(AnimationTest<GeoAnimatable> tAnimationState) {
        return tAnimationState.setAndContinue(ENGINE);
    }

    private PlayState predicate_propeller(AnimationTest<GeoAnimatable> tAnimationState) {
        if (isInWater()) {
            return tAnimationState.setAndContinue(PROPELLER_ON);
        } else {
            return tAnimationState.setAndContinue(PROPELLER_OFF);
        }
    }

    private PlayState predicade_glider(AnimationTest<GeoAnimatable> tAnimationState) {
        if (isDeltaOn()) {
            return tAnimationState.setAndContinue(GLIDER_ON);
        } else {
            return tAnimationState.setAndContinue(GLIDER_OFF);
        }
    }

    private PlayState predicate_drift(AnimationTest<GeoAnimatable> tAnimationState) {
        if (getDriftingTime() >= 3) {
            return tAnimationState.setAndContinue(DRIFT_V);
        }
        //ANIMATION DES PARTICULES ORANGE
        else if (getDriftingTime() >= 2) {
            return tAnimationState.setAndContinue(DRIFT_O);
        }
        //ANIMATION DES PARTICULES BLEUES
        else if (getDriftingTime() >= 1) {
            return tAnimationState.setAndContinue(DRIFT_B);
        }
        //PAS D'ANIMATION DE DRIFT
        else {
            return tAnimationState.setAndContinue(DRIFT_OFF);
        }
    }
}
