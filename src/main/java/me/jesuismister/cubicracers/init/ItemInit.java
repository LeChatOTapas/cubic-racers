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
            KARTS_SPAWN_ITEM.add(
                    ITEMS.registerItem(
                            d.name,                                  // nom de l'item
                            props -> new KartSpawnItem(              // lambda qui reçoit les Properties
                                    props.stacksTo(1),                     // tes properties
                                    d.name,                                // passage du premier String
                                    d.creatorName                          // passage du second String
                            )
                    )
            );
        }
    }

    //ITEM SPAWN ITEM BOX
    public static final DeferredItem<Item> ITEM_BOX_SPAWN_ITEM = ITEMS.registerItem("item_box_spawn_item",
            ItemBoxSpawnItem::new, new Item.Properties());

    //ITEM SPAWN ITEM BOX
    public static final DeferredItem<Item> ROAD_MAKER = ITEMS.registerItem("road_maker",
            RoadMaker::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
