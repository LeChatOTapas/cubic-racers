package me.jesuismister.mckart.init;

import me.jesuismister.mckart.MCKart;
import me.jesuismister.mckart.entity.custom.Kart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MCKart.MODID);

    public static final RegistryObject<EntityType<Kart>> KART =
            ENTITY_TYPES.register("kart", () -> EntityType.Builder.<Kart>of(Kart::new, MobCategory.MISC)
                    .sized(2.0f, 1.0f)
                    .build(new ResourceLocation(MCKart.MODID, "kart").toString()));

}
