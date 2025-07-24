package me.jesuismister.cubicracers.block;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import me.jesuismister.cubicracers.init.SoundsInit;
import me.jesuismister.cubicracers.util.ClientUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static java.lang.Math.min;

public class BouncingMushroomBlock extends SlimeBlock {

    public BouncingMushroomBlock(Properties p_56402_) {
        super(p_56402_);
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        if (entity instanceof TestKart kart) {
            Vec3 velocity = new Vec3(kart.getDeltaMovement().x, 2, kart.getDeltaMovement().z);
            kart.setDeltaMovement(velocity);
            ClientUtil.playSoundToAll(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 8, SoundsInit.KART_BOUNCING.get(), SoundSource.RECORDS, 1f, 0.95f);
        }
    }
}
