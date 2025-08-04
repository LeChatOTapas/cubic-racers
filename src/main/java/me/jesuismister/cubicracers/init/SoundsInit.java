package me.jesuismister.cubicracers.init;

import me.jesuismister.cubicracers.CubicRacers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SoundsInit {
    public static final DeferredRegister<SoundEvent> SOUND_REGISTER = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, CubicRacers.MODID);

    public static Supplier<SoundEvent> ENGINE_IDLE = addSound("engine_idle");
    public static Supplier<SoundEvent> ENGINE_MAX = addSound("engine_max");
    public static Supplier<SoundEvent> STAR_MODE = addSound("kart_star_mode");
    public static Supplier<SoundEvent> KART_GLIDING = addSound("kart_gliding");
    public static Supplier<SoundEvent> KART_DRIFTING = addSound("kart_drifting");
    public static Supplier<SoundEvent> KART_OFF_ROAD = addSound("kart_off_road");
    public static Supplier<SoundEvent> KART_SPEED_BOOST = addSound("kart_speed_boost");
    public static Supplier<SoundEvent> KART_BOUNCING = addSound("kart_bouncing");
    public static Supplier<SoundEvent> BANANA_HIT_KART = addSound("banana_hit_kart");
    public static Supplier<SoundEvent> BOB_OMB_EXPLOSION = addSound("bob_omb_explosion");
    public static Supplier<SoundEvent> GREEN_SHELL_MOVING = addSound("green_shell_moving");
    public static Supplier<SoundEvent> GREEN_SHELL_HIT_KART = addSound("green_shell_hit_kart");
    public static Supplier<SoundEvent> SPAWN_ITEM_BELOW = addSound("spawn_item_below");
    public static Supplier<SoundEvent> THROWING_ITEM = addSound("throwing_item");
    public static Supplier<SoundEvent> ITEM_BOX_CONSUME = addSound("item_box_consume");

    public static Supplier<SoundEvent> KLAXON = addSound("klaxon");

    public static Supplier<SoundEvent> addSound(String soundName) {
        return SOUND_REGISTER.register(soundName, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, soundName)));
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

    public static void playSoundLoop(AbstractTickableSoundInstance loop, Level world) {
        if (world.isClientSide) {
            Minecraft.getInstance().getSoundManager().play(loop);
        }
    }

    public static void register(IEventBus eventBus) {
        SOUND_REGISTER.register(eventBus);
    }
}
