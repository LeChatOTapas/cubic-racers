package me.jesuismister.cubicracers.sounds;

import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SoundStarMode extends SoundLoopKart {

    private float volumeToReach;

    public SoundStarMode(TestKart kart, SoundEvent event, SoundSource category) {
        super(kart, event, category);
        volumeToReach = volume;
        volume = volume / 2.5F;
    }

    @Override
    public void tick() {
        volume = 1.5f;

        super.tick();
    }

    @Override
    public boolean shouldStopSound() {
        TestKart kart = (TestKart) entity;
        if (!kart.isInvincible()) {
            return true;
        }
        return false;
    }


}