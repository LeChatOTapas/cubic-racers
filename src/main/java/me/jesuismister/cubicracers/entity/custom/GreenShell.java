package me.jesuismister.cubicracers.entity.custom;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import me.jesuismister.cubicracers.event.network.Network;
import me.jesuismister.cubicracers.event.network.message.remove.GreenShellRemoveMessage;
import me.jesuismister.cubicracers.init.KartItemsInit;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class GreenShell extends Entity implements GeoEntity {
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(GreenShell.class, EntityDataSerializers.FLOAT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final float MAX_SPEED = 1.2f;

    public static final String TEXTURE = "textures/entity/green_shell.png";
    public static final String MODEL = "geo/green_shell.geo.json";
    public static final String ANIMATION = "animations/green_shell.animation.json";
    public static final float HITBOX = 1f;

    private static final int TICK_TO_DESPAWN = 20 * 20; //20s
    private int tickAlive = 0;
    private int bounceTime = 0;

    public GreenShell(EntityType<?> p_19870_, Level p_19871_) {
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
        //COTE CLIENT
        if (this.level().isClientSide()) {
            //RECUPERER TOUTES LES ENTITES PROCHES DE LA CARAPACE
            List<Entity> nearbyEntities = level().getEntities(this, getBoundingBox().inflate(0));

            for (Entity entity : nearbyEntities) {
                if (entity instanceof Kart kart) {
                    if (kart.getFirstPassenger() != null) {
                        Network.CHANNEL.sendToServer(new GreenShellRemoveMessage());
                        if (kart.canMove) {
                            Kart.stunKart(kart);
                        }
                        this.remove(RemovalReason.KILLED);
                        return;
                    }
                }
            }
        }

        //REBONDS
        if(this.horizontalCollision) bounce();

        //DEPLACEMENT DE LA CARAPACE
        setMovement(this);
        this.move(MoverType.SELF, new Vec3(this.getDeltaMovement().x, -1, this.getDeltaMovement().z));

        //DETRUIRE LA CARAPACE AU BOUT D'UN MOMENT
        tickAlive++;
        if (tickAlive > TICK_TO_DESPAWN) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    /**
     * Spawn la carapace verte derrière le kart
     *
     * @param kart
     */
    public static void spawnGreenShell(Kart kart) {
        if (kart.level() != null) {
            GreenShell green_shell = new GreenShell(KartItemsInit.GREEN_SHELL.get(), kart.level());
            float angle = (float) Math.toRadians(kart.getYRot());
            green_shell.setPos(kart.getX() + (-Math.sin(angle) * (3f + 1f * kart.getSpeed() / kart.MAX_SPEED)), kart.getY(), kart.getZ() + (Math.cos(angle) * (3f + 1f * kart.getSpeed() / kart.MAX_SPEED)));
            green_shell.setYRot(kart.getYRot());
            kart.level().addFreshEntity(green_shell);
        }
    }

    public static void setMovement(GreenShell green_shell) {
        green_shell.setSpeed(MAX_SPEED);

        double x = Math.sin(Math.toRadians(-green_shell.getYRot())) * MAX_SPEED;
        double z = Math.cos(Math.toRadians(-green_shell.getYRot())) * MAX_SPEED;
        Vec3 vec3 = new Vec3(x, 0, z);
        green_shell.setDeltaMovement(vec3);
    }

    public void setSpeed(float new_speed) {
        this.entityData.set(SPEED, new_speed);
    }

    @Override
    /**
     * La carapace peut comme le kart, monter au dessus des blocs de 1 de haut
     */
    public float getStepHeight() {
        return 1.2f;
    }

    private void bounce(){
        if (!this.level().getBlockState(this.blockPosition().relative(Direction.WEST)).is(Blocks.AIR) && !this.level().getBlockState(this.blockPosition().relative(Direction.WEST)).is(Blocks.WATER)) {
            this.setYRot(-this.getYRot());
            bounceTime++;
        }else if(!this.level().getBlockState(this.blockPosition().relative(Direction.EAST)).is(Blocks.AIR) && !this.level().getBlockState(this.blockPosition().relative(Direction.EAST)).is(Blocks.WATER)){
            this.setYRot(-this.getYRot());
            bounceTime++;
        }else if (!this.level().getBlockState(this.blockPosition().relative(Direction.NORTH)).is(Blocks.AIR) && !this.level().getBlockState(this.blockPosition().relative(Direction.EAST)).is(Blocks.WATER)) {
            this.setYRot(-180-this.getYRot());
            bounceTime++;
        }else if(!this.level().getBlockState(this.blockPosition().relative(Direction.SOUTH)).is(Blocks.AIR) && !this.level().getBlockState(this.blockPosition().relative(Direction.EAST)).is(Blocks.WATER)){
            this.setYRot(180-this.getYRot());
            bounceTime++;
        }

        if(bounceTime>4){
            this.remove(RemovalReason.KILLED);
        }
    }
}
