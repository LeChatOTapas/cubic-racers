package me.jesuismister.mckart.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Kart extends Entity implements GeoEntity {
    private static final EntityDataAccessor<Float> SPEED =
            SynchedEntityData.defineId(Kart.class, EntityDataSerializers.FLOAT);
    private AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);



    /////////////
    // DE BASE //
    /////////////
    public Kart(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SPEED, 0.0f);
    }

    @Override //TO DO ?????????????
    protected void readAdditionalSaveData(CompoundTag p_20052_) {}

    @Override //TO DO ?????????????
    protected void addAdditionalSaveData(CompoundTag p_20139_) {}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 20, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        /*
        if(tAnimationState.isMoving()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.trash_kart.roues_qui_tournent", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.trash_kart.roues_qui_tournent_pas", Animation.LoopType.LOOP));
         */
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    ////////////
    // AJOUTS //
    ////////////
    @Override
    /**
     * Est-ce qu'on peut le ramasser (en cliquant dessus ?)
     */
    public boolean isPickable() {
        return true;
    }

    @Override
    /**
     * Jusqu'à quel hauteur il peut monter sans sauter (ici 1 bloc de haut)
     */
    public float getStepHeight() {
        return 1.0f;
    }

    @Override
    /**
     * Peut-on la pousser (les collisions en générale)
     */
    public boolean isPushable() {
        return false;
    }

    @Override
    /**
     * Position du joueur dans le kart
     */
    public void positionRider(Entity player) {
        super.positionRider(player);
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        player.setPos(x, y - 0.8, z);
    }

    @Override
    /**
     * Gestion des interraction avec le kart
     */
    public InteractionResult interact(Player player, InteractionHand hand) {
        //Si le joueur interragis avec le kart + sneak en même temps (???)
        if (player.isShiftKeyDown()){
            //Si le joueur est passagé du kart
            if (this.hasPassenger(player)){
                //Alors le joueur sort du kart
                player.stopRiding();
                return InteractionResult.SUCCESS;
            }
            else{
                return InteractionResult.PASS;
            }
        }
        //Si le joueur interragis avec le kart
        else {
            //Si le joueur n'est pas déjà dans le kart
            if(!this.hasPassenger(player)){
                //Alors le joueur monte dans le kart
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            }
            else{
                return InteractionResult.PASS;
            }
        }
    }

    @Override
    public boolean startRiding(Entity rider) {
        Player player = (Player) rider;
        return super.startRiding(player);
    }

    @Override
    /**
     * Gestion des collisions -> True = pas de collision / False = Collision
     */
    public boolean isColliding(BlockPos blockPos, BlockState blockState) {
        return true; //OKLM y a pas de collision
        //return !this.level.noCollision(this.getBoundingBox(), this);
        /*
        La méthode this.level.noCollision(this.getBoundingBox(), this) est utilisée pour vérifier s'il y a
        une collision entre la boîte englobante (bounding box) de l'entité et d'autres entités ou des blocs
        solides dans le monde. Si noCollision renvoie true, cela signifie qu'il n'y a pas de collision et donc
        isColliding retourne false. Si noCollision renvoie false, cela signifie qu'il y a une collision et donc
        isColliding retourne true.
         */
    }

    @Override
    /**
     * Le conducteur peut interragir (??? - tester de voir à false ce que sa fait)
     */
    public boolean canRiderInteract() {
        return true;
    }

    @Override
    /**
     * Peut monter le kart
     */
    protected boolean canRide(Entity rider) {
        return true;
    }
}
