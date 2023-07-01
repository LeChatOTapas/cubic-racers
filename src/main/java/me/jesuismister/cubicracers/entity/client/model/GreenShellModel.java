package me.jesuismister.cubicracers.entity.client.model;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.GreenShell;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class GreenShellModel extends GeoModel<GreenShell> {

    @Override
    public ResourceLocation getModelResource(GreenShell green_shell) {
        return new ResourceLocation(CubicRacers.MODID, green_shell.MODEL);
    }

    @Override
    public ResourceLocation getTextureResource(GreenShell green_shell) {
        return new ResourceLocation(CubicRacers.MODID, green_shell.TEXTURE);
    }

    @Override
    public ResourceLocation getAnimationResource(GreenShell green_shell) {
        return new ResourceLocation(CubicRacers.MODID, green_shell.ANIMATION);
    }

    @Override
    public void setCustomAnimations(GreenShell green_shell, long instanceId, AnimationState<GreenShell> animationState) {
        super.setCustomAnimations(green_shell, instanceId, animationState);
    }
}
