package me.jesuismister.cubicracers.config;

import de.maxhenkel.corelib.CommonRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {
    public static ClientConfig CLIENT_CONFIG;

    public static void register() {
        registerServerConfigs();
        //registerCommonConfigs();
        registerClientConfigs();
    }

    private static void registerServerConfigs() {
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
        RoadBlockConfig.registerServerConfig(CLIENT_BUILDER);
        KartItemConfig.registerServerConfig(CLIENT_BUILDER);
        KartConfig.registerServerConfig(CLIENT_BUILDER);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CLIENT_BUILDER.build());
    }

    /*
    private static void registerCommonConfigs() {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_BUILDER.build());
    }*/

    private static void registerClientConfigs() {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
        CLIENT_CONFIG = CommonRegistry.registerConfig(ModConfig.Type.CLIENT, ClientConfig.class);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SERVER_BUILDER.build());
    }

}