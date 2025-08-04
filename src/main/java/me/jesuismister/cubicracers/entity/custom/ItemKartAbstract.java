package me.jesuismister.cubicracers.entity.custom;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;

public abstract class ItemKartAbstract extends Entity implements GeoEntity {
    protected int tickAlive = 0;
    protected int removeDelay = -1;

    public ItemKartAbstract(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public boolean isNoGravity() {
        return false;
    }

    @Override
    public boolean hurtClient(DamageSource damageSource) {
        if (damageSource.getEntity() instanceof Player player) {
            if (player.isCreative()) {
                if (player.getVehicle() == null) {
                    this.remove(RemovalReason.KILLED);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        applyVelocity();

        if (tickAlive < getMaxTimeAlive() && removeDelay < 0) {
            applyCollision();
        } else if (!level().isClientSide) {
            checkEndOfLife();
        }
        tickAlive++;
    }

    protected void applyVelocity() {
        if (!onGround()) {
            Vec3 velocity = getDeltaMovement();
            move(MoverType.SELF, velocity);
            setDeltaMovement(velocity.x, velocity.y - 0.3, velocity.z);
        }
    }

    abstract protected void checkEndOfLife();

    abstract protected void applyCollision();

    abstract protected int getMaxTimeAlive();

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float v) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
    }
}
