package me.jesuismister.cubicracers.entity.client.model;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.FakeBox;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class FakeBoxModel extends GeoModel<FakeBox> {

    @Override
    public ResourceLocation getModelResource(FakeBox fakeBox) {
        return new ResourceLocation(CubicRacers.MODID, fakeBox.MODEL);
    }

    @Override
    public ResourceLocation getTextureResource(FakeBox fakeBox) {
        return new ResourceLocation(CubicRacers.MODID, fakeBox.TEXTURE);
    }

    @Override
    public ResourceLocation getAnimationResource(FakeBox fakeBox) {
        return new ResourceLocation(CubicRacers.MODID, fakeBox.ANIMATION);
    }

    @Override
    public void setCustomAnimations(FakeBox fakeBox, long instanceId, AnimationState<FakeBox> animationState) {
        super.setCustomAnimations(fakeBox, instanceId, animationState);
    }
}
