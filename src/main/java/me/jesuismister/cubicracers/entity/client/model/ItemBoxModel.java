package me.jesuismister.cubicracers.entity.client.model;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.ItemBox;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.Optional;

public class ItemBoxModel extends GeoModel<ItemBox> {
    public static final String TEXTURE = "textures/entity/item_box.png";
    public static final String MODEL = "geo/item_box.geo.json";
    public static final String ANIMATION = "animations/item_box.animation.json";

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, MODEL);
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, TEXTURE);
    }

    @Override
    public ResourceLocation getAnimationResource(ItemBox banana) {
        return ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, ANIMATION);
    }
/*
    @Override
    public void setCustomAnimations(ItemBox itemBox, long instanceId, AnimationState<ItemBox> animationState) {
        super.setCustomAnimations(itemBox, instanceId, animationState);

        Optional<GeoBone> boneQuestionMark = this.getBone("question_mark");
        Player player = Minecraft.getInstance().player;
        Vec3 vec3 = EntityAnchorArgument.Anchor.EYES.apply(itemBox);
        Vec3 vec3P = new Vec3(player.getX(), player.getY(), player.getZ());
        double d0 = vec3.x - vec3P.x;
        double d2 = vec3.z - vec3P.z;

        float yrot = Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F);
        if(boneQuestionMark.isPresent()) boneQuestionMark.get().setRotY((float) -Math.toRadians(yrot));
    }
 */
}
