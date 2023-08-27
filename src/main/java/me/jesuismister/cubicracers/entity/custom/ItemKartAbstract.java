package me.jesuismister.cubicracers.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ItemKartAbstract extends Entity {
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(Banana.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> isPropulsing = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public float propulsionY = -1f;

    public ItemKartAbstract(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(SPEED, 0.0f);
        entityData.define(isPropulsing, false);
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
    protected void readAdditionalSaveData(@NotNull CompoundTag p_20052_) {
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag p_20139_) {
    }

    @Override
    public boolean hurt(DamageSource damage, float p_19947_) {
        if (damage.getEntity() instanceof Player player) {
            if(player.isCreative()){
                if (player.getVehicle() == null) {
                    this.remove(RemovalReason.KILLED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean getIsPropulsing() {
        return this.entityData.get(isPropulsing);
    }

    public void setIsPropulsing(boolean value) {
        this.entityData.set(isPropulsing, value);
    }

    public void setSpeed(float new_speed) {
        this.entityData.set(SPEED, new_speed);
    }

    /**
     * Spawn l'item devant le kart
     *
     * @param kart
     */
    public static void spawnItemFront(Kart kart, ItemKartAbstract item) {
        if (kart.level() != null) {
            double angle = Math.toRadians(kart.getYRot());
            item.setPos(kart.getX() + (-Math.sin(angle) * kart.HITBOX_X*2f), kart.getY() + 0.2, kart.getZ() + (Math.cos(angle) * kart.HITBOX_X*2f));
            item.setYRot(kart.getYRot());
            item.setIsPropulsing(true);
            kart.level().addFreshEntity(item);
        }
    }

    /**
     * Spawn l'item derrière le kart
     *
     * @param kart
     */
    public static void spawnItemBack(Kart kart, ItemKartAbstract item) {
        if (kart.level() != null) {
            double angle = Math.toRadians(kart.getYRot());
            item.setPos(kart.getX() + (Math.sin(angle) * kart.HITBOX_X*1.5f), kart.getY(), kart.getZ() + (-Math.cos(angle) * kart.HITBOX_X*1.5f));
            item.setYRot(kart.getYRot() + 180);
            kart.level().addFreshEntity(item);
        }
    }

    /**
     * Stun tous les karts proches
     */
    public void stun(float range, String motif) {
        List<Entity> nearbyEntities = this.level().getEntities(this, this.getBoundingBox().inflate(range));
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Kart kart) {
                if (kart.getCanMove()) Kart.stunKart(kart, motif);
            }
        }
    }
}
