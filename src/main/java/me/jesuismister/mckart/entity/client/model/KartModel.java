package me.jesuismister.mckart.entity.client.model;

import me.jesuismister.mckart.MCKart;
import me.jesuismister.mckart.entity.custom.Kart;
import me.jesuismister.mckart.util.KeyBinds;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import java.util.Optional;

public class KartModel extends GeoModel<Kart> {
    private static float ROTATE_WHEELS_DEGREE = 15;

    @Override
    public ResourceLocation getModelResource(Kart kart) {
        return new ResourceLocation(MCKart.MODID, "geo/trash_kart.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Kart kart) {
        return new ResourceLocation(MCKart.MODID, "textures/entity/trash_kart.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Kart kart) {

        return new ResourceLocation(MCKart.MODID, "animations/trash_kart.animation.json");
    }

    @Override
    public void setCustomAnimations(Kart kart, long instanceId, AnimationState<Kart> animationState) {
        super.setCustomAnimations(kart, instanceId, animationState);
        
        Optional<GeoBone> bonesGauches = this.getBone("roue_avant_gauche");
        Optional<GeoBone> bonesDroit = this.getBone("roue_avant_droite");

        if (bonesGauches.isPresent()) {
            if(kart.keyLeftDown()) bonesGauches.get().setRotY(ROTATE_WHEELS_DEGREE * Mth.DEG_TO_RAD);
            if(kart.keyRightDown()) bonesGauches.get().setRotY(-ROTATE_WHEELS_DEGREE * Mth.DEG_TO_RAD);
        }
        if (bonesDroit.isPresent()) {
            if(kart.keyLeftDown()) bonesDroit.get().setRotY((ROTATE_WHEELS_DEGREE+180) * Mth.DEG_TO_RAD);
            if(kart.keyRightDown()) bonesDroit.get().setRotY((-ROTATE_WHEELS_DEGREE-180) * Mth.DEG_TO_RAD);
        }
    }
}
