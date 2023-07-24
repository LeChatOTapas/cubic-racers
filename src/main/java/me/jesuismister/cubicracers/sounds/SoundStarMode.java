package me.jesuismister.cubicracers.sounds;

import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SoundStarMode extends SoundLoopKart {

    private float volumeToReach;

    public SoundStarMode(Kart kart, SoundEvent event, SoundSource category) {
        super(kart, event, category);
        volumeToReach = volume;
        volume = volume / 2.5F;
    }

    @Override
    public void tick() {
        if (volume < volumeToReach) {
            volume = Math.min(volume + volumeToReach / 2.5F, volumeToReach);
        }

        super.tick();
    }

    @Override
    public boolean shouldStopSound() {
        if (!kart.getIsInvinsible()) {
            return true;
        }
        return false;
    }


}