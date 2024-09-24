package me.jesuismister.cubicracers.sounds;

import me.jesuismister.cubicracers.entity.custom.TestKartAbstract;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SoundKartDrifting extends SoundLoopKart {

    private float volumeToReach;

    public SoundKartDrifting(TestKartAbstract kart, SoundEvent event, SoundSource category) {
        super(kart, event, category);
        volumeToReach = volume;
        volume = volume / 2.5F;
    }

    @Override
    public void tick() {
        if (volume < volumeToReach) {
            volume = Math.min(volume + volumeToReach / 2.5F, volumeToReach*0.5f);
        }

        super.tick();
    }

    @Override
    public boolean shouldStopSound() {
        TestKartAbstract kart = (TestKartAbstract) entity;
        if (!kart.isDrifting()) {
            return true;
        }
        return false;
    }


}