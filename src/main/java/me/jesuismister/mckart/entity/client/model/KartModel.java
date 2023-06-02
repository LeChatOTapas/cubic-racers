package me.jesuismister.mckart.entity.client.model;

import me.jesuismister.mckart.MCKart;
import me.jesuismister.mckart.entity.custom.Kart;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KartModel extends GeoModel<Kart> {

    @Override
    public ResourceLocation getModelResource(Kart animatable) {
        return new ResourceLocation(MCKart.MODID, "geo/trash_kart.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Kart animatable) {
        return new ResourceLocation(MCKart.MODID, "textures/entity/trash_kart.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Kart animatable) {
        return new ResourceLocation(MCKart.MODID, "animations/trash_kart.animation.json");
    }
}
