package me.jesuismister.mckart.init;

import me.jesuismister.mckart.MCKart;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MCKart.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeModeTabs {
    public static CreativeModeTab MCKART_TAB;

    @SubscribeEvent
    public static void registerCreativeModeTabs(CreativeModeTabEvent.Register event){
        MCKART_TAB = event.registerCreativeModeTab(new ResourceLocation(MCKart.MODID, "mckart_tab"),
                builder -> builder.icon(() -> new ItemStack(ItemInit.EXAMPLE_ITEM.get()))
                        .title(Component.translatable("creativemodetab.mckart_tab")));
    }
}
