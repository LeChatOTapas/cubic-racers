package me.jesuismister.cubicracers.sounds;

import me.jesuismister.cubicracers.entity.custom.GreenShell;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SoundGreenShellMoving extends SoundLoopKart {

    private float volumeToReach;

    public SoundGreenShellMoving(GreenShell greenShell, SoundEvent event, SoundSource category) {
        super(greenShell, event, category);
        volumeToReach = volume;
        volume = volume / 2.5F;
    }

    @Override
    public void tick() {
        volume = 1f;

        super.tick();
    }

    @Override
    public boolean shouldStopSound() {
        GreenShell greenShell = (GreenShell) entity;
        if (!greenShell.isAlive()) {
            return true;
        }
        return false;
    }


}