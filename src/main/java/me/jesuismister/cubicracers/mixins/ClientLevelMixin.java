package me.jesuismister.cubicracers.mixins;

import me.jesuismister.cubicracers.init.BlockInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "getMarkerParticleTarget", at = @At("RETURN"), cancellable = true)
    private void getMarkerParticleTarget(CallbackInfoReturnable<Block> cir) {
        if (cir.getReturnValue() != null) return;

        ItemStack itemStack = this.minecraft.player.getMainHandItem();
        Item item = itemStack.getItem();
        if (item.equals(Items.STICK) || item.equals(BlockInit.HOLLOW_ROAD_BLOCK.get().asItem())) {
            cir.setReturnValue(BlockInit.HOLLOW_ROAD_BLOCK.get());
        }
    }
}
