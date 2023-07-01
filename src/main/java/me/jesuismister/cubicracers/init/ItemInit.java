package me.jesuismister.cubicracers.init;


import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.ItemBoxSpawnItem;
import me.jesuismister.cubicracers.entity.KartData;
import me.jesuismister.cubicracers.entity.KartSpawnItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CubicRacers.MODID);

    //ITEMS SPAWN KART
    public static final List<RegistryObject<Item>> KARTS_SPAWN_ITEM = new ArrayList<>();

    public static void initSpawnKartItem() {
        for (KartData d : KartInit.KARTS_DATA) {
            KARTS_SPAWN_ITEM.add(ITEMS.register(d.name, () -> new KartSpawnItem(new Item.Properties(), d.name)));
        }
    }

    //ITEM SPAWN ITEM BOX
    public static final RegistryObject<Item> ITEM_BOX_SPAWN_ITEM = ITEMS.register(
            "item_box_spawn_item", () -> new ItemBoxSpawnItem(new Item.Properties()));
}
