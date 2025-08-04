package me.jesuismister.cubicracers.entity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.custom.Banana;
import me.jesuismister.cubicracers.entity.client.model.BananaModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BananaRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Banana, R> {

    public BananaRenderer(EntityRendererProvider.Context context) {
        super(context, new BananaModel());
    }

    /*
    public BananaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BananaModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Banana animatable) {
        return ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, BananaModel.TEXTURE);
    }

    @Override
    public void render(@NotNull Banana banana, float entityYaw, float partialTick, PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        super.render(banana, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
    */
}
