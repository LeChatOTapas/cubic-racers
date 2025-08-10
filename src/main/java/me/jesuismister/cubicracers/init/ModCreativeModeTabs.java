package me.jesuismister.cubicracers.init;

import me.jesuismister.cubicracers.CubicRacers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB, CubicRacers.MODID);

    public static Supplier<CreativeModeTab> CUBIC_RACERS_TAB = CREATIVE_MODE_TABS.register("cubic_racers",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ItemInit.KARTS_SPAWN_ITEM.getFirst().get()))
                    .title(Component.translatable("creativemodetab.cubicracers_tab"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(ItemInit.ROAD_MAKER);
                        output.accept(ItemInit.ITEM_BOX_SPAWN_ITEM);

                        output.accept(BlockInit.BOOSTER);
                        output.accept(BlockInit.GLIDE_TRIGGER_BLOCK);
                        output.accept(BlockInit.KART_CONTROLLER);

                        output.accept(BlockInit.STARTING_BLOCK);
                        output.accept(BlockInit.ROAD_BLOCK);
                        output.accept(BlockInit.ROAD_BLOCK_DIRT);
                        output.accept(BlockInit.ROAD_BLOCK_SAND);
                        output.accept(BlockInit.ROAD_BLOCK_SNOW);

                        output.accept(BlockInit.RED_BOUNCING_MUSHROOM_BLOCK);

                        for (Supplier<Item> r : ItemInit.KARTS_SPAWN_ITEM) {
                            output.accept(r.get());
                        }
                    }))
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
