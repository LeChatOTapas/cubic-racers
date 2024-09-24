package me.jesuismister.cubicracers.init;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.KartData;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class KartInit {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CubicRacers.MODID);

    public static final Map<String, RegistryObject<EntityType<TestKart>>> KARTS = new HashMap<>();

    public static void initAllKarts() {
        for (KartData d : CubicRacers.KARTS_DATA) {
            addNewKart(d.id, d.name, d.texture, d.model, d.animation, d.playerPosY, d.hitboxX, d.hitboxY);
        }
    }

    public static void addNewKart(int id, String name, String texture, String model, String animation,
                                  float playerPosY, float hitboxX, float hitboxY) {

        KARTS.put(name, ENTITY_TYPES.register(name, () -> EntityType.Builder.<TestKart>of((type, level) ->
                        new TestKart(type, level, id, texture, model, animation, playerPosY, hitboxX, hitboxY), MobCategory.MISC)
                .sized(hitboxX, hitboxY)
                .build(new ResourceLocation(CubicRacers.MODID, name).toString())));
    }
}
