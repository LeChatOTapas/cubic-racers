package me.jesuismister.cubicracers.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KartItemConfig {
    public static Map<String, ModConfigSpec.DoubleValue> ITEMS_DROP_RATES = new HashMap<>();

    public static void registerServerConfig(ModConfigSpec.Builder SERVER_BUILDER) {
        Map<String, Float> itemList = Map.of(
                "Banana", 20f,
                "Green_shell", 30f,
                "Mushroom", 20f,
                "Fake_box", 10f,
                "Bob_omb", 8f,
                "Star", 3f,
                "Thunder", 1f,
                "Klaxon", 8f
        );

        SERVER_BUILDER.comment("Settings for all the drop rates from the item box").push("item_box_drop_rates");
        for(Map.Entry<String, Float> item : itemList.entrySet()){
            SERVER_BUILDER.push(item.getKey());
            ITEMS_DROP_RATES.put(item.getKey(), SERVER_BUILDER
                    .comment("Drop rate of " + item.getKey())
                    .defineInRange("value", item.getValue(), 0, 100));
            SERVER_BUILDER.pop();
        }
        SERVER_BUILDER.pop();
    }
}
