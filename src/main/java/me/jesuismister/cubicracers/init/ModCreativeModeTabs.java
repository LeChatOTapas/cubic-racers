package me.jesuismister.cubicracers.init;

import me.jesuismister.cubicracers.CubicRacers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = CubicRacers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB, CubicRacers.MODID);

    public static RegistryObject<CreativeModeTab> CUBIC_RACERS_TAB = CREATIVE_MODE_TABS.register("cubic_racers", () ->
            CreativeModeTab.builder().icon(() -> new ItemStack(ItemInit.KARTS_SPAWN_ITEM.get(0).get()))
                    .title(Component.translatable("creativemodetab.cubicracers_tab")).build());
}
