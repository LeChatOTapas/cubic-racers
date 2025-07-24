package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.util.ClientUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ItemKartAbstract extends Entity {
    protected int tickAlive = 0;
    protected int removeDelay = -1;

    public ItemKartAbstract(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public boolean isNoGravity() {
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

    /*
    public void stun(float range, String motif) {
        List<Entity> nearbyEntities = this.level().getEntities(this, this.getBoundingBox().inflate(range));
        for (Entity entity : nearbyEntities) {
            if (entity instanceof TestKart kart) {
                if (kart.getCanMove()){
                    TestKart.stunKart(kart, motif);
                    ClientUtil.playSoundToAll(level(), getX(), getY(), getZ(), 8, SoundsInit.BANANA_HIT_KART.get(), SoundSource.RECORDS, 1f, 0.95f);
                }
            }
        }
    }
    */
}
