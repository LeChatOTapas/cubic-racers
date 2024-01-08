package me.jesuismister.cubicracers.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerPlayer;
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
}
