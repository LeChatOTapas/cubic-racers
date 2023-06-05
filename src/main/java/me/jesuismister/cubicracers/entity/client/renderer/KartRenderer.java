package me.jesuismister.cubicracers.entity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.client.model.KartModel;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KartRenderer extends GeoEntityRenderer<Kart> {
    public KartRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KartModel());
    }

    @Override
    public ResourceLocation getTextureLocation(Kart animatable) {
        return new ResourceLocation(CubicRacers.MODID, "textures/entity/trash_kart.png");
    }

    @Override
    public void render(Kart entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        //Tourne le kart dans la direction où il regarde
        Quaternionf rotation = new Quaternionf();
        rotation.rotateY((float) - Math.toRadians(entityYaw));
        poseStack.mulPose(rotation);

        //Render le kart
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }


}