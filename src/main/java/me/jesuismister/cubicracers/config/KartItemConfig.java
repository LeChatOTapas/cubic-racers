package me.jesuismister.cubicracers.config;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.KartData;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KartItemConfig {
    public static Map<String, ForgeConfigSpec.DoubleValue> ITEMS_DROP_RATES = new HashMap<>();

    public static void registerServerConfig(ForgeConfigSpec.Builder SERVER_BUILDER) {
        List<String> itemList = List.of("Banana", "Green_shell", "Mushroom", "Fake_box", "Bob_omb", "Star", "Thunder", "Klaxon");

        SERVER_BUILDER.comment("Settings for all the drop rates from the item box").push("item_box_drop_rates");
        for(String item : itemList){
            SERVER_BUILDER.push(item);

            ITEMS_DROP_RATES.put(item, SERVER_BUILDER
                    .comment("Drop rate of " + item)
                    .defineInRange("value", 12.5, 0, 100));
            SERVER_BUILDER.pop();
        }
        SERVER_BUILDER.pop();
    }
}
