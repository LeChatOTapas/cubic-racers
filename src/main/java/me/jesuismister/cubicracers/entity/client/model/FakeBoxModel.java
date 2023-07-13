package me.jesuismister.cubicracers.entity.client.model;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.FakeBox;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import java.util.Optional;

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

        Optional<GeoBone> boneQuestionMark = this.getBone("question_mark");
        Player player = Minecraft.getInstance().player;
        Vec3 vec3 = EntityAnchorArgument.Anchor.EYES.apply(fakeBox);
        Vec3 vec3P = new Vec3(player.getX(), player.getY(), player.getZ());
        double d0 = vec3.x - vec3P.x;
        double d2 = vec3.z - vec3P.z;

        float yrot = Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F);
        if(boneQuestionMark.isPresent()) boneQuestionMark.get().setRotY((float) -Math.toRadians(yrot));
    }
}
