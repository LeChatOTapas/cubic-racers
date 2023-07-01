package me.jesuismister.cubicracers.entity.client.model;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.BombOmb;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BobOmbModel extends GeoModel<BombOmb> {

    @Override
    public ResourceLocation getModelResource(BombOmb bombOmb) {
        return new ResourceLocation(CubicRacers.MODID, bombOmb.MODEL);
    }

    @Override
    public ResourceLocation getTextureResource(BombOmb bombOmb) {
        return new ResourceLocation(CubicRacers.MODID, bombOmb.TEXTURE);
    }

    @Override
    public ResourceLocation getAnimationResource(BombOmb bombOmb) {
        return new ResourceLocation(CubicRacers.MODID, bombOmb.ANIMATION);
    }

    @Override
    public void setCustomAnimations(BombOmb bombOmb, long instanceId, AnimationState<BombOmb> animationState) {
        super.setCustomAnimations(bombOmb, instanceId, animationState);
    }
}
