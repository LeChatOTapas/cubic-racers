package me.jesuismister.cubicracers.sounds;

import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SoundEngineMax extends SoundLoopKart {

    private float volumeToReach;

    public SoundEngineMax(Kart kart, SoundEvent event, SoundSource category) {
        super(kart, event, category);
        volumeToReach = volume;
        volume = volume / 2.5F;
    }

    @Override
    public void tick() {
        if (volume < volumeToReach) {
            volume = Math.min(volume + volumeToReach / 2.5F, volumeToReach);
        }

        this.volume = Math.max(0.0F, default_volume * Math.min(0.9f, Math.abs(kart.getSpeed()/kart.MAX_SPEED)));

        super.tick();
    }

    @Override
    public boolean shouldStopSound() {
        if (!kart.isOnRoadBlock() || kart.getDeltaOn() || kart.getIsInvinsible() || kart.getSpeed() == 0) {
            return true;
        }
        return false;
    }
}