package me.jesuismister.cubicracers.config;

import me.jesuismister.cubicracers.CubicRacers;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = CubicRacers.MODID)
public class Config {
    public static IConfigSpec SERVER;

    public static void register() {
        registerServerConfigs();
    }

    private static void registerServerConfigs() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        RoadBlockConfig.registerServerConfig(builder);
        KartItemConfig.registerServerConfig(builder);
        KartConfig.registerServerConfig(builder);
        SERVER = builder.build();
    }
}