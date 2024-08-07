package me.jesuismister.cubicracers.config;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.KartData;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoadBlockConfig {
    public static ForgeConfigSpec.BooleanValue ROAD_BLOCK_REQUIRE;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ROAD_BLOCKS;

    public static void registerServerConfig(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Does kart require to be on Road Block to drive at full speed").push("road_block_require");
        ROAD_BLOCK_REQUIRE = SERVER_BUILDER.define("enable", true);

        List<String> list = Arrays.asList("cubicracers:road_block", "cubicracers:road_block_dirt", "cubicracers:road_block_sand", "cubicracers:road_block_snow", "minecraft:water");
        ROAD_BLOCKS = SERVER_BUILDER
                .comment("The list of blocks considered as road blocks:")
                .defineList("road_blocks_list", list, entry -> true);

        SERVER_BUILDER.pop();
    }
}
