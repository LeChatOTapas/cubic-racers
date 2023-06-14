package me.jesuismister.cubicracers.init;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.KartData;
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

    //ITEMS
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item",
            () -> new Item(new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> EXAMPLE_SWORD = ITEMS.register("bb_example_sword",
            () -> new SwordItem(Tiers.DIAMOND, 10, 5, new Item.Properties().stacksTo(1)));

    //ITEMS BLOCK
    public static final RegistryObject<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block",
            () -> new BlockItem(BlockInit.EXAMPLE_BLOCK.get(), new Item.Properties()));

    //ITEMS SPAWN KART
    public static final List<RegistryObject<Item>> KARTS_SPAWN_ITEM = new ArrayList<>();

    public static void initSpawnKartItem(){
        for(KartData d : KartInit.KARTS_DATA){
            System.out.println("NAME = " + d.name);
            KARTS_SPAWN_ITEM.add(ITEMS.register(d.name, () -> new KartItem(new Item.Properties(), d.name)));
        }
    }
}
