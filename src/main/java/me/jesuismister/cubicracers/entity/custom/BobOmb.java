package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.network.Network;
import me.jesuismister.cubicracers.network.message.itemsKart.particles.ExplosionParticleMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
import java.util.Random;

public class BobOmb extends ItemKartAbstract implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final String TEXTURE = "textures/entity/bob_omb.png";
    public static final String MODEL = "geo/bob_omb.geo.json";
    public static final String ANIMATION = "animations/bob_omb.animation.json";

    public static final float HITBOX = 1f;
    private static final float RANGE = 4;

    private static final float TICK_TO_DESPAWN = 20f * 4f; //5s

    public static final EntityDataAccessor<Boolean> shouldExplode = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    private float tickAlive = 0;

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
        entityData.define(shouldExplode, false);
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
            if (getShouldExplode()) {
                stun(RANGE, "Bob_omb");
                if(!level().isClientSide()) {
                    sendExplosionParticle();
                    this.remove(RemovalReason.KILLED);
                }
                return;
            }

            //RECUPERER TOUTES LES ENTITES PROCHES DE LA BOB OM
            List<Entity> nearbyEntities = level().getEntities(this, getBoundingBox().inflate(0));
            for (Entity entity : nearbyEntities) {
                if (entity instanceof Kart) {
                    setShouldExplode(true);
                    break;
                }
            }

            tickAlive++;
            if (tickAlive > TICK_TO_DESPAWN) {
                setShouldExplode(true);
            }

            if (getIsPropulsing()) setIsPropulsing(false);
            this.move(MoverType.SELF, new Vec3(0, -1, 0));
        }
    }

    private void sendExplosionParticle() {
        Network.CHANNEL.send(PacketDistributor.ALL.noArg(), new ExplosionParticleMessage(this.getX(), this.getY(), this.getZ()));
    }

    public static void spawnExplosionParticles(double x, double y, double z) {
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            double offsetX = random.nextGaussian() * RANGE;
            double offsetY = random.nextGaussian() * RANGE;
            double offsetZ = random.nextGaussian() * RANGE;

            Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.EXPLOSION, x, y, z, offsetX, offsetY, offsetZ);
        }
    }

    public boolean getShouldExplode(){
        return this.entityData.get(shouldExplode);
    }

    public void setShouldExplode(boolean value){
        this.entityData.set(shouldExplode, value);
    }
}
