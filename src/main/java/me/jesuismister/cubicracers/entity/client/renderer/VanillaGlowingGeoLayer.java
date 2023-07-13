package me.jesuismister.cubicracers.entity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.texture.AutoGlowingTexture;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class VanillaGlowingGeoLayer<T extends GeoAnimatable> extends AutoGlowingGeoLayer<T> {
    public VanillaGlowingGeoLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    @Override
    protected RenderType getRenderType(T animatable) {
        Method getEmissiveResource = null;
        try {
            getEmissiveResource = AutoGlowingTexture.class.getDeclaredMethod("getEmissiveResource", ResourceLocation.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        getEmissiveResource.setAccessible(true);
        ResourceLocation res;
        try {
            res = (ResourceLocation) getEmissiveResource.invoke(AutoGlowingTexture.class, getTextureResource(animatable));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return RenderType.eyes(res);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }

}