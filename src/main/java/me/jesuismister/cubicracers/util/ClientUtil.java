package me.jesuismister.cubicracers.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class ClientUtil {

    public static void spawnParticle(Player player, ParticleOptions particleIn, boolean longDistanceIn, float xIn, float yIn, float zIn, float xOffsetIn, float yOffsetIn, float zOffsetIn, float speedIn, int countIn) {
        if (player instanceof ServerPlayer) {
            ((ServerPlayer) player).connection.send(new ClientboundLevelParticlesPacket(particleIn, longDistanceIn, xIn, yIn, zIn, xOffsetIn, yOffsetIn, zOffsetIn, speedIn, countIn));
        }
    }

    public static void spawnParticleForAll(Level level, double range, ParticleOptions particleIn, boolean longDistanceIn, double xIn, double yIn, double zIn, float xOffsetIn, float yOffsetIn, float zOffsetIn, float speedIn, int countIn) {
        AABB a = new AABB(new BlockPos((int) (xIn - range), (int) (yIn - range), (int) (zIn - range)), new BlockPos((int) (xIn + range), (int) (yIn + range), (int) (zIn + range)));
        for (Player players : level.getEntitiesOfClass(Player.class, a)) {
            spawnParticle(players, particleIn, longDistanceIn, (float) xIn, (float) yIn, (float) zIn, xOffsetIn, yOffsetIn, zOffsetIn, speedIn, countIn);
        }
    }

    public static void playSound(Player player, double x, double y, double z, SoundEvent sound, SoundSource soundSource, float volume, float pitch) {
        if (player instanceof ServerPlayer) {
            ((ServerPlayer) player).connection.send(new ClientboundSoundPacket(Holder.direct(sound), soundSource, x, y, z, volume, pitch, player.getRandom().nextLong()));
        }
    }

    public static void playSoundToAll(Level world, double x, double y, double z, double range, SoundEvent sound, SoundSource category, float volume, float pitch) {
        AABB a = new AABB(BlockPos.containing(x - range, y - range, z - range), BlockPos.containing(x + range, y + range, z + range));
        for (Player player : world.getEntitiesOfClass(Player.class, a)) {
            playSound(player, x, y, z, sound, category, calculateVolume(calculateDistance(x,y,z,player), range), pitch);
        }
    }

    public static double calculateDistance(double x1, double y1, double z1, Player player) {
        return Math.sqrt(Math.pow(player.getX() - x1, 2) + Math.pow(player.getY() - y1, 2) + Math.pow(player.getZ() - z1, 2));
    }

    public static float calculateVolume(double distance, double range) {
        if (distance >= range) {
            return 0;
        } else {
            return (float) (1 - (distance / range));
        }
    }
}
