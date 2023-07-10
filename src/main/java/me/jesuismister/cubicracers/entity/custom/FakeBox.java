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
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class FakeBox extends Entity implements GeoEntity {
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(FakeBox.class, EntityDataSerializers.FLOAT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final String TEXTURE = "textures/entity/item_box.png";
    public static final String MODEL = "geo/item_box.geo.json";
    public static final String ANIMATION = "animations/item_box.animation.json";
    public static final float HITBOX_X = 1f;
    public static final float HITBOX_Y = 2f;

    private static final float TICK_TO_DESPAWN = 20f * 90f; //5s
    private float tickAlive = 0;

    private static final int TICK_TO_GET_BACK_ITEM = 20 * 6; //6s
    private int tickDisabled = 0;

    public FakeBox(EntityType<?> p_19870_, Level p_19871_) {

        super(p_19870_, p_19871_);
    }

    public FakeBox(Level level, double x, double y, double z) {
        this(KartItemsInit.ITEM_BOX.get(), level);

        this.xo = Math.floor(x) + 0.5f;
        this.yo = y;
        this.zo = Math.floor(z) + 0.5f;
        this.setPos(xo, yo, zo);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        tAnimationState.getController().setAnimation(RawAnimation.begin()
                .then("box_on", Animation.LoopType.LOOP));
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
            //RECUPERER TOUTES LES ENTITES PROCHES DU CUBE
            List<Entity> nearbyEntities = level().getEntities(this, getBoundingBox().inflate(0.5f)); // Ajustez la valeur de l'inflation selon vos besoins

            //PARCOURIR LA LISTE DES ENTITES PROCHES
            for (Entity entity : nearbyEntities) {
                //ON CHECK QUE LES ENTITES "KART"
                if (entity instanceof Kart kart) {
                    //Network.CHANNEL.sendToServer(new ItemBoxConsumeMessage(""));
                    Kart.stunKart(kart);
                    this.remove(RemovalReason.KILLED);
                }
        }

        //LE CUBE DESPAWN AU BOUT DE X SECS
        tickAlive++;
        if (tickAlive > TICK_TO_DESPAWN) {
            this.remove(RemovalReason.KILLED);
        }
        this.move(MoverType.SELF, new Vec3(0, -1, 0));
    }

    /**
     * Spawn le cube derrière le kart
     *
     * @param kart
     */
    public static void spawnFakeBox(Kart kart) {
        FakeBox fake_cube = new FakeBox(KartItemsInit.FAKE_BOX.get(), kart.level());

        double angle = Math.toRadians(kart.getYRot());
        fake_cube.setPos(kart.getX() + (Math.sin(angle) * 3f), kart.getY(), kart.getZ() + (-Math.cos(angle) * 3f));
        kart.level().addFreshEntity(fake_cube);
    }
}
