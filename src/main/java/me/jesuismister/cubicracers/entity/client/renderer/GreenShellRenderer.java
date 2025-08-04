package me.jesuismister.cubicracers.entity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.client.model.FakeBoxModel;
import me.jesuismister.cubicracers.entity.client.model.GreenShellModel;
import me.jesuismister.cubicracers.entity.custom.FakeBox;
import me.jesuismister.cubicracers.entity.custom.GreenShell;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class GreenShellRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<GreenShell, R> {

    public GreenShellRenderer(EntityRendererProvider.Context context) {
        super(context, new GreenShellModel());
    }

/*
    public GreenShellRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GreenShellModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull GreenShell animatable) {
        return new ResourceLocation(CubicRacers.MODID, GreenShell.TEXTURE);
    }

    @Override
    public void render(@NotNull GreenShell greenShell, float entityYaw, float partialTick, PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        super.render(greenShell, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
 */
}
