package me.jesuismister.cubicracers.entity.custom;

import me.jesuismister.cubicracers.config.KartItemConfig;
import me.jesuismister.cubicracers.init.ItemInit;
import me.jesuismister.cubicracers.init.KartItemsInit;
import me.jesuismister.cubicracers.network.Network;
import me.jesuismister.cubicracers.network.message.ItemToClientMessage;
import me.jesuismister.cubicracers.util.ClientRandom;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Map;

import static me.jesuismister.cubicracers.util.ClientUtil.spawnParticleForAll;

public class ItemBox extends ItemKartAbstract implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final String TEXTURE = "textures/entity/item_box.png";
    public static final String MODEL = "geo/item_box.geo.json";
    public static final String ANIMATION = "animations/item_box.animation.json";
    public static final float HITBOX_X = 1f;
    public static final float HITBOX_Y = 2f;

    private static final double BANANA_DROP_RATE = 20;
    private static final double GREEN_SHELL_DROP_RATE = 50;
    private static final double MUSHROOM_DROP_RATE = 65;
    private static final double FAKE_BOX_DROP_RATE = 75;
    private static final double BOMB_OMB_DROP_RATE = 85;
    private static final double STAR_DROP_RATE = 90;
    private static final double THUNDER_DROP_RATE = 95;
    private static final double KLAXON_DROP_RATE = 100;

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
        this.entityData.define(hasItem, true);
        this.entityData.define(tickDisabled, 0);
    }

    @Override
    public void tick() {
        super.tick();
        BlockState state = Blocks.GLASS.defaultBlockState();
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
                        spawnParticleForAll(this.level(), 20, new BlockParticleOption(ParticleTypes.BLOCK, state), true, this.getX() , this.getY() + 2,  this.getZ() , 0.6f, 0f, 0.6f, 0.8f, 40);
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
        if (kart.getFirstPassenger()== null || !(kart.getFirstPassenger() instanceof Player))
            return false;

        if(kart.getKartItem().equals("None")){
            kart.setKartItem(getRandomItem());
        }
        ServerPlayer player = (ServerPlayer) kart.getFirstPassenger();
        Network.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ItemToClientMessage(kart.getKartItem()));

        return true;
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ItemInit.ITEM_BOX_SPAWN_ITEM.get());
    }

    @Override
    /**
     * Méthode qui fait en sorte de détruire le cube quand il prend des dégats
     */
    public boolean hurt(DamageSource damage, float p_19947_) {
        if (damage.getEntity() instanceof Player player) {
            if (this.getFirstPassenger() == null) {
                remove(RemovalReason.KILLED);
                if(!player.isCreative() && !level().isClientSide()){
                    Item spawn_item = ItemInit.ITEM_BOX_SPAWN_ITEM.get();
                    player.level().addFreshEntity(new ItemEntity(player.level(), getX(), getY(), getZ(), new ItemStack(spawn_item)));
                }
                return true;
            }
        }
        return false;
    }

    private String getRandomItem(){
        //Determination de la borne maximal (car pas forcement 100)
        int max = 0;
        for(ForgeConfigSpec.DoubleValue v : KartItemConfig.ITEMS_DROP_RATES.values()){
            max += v.get();
        }

        int rand = ClientRandom.nextInt(max);
        int temp = 0;
        for(Map.Entry<String, ForgeConfigSpec.DoubleValue> v : KartItemConfig.ITEMS_DROP_RATES.entrySet()){
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
}
