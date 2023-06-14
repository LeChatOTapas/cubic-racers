package me.jesuismister.cubicracers.init;

import me.jesuismister.cubicracers.CubicRacers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CubicRacers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeModeTabs {
    public static CreativeModeTab CUBIC_RACERS_TAB;

    @SubscribeEvent
    public static void registerCreativeModeTabs(CreativeModeTabEvent.Register event) {
        CUBIC_RACERS_TAB = event.registerCreativeModeTab(new ResourceLocation(CubicRacers.MODID, "cubicracers_tab"),
                builder -> builder.icon(() -> new ItemStack(ItemInit.EXAMPLE_ITEM.get()))
                        .title(Component.translatable("creativemodetab.cubicracers_tab")));
    }
}
