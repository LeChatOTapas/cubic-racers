package me.jesuismister.mckart.entity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jesuismister.mckart.MCKart;
import me.jesuismister.mckart.entity.client.model.KartModel;
import me.jesuismister.mckart.entity.custom.Kart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KartRenderer extends GeoEntityRenderer<Kart> {
    public KartRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KartModel());
    }

    @Override
    public ResourceLocation getTextureLocation(Kart animatable) {
        return new ResourceLocation(MCKart.MODID, "textures/entity/trash_kart.png");
    }

    @Override
    public void render(Kart entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

}