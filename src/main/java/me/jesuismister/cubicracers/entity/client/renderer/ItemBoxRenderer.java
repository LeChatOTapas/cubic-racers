package me.jesuismister.cubicracers.entity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.client.model.ItemBoxModel;
import me.jesuismister.cubicracers.entity.custom.ItemBox;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ItemBoxRenderer extends GeoEntityRenderer<ItemBox> {

    public ItemBoxRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ItemBoxModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ItemBox animatable) {
        return new ResourceLocation(CubicRacers.MODID, ItemBox.TEXTURE);
    }

    @Override
    public void render(@NotNull ItemBox itemBox, float entityYaw, float partialTick, PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        super.render(itemBox, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
