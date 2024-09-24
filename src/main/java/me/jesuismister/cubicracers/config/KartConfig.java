package me.jesuismister.cubicracers.config;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.KartData;
import me.jesuismister.cubicracers.init.KartInit;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class KartConfig {
    public static List<ForgeConfigSpec.DoubleValue> MAX_SPEED = new ArrayList<>();
    public static List<ForgeConfigSpec.DoubleValue> ACCELERATION_BOOST = new ArrayList<>();
    public static List<ForgeConfigSpec.DoubleValue> BOOST = new ArrayList<>();
    public static List<ForgeConfigSpec.DoubleValue> HANDLING = new ArrayList<>();

    public static void registerServerConfig(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Settings of all the karts").push("karts_settings");
        for(KartData d : CubicRacers.KARTS_DATA){
            SERVER_BUILDER.comment("Settings for the " + d.name).push(d.name);

            MAX_SPEED.add(SERVER_BUILDER
                    .comment("The maximum speed the kart can reach")
                    .defineInRange("max_speed", 1.0, 0.1, 10.0));
            ACCELERATION_BOOST.add(SERVER_BUILDER
                    .comment("The acceleration rate of the kart")
                    .defineInRange("acceleration_boost", 0.025, 0.01, 0.1));
            BOOST.add(SERVER_BUILDER
                    .comment("The boost the kart receives")
                    .defineInRange("boost", 0.5, 0.1, 10.0));
            HANDLING.add(SERVER_BUILDER
                    .comment("The handling rate of the kart")
                    .defineInRange("handling", 3.5, 1.0, 5.0));
            SERVER_BUILDER.pop();
        }
        SERVER_BUILDER.pop();
    }
}