package me.jesuismister.cubicracers.entity.client.model;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.Banana;
import me.jesuismister.cubicracers.entity.custom.BobOmb;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BobOmbModel extends GeoModel<BobOmb> {
    public static final String TEXTURE = "textures/entity/bob_omb.png";
    public static final String MODEL = "bob_omb";
    public static final String ANIMATION = "bob_omb";

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, MODEL);
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, TEXTURE);
    }

    @Override
    public ResourceLocation getAnimationResource(BobOmb bobOmb) {
        return ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, ANIMATION);
    }
}
