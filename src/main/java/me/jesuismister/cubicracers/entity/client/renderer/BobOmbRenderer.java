package me.jesuismister.cubicracers.entity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.client.model.BobOmbModel;
import me.jesuismister.cubicracers.entity.custom.BobOmb;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BobOmbRenderer extends GeoEntityRenderer<BobOmb> {

    public BobOmbRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BobOmbModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BobOmb animatable) {
        return new ResourceLocation(CubicRacers.MODID, BobOmb.TEXTURE);
    }

    @Override
    public void render(@NotNull BobOmb bombOmb, float entityYaw, float partialTick, PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        super.render(bombOmb, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
