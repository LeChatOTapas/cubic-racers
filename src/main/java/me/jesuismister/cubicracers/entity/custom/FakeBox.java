package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.init.KartItemsInit;
import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.util.ClientUtil;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class FakeBox extends ItemKartAbstract implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final String TEXTURE = "textures/entity/item_box.png";
    public static final String MODEL = "geo/item_box.geo.json";
    public static final String ANIMATION = "animations/item_box.animation.json";
    public static final float HITBOX_X = 1f;
    public static final float HITBOX_Y = 2f;

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
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
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
                    TestKart.stunKart(kart, "FakeBox");
                    if (!level().isClientSide)
                        ClientUtil.playSoundToAll(level(), getX(), getY(), getZ(), 8, SoundsInit.BANANA_HIT_KART.get(), SoundSource.RECORDS, 1f, 0.95f);
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
}
