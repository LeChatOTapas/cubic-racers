package me.jesuismister.cubicracers.init;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.Banana;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class KartObjectInit {
    public static final DeferredRegister<EntityType<?>> KART_OBJECT_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CubicRacers.MODID);

    public static final RegistryObject<EntityType<Banana>> BANANA = KART_OBJECT_TYPES.register(
            "banana", () -> EntityType.Builder.<Banana>of((type, level) ->
            new Banana(type, level), MobCategory.MISC).sized(0.5f, 0.5f)
            .build(new ResourceLocation(CubicRacers.MODID, "banana").toString()));


}
