package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.network.Network;
import me.jesuismister.cubicracers.network.message.serverToClient.kartItem.ExplosionParticleMessage;
import me.jesuismister.cubicracers.network.message.serverToClient.StunMessage;
import me.jesuismister.cubicracers.util.ClientUtil;
import me.jesuismister.cubicracers.util.UtilityMethod;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class BobOmb extends ItemKartAbstract implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final String TEXTURE = "textures/entity/bob_omb.png";
    public static final String MODEL = "geo/bob_omb.geo.json";
    public static final String ANIMATION = "animations/bob_omb.animation.json";
    public static final float HITBOX = 1f;
    public static final int RANGE = 4;

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
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    protected void applyCollision() {
        List<Entity> nearbyEntities = level().getEntities(this, getBoundingBox().inflate(0));
        for (Entity entity : nearbyEntities) {
            if (entity instanceof TestKart kart && kart.getFirstPassenger() != null) {
                if (!kart.isStun()) {
                    TestKart.stunKart(kart, "BobOmb");
                }
                removeDelay = 2;
                break;
            }
        }
    }

    @Override
    protected int getMaxTimeAlive() {
        return 20 * 4; // 4s
    }

    @Override
    protected void checkEndOfLife() {
        if (removeDelay <= 0) {
            UtilityMethod.stunKartAround(this, RANGE, "Bob_omb");
            this.remove(RemovalReason.KILLED);
            ClientUtil.playSoundToAll(level(), getX(), getY(), getZ(), 8 + RANGE, SoundsInit.BOB_OMB_EXPLOSION.get(), SoundSource.RECORDS, 1f, 0.95f);
            Network.CHANNEL.send(PacketDistributor.ALL.noArg(), new ExplosionParticleMessage(this.getX(), this.getY(), this.getZ()));
            return;
        }
        removeDelay--;
    }
}
