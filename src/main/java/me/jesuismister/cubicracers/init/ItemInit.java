package me.jesuismister.cubicracers.init;


import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.items.ItemBoxSpawnItem;
import me.jesuismister.cubicracers.entity.KartData;
import me.jesuismister.cubicracers.items.KartSpawnItem;
import me.jesuismister.cubicracers.items.RoadMaker;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public class ItemInit {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CubicRacers.MODID);

    //ITEMS SPAWN KART
    public static final List<DeferredItem<Item>> KARTS_SPAWN_ITEM = new ArrayList<>();

    public static void initSpawnKartItem() {
        for (KartData d : CubicRacers.KARTS_DATA) {
            KARTS_SPAWN_ITEM.add(ITEMS.register(d.name, () -> new KartSpawnItem(new Item.Properties().stacksTo(1), d.name, d.creatorName)));
        }
    }

    //ITEM SPAWN ITEM BOX
    public static final DeferredItem<Item> ITEM_BOX_SPAWN_ITEM = ITEMS.register(
            "item_box_spawn_item", () -> new ItemBoxSpawnItem(new Item.Properties()));

    //ITEM SPAWN ITEM BOX
    public static final DeferredItem<Item> ROAD_MAKER = ITEMS.register(
            "road_maker", () -> new RoadMaker(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
