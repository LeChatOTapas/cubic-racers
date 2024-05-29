package me.jesuismister.cubicracers.config;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.KartData;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class RoadBlockConfig {
    public static ForgeConfigSpec.BooleanValue ROAD_BLOCK_REQUIRE;

    public static void registerServerConfig(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Does kart require to be on Road Block to drive at full speed").push("road_block_require");
        ROAD_BLOCK_REQUIRE = SERVER_BUILDER.define("enable", true);
        SERVER_BUILDER.pop();
    }
}
