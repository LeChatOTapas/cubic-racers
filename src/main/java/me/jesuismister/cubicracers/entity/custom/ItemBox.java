package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.config.KartItemConfig;
import me.jesuismister.cubicracers.init.ItemInit;
import me.jesuismister.cubicracers.init.KartItemsInit;
import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.util.ClientRandom;
import me.jesuismister.cubicracers.util.ClientUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Map;

public class ItemBox extends ItemKartAbstract implements GeoEntity {
    public static final float HITBOX_X = 1f;
    public static final float HITBOX_Y = 2f;

    private static final int TICK_TO_GET_BACK_ITEM = 20 * 4; //4s

    public static final EntityDataAccessor<Boolean> hasItem = SynchedEntityData.defineId(ItemBox.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> tickDisabled = SynchedEntityData.defineId(ItemBox.class, EntityDataSerializers.INT);

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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        this.entityData.set(hasItem, true);
        this.entityData.set(tickDisabled, 0);
    }
    @Override
    public void tick() {
        super.tick();

        //SI IL Y A UN ITEM DE DISPO DANS LE CUBE
        if (getHasItem()) {
            applyCollision();
        } else {
            if (getTickDisabled() > TICK_TO_GET_BACK_ITEM) setHasItem(true);
            setTickDisabled(getTickDisabled() + 1);
        }
    }

    @Override
    protected void checkEndOfLife() {
    }

    @Override
    protected void applyCollision() {
        //RECUPERER TOUTES LES ENTITES PROCHES DU CUBE
        List<Entity> nearbyEntities = level().getEntities(this, getBoundingBox().inflate(0.5f));

        //PARCOURIR LA LISTE DES ENTITES PROCHES
        for (Entity entity : nearbyEntities) {
            //ON CHECK QUE LES ENTITES "KART"
            if (entity instanceof TestKart kart) {
                if (!level().isClientSide()) {
                    giveRandomItem(kart);
                    ClientUtil.playSoundToAll(level(), getX(), getY(), getZ(), 8, SoundsInit.ITEM_BOX_CONSUME.get(), SoundSource.RECORDS, 1f, 0.95f);
                }
                setHasItem(false);
                setTickDisabled(0);
                break;
            }
        }
    }

    @Override
    protected int getMaxTimeAlive() {
        return -1;
    }

    public boolean giveRandomItem(TestKart kart) {
        if (kart.getFirstPassenger() == null || !(kart.getFirstPassenger() instanceof Player))
            return false;

        if (kart.getKartItem().equals("None")) {
            kart.setKartItem(getRandomItem());
        }
        return true;
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ItemInit.ITEM_BOX_SPAWN_ITEM.get());
    }

    @Override
    public boolean hurtClient(DamageSource damageSource) {
        if (damageSource.getEntity() instanceof Player player) {
            if (this.getFirstPassenger() == null) {
                remove(RemovalReason.KILLED);
                if (!player.isCreative() && !level().isClientSide()) {
                    Item spawn_item = ItemInit.ITEM_BOX_SPAWN_ITEM.get();
                    player.level().addFreshEntity(new ItemEntity(player.level(), getX(), getY(), getZ(), new ItemStack(spawn_item)));
                }
                return true;
            }
        }
        return false;
    }

    private String getRandomItem() {
        //Determination de la borne maximal (car pas forcement 100)
        int max = 0;
        for (ModConfigSpec.DoubleValue v : KartItemConfig.ITEMS_DROP_RATES.values()) {
            max += v.get();
        }

        int rand = ClientRandom.nextInt(max);
        int temp = 0;
        for (Map.Entry<String, ModConfigSpec.DoubleValue> v : KartItemConfig.ITEMS_DROP_RATES.entrySet()) {
            temp += v.getValue().get();
            if (rand <= temp) {
                return v.getKey();
            }
        }
        return "None";
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

    //////////////
    // GECKOLIB //
    //////////////
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation BOX_ON = RawAnimation.begin().thenLoop("box_on");
    protected static final RawAnimation BOX_OFF = RawAnimation.begin().thenLoop("box_off");

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationTest<GeoAnimatable> geoAnimatableAnimationTest) {
        if (getHasItem()) {
            return geoAnimatableAnimationTest.setAndContinue(BOX_ON);
        } else {
            return geoAnimatableAnimationTest.setAndContinue(BOX_OFF);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
