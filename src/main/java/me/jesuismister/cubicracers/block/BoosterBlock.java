package me.jesuismister.cubicracers.block;

import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BoosterBlock extends GlazedTerracottaBlock {

    public BoosterBlock(BlockBehaviour.Properties p_55926_) {
        super(p_55926_);
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        if (entity instanceof Kart kart) {
            if (kart.getFirstPassenger() == null) return;

            //RETOURNER LE KART A 180 SI IL VA A CONTRE SENS DU BOOSTER
            if (blockState.getValue(FACING).equals(kart.getDirection())) {
                if (blockState.getValue(FACING).getOpposite().equals(Direction.NORTH)) {
                    kart.setYRot(180);
                } else if (blockState.getValue(FACING).getOpposite().equals(Direction.WEST)) {
                    kart.setYRot(90);
                } else if (blockState.getValue(FACING).getOpposite().equals(Direction.EAST)) {
                    kart.setYRot(-90);
                } else if (blockState.getValue(FACING).getOpposite().equals(Direction.SOUTH)) {
                    kart.setYRot(0);
                }
            }
            //APPLIQUE LE BOOST
            kart.setTimeBoost(5.f);
            kart.setSpeed(kart.MAX_SPEED + kart.BOOST);
        }

        super.stepOn(level, blockPos, blockState, entity);
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return super.rotate(state, level, pos, direction);
    }
}
