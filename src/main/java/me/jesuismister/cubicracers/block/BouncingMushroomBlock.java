package me.jesuismister.cubicracers.block;

import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BouncingMushroomBlock extends SlimeBlock {
    public static int TIME_BOUNCING = 10;

    public BouncingMushroomBlock(Properties p_56402_) {
        super(p_56402_);
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        if (entity instanceof Kart kart) {
            kart.setBouncingTime(TIME_BOUNCING);
        }
    }
}
