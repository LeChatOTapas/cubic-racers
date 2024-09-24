package me.jesuismister.cubicracers.entity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.client.model.TestKartModel;
import me.jesuismister.cubicracers.entity.custom.TestKart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TestKartRenderer extends GeoEntityRenderer<TestKart> {

    public TestKartRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TestKartModel());
        //this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(TestKart animatable) {
        if(animatable.isInvinsible()) return new ResourceLocation(CubicRacers.MODID, "textures/entity/star_model.png");
        else return new ResourceLocation(CubicRacers.MODID, animatable.TEXTURE);
    }

    @Override
    public void render(@NotNull TestKart entity, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        //TOURNE LE KART DANS LA DIRECTION OU IL REGARDE
        Quaternionf rotation = new Quaternionf();


        if(entity.getCanMove()){
            if(entity.isDrifting()){
                if(entity.getDriftingSens().equals("Left")){
                    rotation.rotateY((float) -Math.toRadians(entityYaw - 20));
                }else{
                    rotation.rotateY((float) -Math.toRadians(entityYaw + 20));
                }
            }else{
                rotation.rotateY((float) -Math.toRadians(entityYaw));
            }
        }
        else rotation.rotateY((float) -Math.toRadians(entityYaw + entity.getStunRotation()));
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
}