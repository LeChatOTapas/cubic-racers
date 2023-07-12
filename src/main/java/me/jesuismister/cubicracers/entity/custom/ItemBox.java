package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.init.KartItemsInit;
import me.jesuismister.cubicracers.network.Network;
import me.jesuismister.cubicracers.network.message.ItemToClientMessage;
import me.jesuismister.cubicracers.util.ClientRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Supplier;

public class ItemBox extends ItemKartAbstract implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final String TEXTURE = "textures/entity/item_box.png";
    public static final String MODEL = "geo/item_box.geo.json";
    public static final String ANIMATION = "animations/item_box.animation.json";
    public static final float HITBOX_X = 1f;
    public static final float HITBOX_Y = 2f;

    private static final double BANANA_DROP_RATE = 12.5;
    private static final double GREEN_SHELL_DROP_RATE = 25;
    private static final double MUSHROOM_DROP_RATE = 37.5;
    private static final double FAKE_BOX_DROP_RATE = 50;
    private static final double BOMB_OMB_DROP_RATE = 62.5;
    private static final double STAR_DROP_RATE = 75;
    private static final double THUNDER_DROP_RATE = 87.5;
    private static final double KLAXON_DROP_RATE = 100;

    private static final int TICK_TO_GET_BACK_ITEM = 20 * 4; //4s
    public static final EntityDataAccessor<Boolean> hasItem = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> tickDisabled = SynchedEntityData.defineId(Kart.class, EntityDataSerializers.INT);

    public ItemBox(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public ItemBox(Level level, double x, double y, double z) {
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
        if (getHasItem()) {
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
        super.defineSynchedData();
        this.entityData.define(hasItem, true);
        this.entityData.define(tickDisabled, 0);
    }

    @Override
    public void tick() {
        super.tick();
        //SI IL Y A UN ITEM DE DISPO DANS LE CUBE
        if (getHasItem()) {
            //RECUPERER TOUTES LES ENTITES PROCHES DU CUBE
            List<Entity> nearbyEntities = level().getEntities(this, getBoundingBox().inflate(0.5f)); // Ajustez la valeur de l'inflation selon vos besoins

            //PARCOURIR LA LISTE DES ENTITES PROCHES
            for (Entity entity : nearbyEntities) {
                //ON CHECK QUE LES ENTITES "KART"
                if (entity instanceof Kart kart) {
                    if (!level().isClientSide() && giveRandomItem(kart)) {
                        setHasItem(false);
                        setTickDisabled(0);
                        break;
                    }
                }
            }
        }

        //REAPPROVISIONNER LE CUBE AU BOUT DE X SECONDES
        if (!getHasItem()) {
            if (getTickDisabled() > TICK_TO_GET_BACK_ITEM) setHasItem(true);
            setTickDisabled(getTickDisabled() + 1);
        }

    }

    /**
     * Méthode qui donne un item au kart donné en paramètre
     *
     * @param kart
     */
    public boolean giveRandomItem(Kart kart) {
        if (!kart.getKartItem().equals("None") && kart.getFirstPassenger() != null && kart.getFirstPassenger() instanceof Player)
            return false;

        double rand = ClientRandom.nextInt(100);

        if (0 <= rand && rand < BANANA_DROP_RATE) {
            kart.setKartItem("Banana");
        } else if (BANANA_DROP_RATE <= rand && rand < GREEN_SHELL_DROP_RATE) {
            kart.setKartItem("Green_shell");
        } else if (GREEN_SHELL_DROP_RATE <= rand && rand < MUSHROOM_DROP_RATE) {
            kart.setKartItem("Mushroom");
        } else if (MUSHROOM_DROP_RATE <= rand && rand < FAKE_BOX_DROP_RATE) {
            kart.setKartItem("Fake_box");
        } else if (FAKE_BOX_DROP_RATE <= rand && rand < BOMB_OMB_DROP_RATE) {
            kart.setKartItem("Bomb_omb");
        } else if (BOMB_OMB_DROP_RATE <= rand && rand < STAR_DROP_RATE) {
            kart.setKartItem("Star");
        } else if (STAR_DROP_RATE <= rand && rand < THUNDER_DROP_RATE) {
            kart.setKartItem("Thunder");
        } else if (THUNDER_DROP_RATE <= rand && rand <= KLAXON_DROP_RATE) {
            kart.setKartItem("Klaxon");
        }

        ServerPlayer player = (ServerPlayer) kart.getFirstPassenger();
        Network.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ItemToClientMessage(kart.getKartItem()));

        return true;
    }

    public boolean getHasItem() {
        return this.entityData.get(hasItem);
    }

    public void setHasItem(boolean value) {
        this.entityData.set(hasItem, value);
    }

    public int getTickDisabled() {
        return this.entityData.get(tickDisabled);
    }

    public void setTickDisabled(int value) {
        this.entityData.set(tickDisabled, value);
    }
}
