package me.jesuismister.cubicracers.entity.client.model;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.Banana;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BananaModel extends GeoModel<Banana> {
    public static final String TEXTURE = "textures/entity/banana.png";
    public static final String MODEL = "banana";
    public static final String ANIMATION = "banana";

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, MODEL);
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, TEXTURE);
    }

    @Override
    public ResourceLocation getAnimationResource(Banana banana) {
        return ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, ANIMATION);
    }
}
