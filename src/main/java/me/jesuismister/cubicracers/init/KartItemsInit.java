package me.jesuismister.cubicracers.init;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.Supplier;

public class KartItemsInit {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, CubicRacers.MODID);


    //ITEM BOX
    public static final Supplier<EntityType<ItemBox>> ITEM_BOX =
            ENTITY_TYPES.register("item_box",
                    () -> EntityType.Builder.<ItemBox>of(ItemBox::new, MobCategory.MISC)
                            .sized(ItemBox.HITBOX_X, ItemBox.HITBOX_Y)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.withDefaultNamespace("item_box"))));

    //BANANE
    public static final Supplier<EntityType<Banana>> BANANA =
            ENTITY_TYPES.register("banana",
                    () -> EntityType.Builder.of(Banana::new, MobCategory.MISC)
                            .sized(Banana.HITBOX, Banana.HITBOX)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.withDefaultNamespace("banana"))));

    //BOMB OMB
    public static final Supplier<EntityType<BobOmb>> BOMB_OMB =
            ENTITY_TYPES.register("bob_omb",
                    () -> EntityType.Builder.of(BobOmb::new, MobCategory.MISC)
                            .sized(BobOmb.HITBOX, BobOmb.HITBOX)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.withDefaultNamespace("bob_omb"))));

    //GREEN SHELL
    public static final Supplier<EntityType<GreenShell>> GREEN_SHELL =
            ENTITY_TYPES.register("green_shell",
                    () -> EntityType.Builder.of(GreenShell::new, MobCategory.MISC)
                            .sized(GreenShell.HITBOX, Banana.HITBOX)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.withDefaultNamespace("green_shell"))));

    //ITEM BOX
    public static final Supplier<EntityType<FakeBox>> FAKE_BOX =
            ENTITY_TYPES.register("fake_box",
                    () -> EntityType.Builder.<FakeBox>of(FakeBox::new, MobCategory.MISC)
                            .sized(FakeBox.HITBOX_X, FakeBox.HITBOX_Y)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.withDefaultNamespace("fake_box")))); //peut -être "item_box" plutot que "fake_box" ici

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
