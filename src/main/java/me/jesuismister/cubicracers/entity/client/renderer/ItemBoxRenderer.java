package me.jesuismister.cubicracers.entity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.entity.client.model.GreenShellModel;
import me.jesuismister.cubicracers.entity.client.model.ItemBoxModel;
import me.jesuismister.cubicracers.entity.custom.GreenShell;
import me.jesuismister.cubicracers.entity.custom.ItemBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class ItemBoxRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<ItemBox, R> {

    public ItemBoxRenderer(EntityRendererProvider.Context context) {
        super(context, new ItemBoxModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    /*
    public ItemBoxRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ItemBoxModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ItemBox animatable) {
        return new ResourceLocation(CubicRacers.MODID, ItemBox.TEXTURE);
    }

    @Override
    public void render(@NotNull ItemBox itemBox, float entityYaw, float partialTick, PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        //RENDER LE KART
        super.render(itemBox, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }

    @Override
    public RenderType getRenderType(ItemBox animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        //return super.getRenderType(animatable, texture, bufferSource, partialTick);
        return RenderType.entityTranslucentEmissive(new ResourceLocation(CubicRacers.MODID, animatable.TEXTURE));
        //return RenderType.entityCutout(new ResourceLocation(CubicRacers.MODID, animatable.TEXTURE));
    }
     */
}
