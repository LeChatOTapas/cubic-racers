package me.jesuismister.cubicracers.init;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.KartData;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KartInit {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CubicRacers.MODID);

    public static final List<KartData> KARTS_DATA = new ArrayList<>();
    public static final Map<String, RegistryObject<EntityType<Kart>>> KARTS = new HashMap<>();

    public static void initAllKarts() {
        /*
        KARTS_DATA.add(new KartData("trash_kart",    0.60f, 0.020f, 0.6f, 3.0f, -0.5f, 1.7f, 1.2f));
        KARTS_DATA.add(new KartData("standard_kart", 0.80f, 0.030f, 0.3f, 3.0f, -1.0f, 2.2f, 1.3f));
        KARTS_DATA.add(new KartData("flame_flyer",   0.90f, 0.020f, 0.3f, 3.0f, -0.9f, 2.1f, 1.4f));
        KARTS_DATA.add(new KartData("b_dasher",      0.70f, 0.035f, 0.5f, 3.0f, -0.8f, 2.3f, 1.0f));
        KARTS_DATA.add(new KartData("zipper",        0.80f, 0.030f, 0.2f, 3.0f, -0.8f, 1.7f, 1.2f));
        KARTS_DATA.add(new KartData("mach_celere",   0.70f, 0.045f, 0.3f, 3.0f, -0.8f, 2.3f, 1.0f));
        KARTS_DATA.add(new KartData("rally_romper",  1.00f, 0.015f, 0.1f, 3.0f, -0.3f, 2.5f, 1.8f));
        */
        KARTS_DATA.add(new KartData("standard_kart", 1.20f, 0.025f, 0.5f, 2.5f, -1.0f, 2.2f, 1.3f));
        KARTS_DATA.add(new KartData("flame_flyer",   1.20f, 0.025f, 0.5f, 2.5f, -0.9f, 2.1f, 1.4f));
        KARTS_DATA.add(new KartData("b_dasher",      1.20f, 0.025f, 0.5f, 2.5f, -0.8f, 2.3f, 1.0f));
        KARTS_DATA.add(new KartData("zipper",        1.20f, 0.025f, 0.5f, 2.5f, -0.8f, 1.7f, 1.2f));
        KARTS_DATA.add(new KartData("mach_celere",   1.20f, 0.025f, 0.5f, 2.5f, -0.8f, 2.3f, 1.0f));
        KARTS_DATA.add(new KartData("rally_romper",  1.20f, 0.025f, 0.5f, 2.5f, -0.7f, 2.5f, 1.8f));
        KARTS_DATA.add(new KartData("trash_kart",    1.20f, 0.025f, 0.5f, 2.5f, -0.6f, 1.8f, 1.8f));
        
        for (KartData d : KARTS_DATA) {
            addNewKart(d.name, d.texture, d.model, d.animation, d.maxSpeed, d.accelerationBoost, d.boost, d.maniabiliteCoeff, d.playerPosY, d.hitboxX, d.hitboxY);
        }
    }

    /**
     * Méthode qui ajoute un nouveau kart a la liste des KARTS a register
     *
     * @param name              = nom du kart (utile pour le path vers la texture, le model et l'animation)
     * @param maxSpeed          = vitesse max que peux atteindre le kart
     * @param accelerationBoost = coeff d'accélération (en gros à chaque tick la vitesse = vitesse initial + accelerationBoost)
     * @param boost             = montant du boost que reçoit le kart après un dérapage, objet de boost, ou autre
     * @param maniabiliteCoeff  = coeff de maniabilité (+ il est élevé + le kart tourne facilement)
     * @param playerPosY        = position en Y du joueur dans le kart (0 = position initial en Y)
     */
    public static void addNewKart(String name, String texture, String model, String animation, float maxSpeed, float accelerationBoost, float boost, float maniabiliteCoeff,
                                  float playerPosY, float hitboxX, float hitboxY) {

        KARTS.put(name, ENTITY_TYPES.register(name, () -> EntityType.Builder.<Kart>of((type, level) ->
                        new Kart(type, level, texture, model, animation, maxSpeed, accelerationBoost, boost,
                                maniabiliteCoeff, playerPosY, hitboxX, hitboxY), MobCategory.MISC)
                .sized(hitboxX, hitboxY)
                .build(new ResourceLocation(CubicRacers.MODID, name).toString())));
    }
}
