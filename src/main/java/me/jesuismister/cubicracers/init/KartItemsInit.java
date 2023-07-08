package me.jesuismister.cubicracers.init;

import io.netty.util.Attribute;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.Banana;
import me.jesuismister.cubicracers.entity.custom.BobOmb;
import me.jesuismister.cubicracers.entity.custom.GreenShell;
import me.jesuismister.cubicracers.entity.custom.ItemBox;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class KartItemsInit {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CubicRacers.MODID);

    //ITEM BOX
    public static final RegistryObject<EntityType<ItemBox>> ITEM_BOX =
            ENTITY_TYPES.register("item_box",
                    () -> EntityType.Builder.<ItemBox>of((type, level) -> new ItemBox(type, level, false), MobCategory.MISC)
                            .sized(ItemBox.HITBOX_X, ItemBox.HITBOX_Y)
                            .build(new ResourceLocation(CubicRacers.MODID, "item_box").toString()));

    //BANANE
    public static final RegistryObject<EntityType<Banana>> BANANA =
            ENTITY_TYPES.register("banana",
                    () -> EntityType.Builder.of(Banana::new, MobCategory.MISC)
                            .sized(Banana.HITBOX, Banana.HITBOX)
                            .build(new ResourceLocation(CubicRacers.MODID, "banana").toString()));

    //BOMB OMB
    public static final RegistryObject<EntityType<BobOmb>> BOMB_OMB =
            ENTITY_TYPES.register("bob_omb",
                    () -> EntityType.Builder.of(BobOmb::new, MobCategory.MISC)
                            .sized(BobOmb.HITBOX, BobOmb.HITBOX)
                            .build(new ResourceLocation(CubicRacers.MODID, "bob_omb").toString()));

    //GREEN SHELL
    public static final RegistryObject<EntityType<GreenShell>> GREEN_SHELL =
            ENTITY_TYPES.register("green_shell",
                    () -> EntityType.Builder.of(GreenShell::new, MobCategory.MISC)
                            .sized(GreenShell.HITBOX, Banana.HITBOX)
                            .build(new ResourceLocation(CubicRacers.MODID, "green_shell").toString()));
}
