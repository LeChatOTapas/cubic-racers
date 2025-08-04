package me.jesuismister.cubicracers.entity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.client.model.BobOmbModel;
import me.jesuismister.cubicracers.entity.client.model.FakeBoxModel;
import me.jesuismister.cubicracers.entity.client.model.ItemBoxModel;
import me.jesuismister.cubicracers.entity.custom.BobOmb;
import me.jesuismister.cubicracers.entity.custom.FakeBox;
import me.jesuismister.cubicracers.entity.custom.ItemBox;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class FakeBoxRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<FakeBox, R> {

    public FakeBoxRenderer(EntityRendererProvider.Context context) {
        super(context, new FakeBoxModel());
    }

    /*
    public FakeBoxRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FakeBoxModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FakeBox animatable) {
        return new ResourceLocation(CubicRacers.MODID, ItemBox.TEXTURE);
    }

    @Override
    public void render(@NotNull FakeBox fakeBox, float entityYaw, float partialTick, PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        //RENDER LE KART
        super.render(fakeBox, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }

    @Override
    public RenderType getRenderType(FakeBox animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        //return super.getRenderType(animatable, texture, bufferSource, partialTick);
        return RenderType.entityTranslucentEmissive(new ResourceLocation(CubicRacers.MODID, animatable.TEXTURE));
        //return RenderType.entityCutout(new ResourceLocation(CubicRacers.MODID, animatable.TEXTURE));
    }
    */
}
