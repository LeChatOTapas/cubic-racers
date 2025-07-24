package me.jesuismister.cubicracers.config;

import de.maxhenkel.corelib.CommonRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {
    public static ClientConfig CLIENT_CONFIG;

    public static void register() {
        registerServerConfigs();
        registerClientConfigs();
    }

    private static void registerServerConfigs() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        RoadBlockConfig.registerServerConfig(builder);
        KartItemConfig.registerServerConfig(builder);
        KartConfig.registerServerConfig(builder);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, builder.build());
    }
    private static void registerClientConfigs() {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
        CLIENT_CONFIG = CommonRegistry.registerConfig(ModConfig.Type.CLIENT, ClientConfig.class);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SERVER_BUILDER.build());
    }

}