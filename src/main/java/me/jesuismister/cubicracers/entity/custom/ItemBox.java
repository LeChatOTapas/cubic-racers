package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.init.KartItemsInit;
import me.jesuismister.cubicracers.util.ClientRandom;
import me.jesuismister.cubicracers.util.ServerRandom;
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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class ItemBox extends Entity implements GeoEntity {
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(ItemBox.class, EntityDataSerializers.FLOAT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final String TEXTURE = "textures/entity/item_box.png";
    public static final String MODEL = "geo/item_box.geo.json";
    public static final String ANIMATION = "animations/item_box.animation.json";
    public static final float HITBOX_X = 1f;
    public static final float HITBOX_Y = 2f;

    private static final double BANANA_DROP_RATE = 25; //BORNE DE 0 à 30
    private static final double GREEN_SHELL_DROP_RATE = 50; //BORNE DE 0 à 30
    private static final double MUSHROOM_DROP_RATE = 65; //BORNE DE 30 à 60
    private static final double FAKE_BOX_DROP_RATE = 70; //BORNE DE 60 à 70
    private static final double BOMB_OMB_DROP_RATE = 80; //BORNE DE 70 à 80
    private static final double STAR_DROP_RATE = 90; //BORNE DE 80 à 90
    private static final double THUNDER_DROP_RATE = 95; //BORNE DE 90 à 95
    private static final double KLAXON_DROP_RATE = 100; //BORNE DE 95 à 100

    private static final int TICK_TO_GET_BACK_ITEM = 20 * 6; //6s
    private int tickDisabled = 0;
    private boolean hasItem = true;
    private boolean isFalse = false;

    public ItemBox(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public ItemBox(Level level, double x, double y, double z, boolean isFalse) {
        this(KartItemsInit.ITEM_BOX.get(), level);

        this.isFalse = isFalse;

        this.xo = Math.floor(x) + 0.5f;
        this.yo = y;
        this.zo = Math.floor(z) + 0.5f;
        this.setPos(xo, yo, zo);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if (hasItem) {
            tAnimationState.getController().setAnimation(RawAnimation.begin()
                    .then("box_on", Animation.LoopType.LOOP));
        } else {
            tAnimationState.getController().setAnimation(RawAnimation.begin()
                    .then("box_off", Animation.LoopType.HOLD_ON_LAST_FRAME));
        }
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

        //RECUPERER TOUTES LES ENTITES PROCHES DU CUBE
        List<Entity> nearbyEntities = level.getEntities(this, getBoundingBox().inflate(0.5f)); // Ajustez la valeur de l'inflation selon vos besoins

        //PARCOURIR LA LISTE DES ENTITES PROCHES
        for (Entity entity : nearbyEntities) {
            //ON CHECK QUE LES ENTITES "PLAYER"
            if (entity instanceof Player) {
                //ON CHECK QUE LES "PLAYER" DANS UN "KART"
                if (hasItem && entity.getVehicle() != null && entity.getVehicle() instanceof Kart kart) {
                    if(isFalse){
                        Kart.stunKart(kart);
                        this.remove(RemovalReason.KILLED);
                    }else{
                        hasItem = false;
                        tickDisabled = 0;
                        giveRandomItem(kart);
                    }

                    //POUR EVITER DE DONNER UN OBJET A PLUSIEURS VEHICULES
                    return;
                }
            }
        }

        //REAPPROVISIONNER LE CUBE AU BOUT DE X SECONDES
        if (!hasItem) {
            if (tickDisabled > TICK_TO_GET_BACK_ITEM) hasItem = true;
            tickDisabled++;
        }
    }

    /**
     * Méthode qui donne un item au kart donné en paramètre
     *
     * @param kart
     */
    public void giveRandomItem(Kart kart) {
        if(!kart.kartItem.equals("None")) return;

        double rand;
        if(this.getLevel().isClientSide()){
            rand = ClientRandom.nextInt(100);
        }else{
            rand = ServerRandom.nextInt(100);
        }

        if (0 <= rand && rand < BANANA_DROP_RATE) {
            kart.kartItem = "Banana";
        } else if (BANANA_DROP_RATE < rand && rand < GREEN_SHELL_DROP_RATE) {
            kart.kartItem = "Green_shell";
        } else if (GREEN_SHELL_DROP_RATE < rand && rand < MUSHROOM_DROP_RATE) {
            kart.kartItem = "Mushroom";
        } else if (MUSHROOM_DROP_RATE < rand && rand < FAKE_BOX_DROP_RATE) {
            kart.kartItem = "Fake_box";
        } else if (FAKE_BOX_DROP_RATE < rand && rand < BOMB_OMB_DROP_RATE) {
            kart.kartItem = "Bomb_omb";
        } else if (BOMB_OMB_DROP_RATE < rand && rand < STAR_DROP_RATE) {
            kart.kartItem = "Star";
        } else if (STAR_DROP_RATE < rand && rand < THUNDER_DROP_RATE) {
            kart.kartItem = "Thunder";
        } else if (THUNDER_DROP_RATE < rand && rand <= KLAXON_DROP_RATE) {
            kart.kartItem = "Klaxon";
        }
    }

    @Override
    /**
     * Méthode qui fait en sorte de détruire la box quand elle prend des dégats
     */
    public boolean hurt(DamageSource damage, float p_19947_) {
        if (damage.getEntity() instanceof Player player) {
            if (player.getVehicle() == null) {
                this.remove(RemovalReason.KILLED);
                return true;
            }
        }
        return false;
    }

    /**
     * Spawn le cube derrière le kart
     *
     * @param kart
     */
    public static void spawnFakeBox(Kart kart) {
        if (kart.getLevel() != null) {
            ItemBox fake_cube = new ItemBox(KartItemsInit.ITEM_BOX.get(), kart.getLevel());
            double angle = Math.toRadians(kart.getYRot());
            fake_cube.setPos(kart.getX() + (Math.sin(angle) * 2f), kart.getY(), kart.getZ() + (-Math.cos(angle) * 2f));
            fake_cube.isFalse = true;
            kart.getLevel().addFreshEntity(fake_cube);
        }
    }
}
