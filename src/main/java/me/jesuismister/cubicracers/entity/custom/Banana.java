package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.util.ClientUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class Banana extends ItemKartAbstract implements GeoEntity {
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
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        tAnimationState.getController().setAnimation(RawAnimation.begin()
                .then("dancing", Animation.LoopType.LOOP));
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
            this.move(MoverType.SELF, new Vec3(getDeltaMovement().x, (1-Math.sqrt(propulsionY))*3, getDeltaMovement().z));
            propulsionY += 0.3f;
        }else{
            this.move(MoverType.SELF, new Vec3(0, -1, 0));
        }

        //RECUPERER TOUTES LES ENTITES PROCHES DE LA BANANE
        List<Entity> nearbyEntities = level().getEntities(this, getBoundingBox().inflate(0));

        if(!level().isClientSide()){
            for (Entity entity : nearbyEntities) {
                if (entity instanceof TestKart kart) {
                    if (kart.getFirstPassenger() != null) {
                        if (kart.getCanMove()) {
                            TestKart.stunKart(kart, "Banana");
                            ClientUtil.playSoundToAll(level(), getX(), getY(), getZ(), 8, SoundsInit.BANANA_HIT_KART.get(), SoundSource.RECORDS, 1f, 0.95f);
                        }
                        this.remove(RemovalReason.KILLED);
                        return;
                    }
                }
            }
        }

        //DETRUIRE LA BANANE AU BOUT D'UN MOMENT
        tickAlive++;
        if (tickAlive > TICK_TO_DESPAWN) {
            this.remove(RemovalReason.DISCARDED);
        }

        if (getIsPropulsing()) setIsPropulsing(false);

    }
}
