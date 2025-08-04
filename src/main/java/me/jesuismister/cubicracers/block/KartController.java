package me.jesuismister.cubicracers.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;

import javax.annotation.Nullable;

public class KartController extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public KartController(Properties p_55657_) {
        super(p_55657_);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite()).setValue(LIT, Boolean.valueOf(blockPlaceContext.getLevel().hasNeighborSignal(blockPlaceContext.getClickedPos())));
        //return this.defaultBlockState().setValue(LIT, Boolean.valueOf(p_55659_.getLevel().hasNeighborSignal(p_55659_.getClickedPos())));
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        super.stepOn(level, blockPos, blockState, entity);
    }

    @Override
    protected void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block p_60512_, @org.jetbrains.annotations.Nullable Orientation p_365159_, boolean p_60514_) {
        if (!level.isClientSide) {
            boolean flag = blockState.getValue(LIT);
            if (flag != level.hasNeighborSignal(blockPos)) {
                if (flag) {
                    level.scheduleTick(blockPos, this, 4);
                } else {
                    level.setBlock(blockPos, blockState.cycle(LIT), 2);
                }
            }
        }
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (blockState.getValue(LIT) && !serverLevel.hasNeighborSignal(blockPos)) {
            serverLevel.setBlock(blockPos, blockState.cycle(LIT), 2);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockBlockStateBuilder) {
        blockBlockStateBuilder.add(LIT);
        blockBlockStateBuilder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return super.rotate(state, level, pos, direction);
    }
}
