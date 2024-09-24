package me.jesuismister.cubicracers.block;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BouncingMushroomBlock extends SlimeBlock {
    public static int TIME_BOUNCING = 10;

    public BouncingMushroomBlock(Properties p_56402_) {
        super(p_56402_);
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        if (entity instanceof TestKart kart) {
            kart.setBouncingTime(TIME_BOUNCING);
        }
    }
}
