package me.jesuismister.cubicracers.init;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CubicRacers.MODID);

    public static final List<RegistryObject<EntityType<Kart>>> KARTS = new ArrayList<>();

    public static void initAllKarts(){
        addNewKart("trash_kart", 0.8f, 0.025f, 0.5f, 3.0f, 0 , -0.5f, 0);
        addNewKart("trash_kart2", 0.8f, 0.025f, 0.5f, 3.0f, 0 , -0.5f, 0);
    }

    /**
     * Méthode qui ajoute un nouveau kart a la liste des KARTS a register
     * @param name = nom du kart (utile pour le path vers la texture, le model et l'animation)
     * @param maxSpeed = vitesse max que peux atteindre le kart
     * @param accelerationBoost = coeff d'accélération (en gros à chaque tick la vitesse = vitesse initial + accelerationBoost)
     * @param boost = montant du boost que reçoit le kart après un dérapage, objet de boost, ou autre
     * @param maniabiliteCoeff = coeff de maniabilité (+ il est élevé + le kart tourne facilement)
     * @param playerPosX = position en X du joueur dans le kart (0 = position initial en X)
     * @param playerPosY = position en Y du joueur dans le kart (0 = position initial en Y)
     * @param playerPosZ = position en Z du joueur dans le kart (0 = position initial en Z)
     */
    public static void addNewKart(String name, float maxSpeed, float accelerationBoost, float boost, float maniabiliteCoeff,
                                  float playerPosX, float playerPosY, float playerPosZ){
        String pathTexture = "textures/entity/" + name + ".png";
        String pathModel = "geo/" + name + ".geo.json";
        String pathAnimation = "animations/" + name + ".animation.json";

        KARTS.add(ENTITY_TYPES.register(name, () -> EntityType.Builder.<Kart>of((type, level) ->
                        new Kart(type, level, pathTexture, pathModel, pathAnimation, maxSpeed, accelerationBoost, boost,
                        maniabiliteCoeff, playerPosX, playerPosY, playerPosZ), MobCategory.MISC)
                .sized(1.7f, 1.2f)
                .build(new ResourceLocation(CubicRacers.MODID, name).toString())));
    }
}
