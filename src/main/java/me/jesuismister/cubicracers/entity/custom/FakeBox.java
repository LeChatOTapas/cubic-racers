package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.init.KartItemsInit;
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

    private static final float TICK_TO_DESPAWN = 20f * 90f; //5s
    private float tickAlive = 0;

    private float propulsionY = -1f;

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
    public void tick() {
        super.tick();
        if (getIsPropulsing() && !onGround()) {
            double x = Math.sin(Math.toRadians(-getYRot())) * 3.5;
            double z = Math.cos(Math.toRadians(-getYRot())) * 3.5;
            Vec3 vec3 = new Vec3(x, 0, z);
            setDeltaMovement(vec3);
            this.move(MoverType.SELF, new Vec3(getDeltaMovement().x, (1 - Math.sqrt(propulsionY)) * 3, getDeltaMovement().z));
            propulsionY += 0.3f;
        } else {
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
            if (getIsPropulsing()) setIsPropulsing(false);
            this.move(MoverType.SELF, new Vec3(0, -1, 0));
        }
    }
}
