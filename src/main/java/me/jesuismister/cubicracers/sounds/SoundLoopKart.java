package me.jesuismister.cubicracers.sounds;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public abstract class SoundLoopKart extends AbstractTickableSoundInstance {
    private Vec3 prevPlayerPos;
    private Vec3 prevSoundPos;
    protected Kart kart;
    public float default_volume;

    public SoundLoopKart(Kart kart, SoundEvent event, SoundSource category) {
        super(event, category, SoundInstance.createUnseededRandom());
        this.kart = kart;
        this.looping = true;
        this.delay = 0;
        this.default_volume = CubicRacers.CLIENT_CONFIG.kartVolume.get().floatValue();
        this.volume = CubicRacers.CLIENT_CONFIG.kartVolume.get().floatValue();
        this.pitch = 1F;
        this.relative = true;
        this.attenuation = Attenuation.LINEAR;
        this.updatePos();
    }

    public void updatePos() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            prevPlayerPos = player.position();
        }

        prevSoundPos = new Vec3(x, y, z);
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

        // Calculate the distance between the player and the sound source
        double distance = player.position().distanceTo(new Vec3(x, y, z));

        // Adjust the volume based on the distance (you can use any attenuation formula you like)
        this.volume = Math.max(0.0F, volume - (float) (distance * 0.025));

        updatePos();
    }

    public void setDonePlaying() {
        stop();
    }

    public abstract boolean shouldStopSound();
}
