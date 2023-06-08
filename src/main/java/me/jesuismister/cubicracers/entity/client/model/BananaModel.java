package me.jesuismister.cubicracers.entity.client.model;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.Banana;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BananaModel extends GeoModel<Banana> {

    @Override
    public ResourceLocation getModelResource(Banana banana) {
        return new ResourceLocation(CubicRacers.MODID, banana.MODEL);
    }

    @Override
    public ResourceLocation getTextureResource(Banana banana) {
        return new ResourceLocation(CubicRacers.MODID, banana.TEXTURE);
    }

    @Override
    public ResourceLocation getAnimationResource(Banana banana) {
        return new ResourceLocation(CubicRacers.MODID, banana.ANIMATION);
    }

    @Override
    public void setCustomAnimations(Banana banana, long instanceId, AnimationState<Banana> animationState) {
        super.setCustomAnimations(banana, instanceId, animationState);
    }
}
