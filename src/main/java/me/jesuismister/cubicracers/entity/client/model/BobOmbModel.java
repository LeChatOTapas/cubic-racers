package me.jesuismister.cubicracers.entity.client.model;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.BobOmb;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BobOmbModel extends GeoModel<BobOmb> {

    @Override
    public ResourceLocation getModelResource(BobOmb bombOmb) {
        return new ResourceLocation(CubicRacers.MODID, bombOmb.MODEL);
    }

    @Override
    public ResourceLocation getTextureResource(BobOmb bombOmb) {
        return new ResourceLocation(CubicRacers.MODID, bombOmb.TEXTURE);
    }

    @Override
    public ResourceLocation getAnimationResource(BobOmb bombOmb) {
        return new ResourceLocation(CubicRacers.MODID, bombOmb.ANIMATION);
    }

    @Override
    public void setCustomAnimations(BobOmb bombOmb, long instanceId, AnimationState<BobOmb> animationState) {
        super.setCustomAnimations(bombOmb, instanceId, animationState);
    }
}
