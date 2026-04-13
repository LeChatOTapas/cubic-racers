package me.jesuismister.cubicracers.mixins;

import me.jesuismister.cubicracers.init.BlockInit;
import me.jesuismister.cubicracers.init.ItemInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "getMarkerParticleTarget", at = @At("RETURN"), cancellable = true)
    private void getMarkerParticleTarget(CallbackInfoReturnable<Block> cir) {
        if (cir.getReturnValue() != null) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ItemStack itemStack = mc.player.getMainHandItem();
        Item item = itemStack.getItem();
        if (item.equals(ItemInit.ROAD_MAKER.get()) || item.equals(BlockInit.HOLLOW_ROAD_BLOCK.get().asItem())) {
            cir.setReturnValue(BlockInit.HOLLOW_ROAD_BLOCK.get());
        }
    }
}
