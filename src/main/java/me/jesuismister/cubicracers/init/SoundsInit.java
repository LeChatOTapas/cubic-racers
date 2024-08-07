package me.jesuismister.cubicracers.init;

import me.jesuismister.cubicracers.CubicRacers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundsInit {
    public static final DeferredRegister<SoundEvent> SOUND_REGISTER = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CubicRacers.MODID);

    public static RegistryObject<SoundEvent> ENGINE_IDLE = addSound("engine_idle");
    public static RegistryObject<SoundEvent> ENGINE_MAX = addSound("engine_max");
    public static RegistryObject<SoundEvent> STAR_MODE = addSound("kart_star_mode");
    public static RegistryObject<SoundEvent> KART_GLIDING = addSound("kart_gliding");
    public static RegistryObject<SoundEvent> KART_DRIFTING = addSound("kart_drifting");
    public static RegistryObject<SoundEvent> KART_OFF_ROAD = addSound("kart_off_road");
    public static RegistryObject<SoundEvent> KART_SPEED_BOOST = addSound("kart_speed_boost");
    public static RegistryObject<SoundEvent> KART_BOUNCING = addSound("kart_bouncing");
    public static RegistryObject<SoundEvent> BANANA_HIT_KART = addSound("banana_hit_kart");
    public static RegistryObject<SoundEvent> BOB_OMB_EXPLOSION = addSound("bob_omb_explosion");
    public static RegistryObject<SoundEvent> GREEN_SHELL_MOVING = addSound("green_shell_moving");
    public static RegistryObject<SoundEvent> GREEN_SHELL_HIT_KART = addSound("green_shell_hit_kart");
    public static RegistryObject<SoundEvent> SPAWN_ITEM_BELOW = addSound("spawn_item_below");
    public static RegistryObject<SoundEvent> THROWING_ITEM = addSound("throwing_item");
    public static RegistryObject<SoundEvent> ITEM_BOX_CONSUME = addSound("item_box_consume");

    public static RegistryObject<SoundEvent> KLAXON = addSound("klaxon");

    public static RegistryObject<SoundEvent> addSound(String soundName) {
        return SOUND_REGISTER.register(soundName, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CubicRacers.MODID, soundName)));
    }

    public static void playSound(SoundEvent evt, Level world, BlockPos pos, Player entity, SoundSource category, float volume) {
        playSound(evt, world, pos, entity, category, volume, 0.5F);
    }

    public static void playSound(SoundEvent evt, Level world, BlockPos pos, Player entity, SoundSource category, float volume, float pitch) {
        if (entity != null) {
            world.playSound(entity, pos, evt, category, volume, pitch);
        } else {
            if (!world.isClientSide) {
                world.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, evt, category, volume, pitch);
            }
        }
    }

    public static void playSound(SoundEvent evt, Level world, BlockPos pos, Player entity, SoundSource category) {
        playSound(evt, world, pos, entity, category, 0.15F);
    }

    @OnlyIn(Dist.CLIENT)
    public static void playSoundLoop(AbstractTickableSoundInstance loop, Level world) {
        if (world.isClientSide) {
            Minecraft.getInstance().getSoundManager().play(loop);
        }
    }
}
