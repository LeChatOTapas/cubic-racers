package me.jesuismister.cubicracers.entity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.client.model.KartModel;
import me.jesuismister.cubicracers.entity.custom.Kart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class KartRenderer extends GeoEntityRenderer<Kart> {

    public KartRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KartModel());
        //this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(Kart animatable) {
        if(animatable.getIsInvinsible()) return new ResourceLocation(CubicRacers.MODID, "textures/entity/star_model.png");
        else return new ResourceLocation(CubicRacers.MODID, animatable.TEXTURE);
    }

    @Override
    public void render(@NotNull Kart entity, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        //TOURNE LE KART DANS LA DIRECTION OU IL REGARDE
        Quaternionf rotation = new Quaternionf();

        if(entity.getCanMove()){
            if(entity.getIsDrifting()){
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


}