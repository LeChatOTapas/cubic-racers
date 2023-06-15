package me.jesuismister.cubicracers.entity.client.model;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.ItemBox;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class ItemBoxModel extends GeoModel<ItemBox> {

    @Override
    public ResourceLocation getModelResource(ItemBox itemBox) {
        return new ResourceLocation(CubicRacers.MODID, itemBox.MODEL);
    }

    @Override
    public ResourceLocation getTextureResource(ItemBox itemBox) {
        return new ResourceLocation(CubicRacers.MODID, itemBox.TEXTURE);
    }

    @Override
    public ResourceLocation getAnimationResource(ItemBox itemBox) {
        return new ResourceLocation(CubicRacers.MODID, itemBox.ANIMATION);
    }

    @Override
    public void setCustomAnimations(ItemBox itemBox, long instanceId, AnimationState<ItemBox> animationState) {
        super.setCustomAnimations(itemBox, instanceId, animationState);
    }
}
