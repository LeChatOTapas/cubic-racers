package me.jesuismister.cubicracers.entity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.client.model.FakeBoxModel;
import me.jesuismister.cubicracers.entity.custom.FakeBox;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FakeBoxRenderer extends GeoEntityRenderer<FakeBox> {

    public FakeBoxRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FakeBoxModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FakeBox animatable) {
        return new ResourceLocation(CubicRacers.MODID, FakeBox.TEXTURE);
    }

    @Override
    public void render(@NotNull FakeBox fakeBox, float entityYaw, float partialTick, PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        super.render(fakeBox, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
