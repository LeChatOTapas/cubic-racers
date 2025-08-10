package me.jesuismister.cubicracers.entity.client.renderer;

import me.jesuismister.cubicracers.entity.client.KartRenderData;
import me.jesuismister.cubicracers.entity.client.model.ItemBoxModel;
import me.jesuismister.cubicracers.entity.client.model.TestKartModel;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class TestKartRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<TestKart, R> {

    public TestKartRenderer(EntityRendererProvider.Context context) {
        super(context, new TestKartModel());
        //this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
    @Override
    public void addRenderData(TestKart kart, Void relatedObject, R renderState) {
        super.addRenderData(kart, relatedObject, renderState);

        // On stocke dans le renderState les chemins dynamiques
        renderState.addGeckolibData(
                KartRenderData.MODEL_PATH,
                kart.MODEL
        );
        renderState.addGeckolibData(
                KartRenderData.TEXTURE_PATH,
                kart.isInvincible()
                        ? "textures/entity/star_model.png"
                        : kart.TEXTURE
        );
    }
/*
    public TestKartRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TestKartModel());
        //this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(TestKart animatable) {
        if (animatable.isInvinsible()) return new ResourceLocation(CubicRacers.MODID, "textures/entity/star_model.png");
        else return new ResourceLocation(CubicRacers.MODID, animatable.TEXTURE);
    }

    @Override
    public void render(@NotNull TestKart entity, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        //TOURNE LE KART DANS LA DIRECTION OU IL REGARDE
        Quaternionf rotation = new Quaternionf();
        if (!entity.isStun()) {
            if (entity.isDrifting()) {
                if (entity.getDriftingSens().equals("Left")) {
                    rotation.rotateY((float) -Math.toRadians(entityYaw - 20));
                } else {
                    rotation.rotateY((float) -Math.toRadians(entityYaw + 20));
                }
            } else {
                rotation.rotateY((float) -Math.toRadians(entityYaw));
            }
        } else {
            rotation.rotateY((float) -Math.toRadians(entityYaw + entity.getStunRotation()));
        }
        poseStack.mulPose(rotation);

        //RENDER LE KART
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }

    @Override
    public RenderType getRenderType(TestKart animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);

        //Ca fait bugué le glowmask de l'étoile ???
        //return RenderType.entityTranslucent(new ResourceLocation(CubicRacers.MODID, animatable.TEXTURE));
    }
    */
}