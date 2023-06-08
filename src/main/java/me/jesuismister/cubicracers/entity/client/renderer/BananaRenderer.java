package me.jesuismister.cubicracers.entity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.Banana;
import me.jesuismister.cubicracers.entity.client.model.BananaModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BananaRenderer extends GeoEntityRenderer<Banana> {

    public BananaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BananaModel());
    }

    @Override
    public ResourceLocation getTextureLocation(Banana animatable) {
        return new ResourceLocation(CubicRacers.MODID, animatable.TEXTURE);
    }

    @Override
    public void render(Banana banana, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
/*
        Quaternionf rotation = new Quaternionf();
        rotation.rotateY((float) - Math.toRadians(entityYaw));
        poseStack.mulPose(rotation);*/

        super.render(banana, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }
}
