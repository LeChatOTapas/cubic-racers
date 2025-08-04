package me.jesuismister.cubicracers.init;

import me.jesuismister.cubicracers.CubicRacers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB, CubicRacers.MODID);

    public static Supplier<CreativeModeTab> CUBIC_RACERS_TAB = CREATIVE_MODE_TABS.register("cubic_racers", () ->
            CreativeModeTab.builder().icon(() -> new ItemStack(ItemInit.KARTS_SPAWN_ITEM.getFirst().get()))
                    .title(Component.translatable("creativemodetab.cubicracers_tab")).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
