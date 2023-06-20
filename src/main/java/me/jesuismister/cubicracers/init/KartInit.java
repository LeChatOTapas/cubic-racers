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
        KARTS_DATA.add(new KartData("trash_kart", 0.6f, 0.02f, 0.2f, 3.0f, -0.5f));
        KARTS_DATA.add(new KartData("trash_kart2", 0.6f, 0.02f, 0.2f, 3.0f, -0.5f)); //PROVISOIRE - NE PAS OUBLIER D'ENLEVER LES TEXTURES INUTILES

        for (KartData d : KARTS_DATA) {
            addNewKart(d.name, d.texture, d.model, d.animation, d.maxSpeed, d.accelerationBoost, d.boost, d.maniabiliteCoeff, d.playerPosY);
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
                                  float playerPosY) {

        KARTS.put(name, ENTITY_TYPES.register(name, () -> EntityType.Builder.<Kart>of((type, level) ->
                        new Kart(type, level, texture, model, animation, maxSpeed, accelerationBoost, boost,
                                maniabiliteCoeff, playerPosY), MobCategory.MISC)
                .sized(1.7f, 1.2f)
                .build(new ResourceLocation(CubicRacers.MODID, name).toString())));
    }
}
