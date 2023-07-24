package me.jesuismister.cubicracers.sounds;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.KartAbstract;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public abstract class SoundLoopKart extends AbstractTickableSoundInstance {

    protected KartAbstract kart;

    public SoundLoopKart(KartAbstract kart, SoundEvent event, SoundSource category) {
        super(event, category, SoundInstance.createUnseededRandom());
        this.kart = kart;
        this.looping = true;
        this.delay = 0;
        this.volume = CubicRacers.CLIENT_CONFIG.kartVolume.get().floatValue();
        this.pitch = 1F;
        this.relative = false;
        this.attenuation = Attenuation.LINEAR;
        this.updatePos();
    }

    public void updatePos() {
        this.x = (float) kart.getX();
        this.y = (float) kart.getY();
        this.z = (float) kart.getZ();
    }

    @Override
    public void tick() {
        if (isStopped()) {
            return;
        }

        if (!kart.isAlive()) {
            setDonePlaying();
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.isAlive()) {
            setDonePlaying();
            return;
        }

        if (shouldStopSound()) {
            setDonePlaying();
            return;
        }

        updatePos();
    }

    public void setDonePlaying() {
        stop();
    }

    public abstract boolean shouldStopSound();
}
