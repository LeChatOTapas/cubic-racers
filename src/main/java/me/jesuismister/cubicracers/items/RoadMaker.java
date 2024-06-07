package me.jesuismister.cubicracers.items;

import me.jesuismister.cubicracers.block.HollowRoadBlock;
import me.jesuismister.cubicracers.entity.custom.ItemBox;
import me.jesuismister.cubicracers.init.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.property.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class RoadMaker extends Item {
    private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);

    public RoadMaker(Properties p_41383_) {
        super(p_41383_);
    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        HitResult hitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        // SI LE TARGET DU CLIQUE DU JOUEUR EST PAS VALIDE, ON PASSE
        if (hitresult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
        }
        // SINON ON CONTINUE
        else {
            // SI LE TARGET DU CLIQUE DU JOUEUR EST UN BLOCK
            if (hitresult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult) hitresult;
                BlockPos blockPos = blockHitResult.getBlockPos();
                if(!(level.getBlockState(blockPos).getBlock() instanceof LiquidBlock))
                    blockPos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());

                // COTES SERVEUR
                if (!level.isClientSide) {
                    BlockState blockState = BlockInit.HOLLOW_ROAD_BLOCK.get().defaultBlockState();
                    if (level.getBlockState(blockPos).canBeReplaced()) {
                        if(level.getBlockState(blockPos).getBlock().equals(Blocks.WATER))
                            blockState = blockState.setValue(HollowRoadBlock.WATERLOGGED, true);
                        level.setBlock(blockPos, blockState, 3);
                        level.gameEvent(player, GameEvent.BLOCK_PLACE, blockPos);
                    }
                }
                player.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
            }
            // SINON ON ARRETE
            else {
                return InteractionResultHolder.pass(itemstack);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        p_41423_.add(Component.literal("§8§oPermits placing invisible road blocks\nwhich allow full-speed kart driving§r"));
    }
}
