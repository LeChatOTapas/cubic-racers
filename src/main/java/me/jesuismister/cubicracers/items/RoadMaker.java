package me.jesuismister.cubicracers.items;

import me.jesuismister.cubicracers.block.HollowRoadBlock;
import me.jesuismister.cubicracers.init.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class RoadMaker extends Item {
    private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);

    public RoadMaker(Properties p_41383_) {
        super(p_41383_);
    }

    public @NotNull InteractionResult use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        HitResult hitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        // SI LE TARGET DU CLIQUE DU JOUEUR EST PAS VALIDE, ON PASSE
        if (hitresult.getType() == HitResult.Type.MISS) {
            return InteractionResult.PASS;
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
                return InteractionResult.SUCCESS;
            }
            // SINON ON ARRETE
            else {
                return InteractionResult.PASS;
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, TooltipContext p_339594_, TooltipDisplay p_399753_, Consumer<Component> p_399884_, TooltipFlag p_41424_) {
        // tooltipComponents.add(Component.literal("§8§oPermits placing invisible road blocks\nwhich allow full-speed kart driving§r"));
    }
}
