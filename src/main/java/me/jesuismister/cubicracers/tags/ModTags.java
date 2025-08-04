package me.jesuismister.cubicracers.tags;

import me.jesuismister.cubicracers.CubicRacers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks{
        public static final TagKey<Block> ROAD_BLOCK_TAG = tag("road_block_tag");

        private static TagKey<Block> tag (String name){
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(CubicRacers.MODID, name));
        }
    }

    /*
    public static class Items{
        private static TagKey<Item> tag (String name){
            return ItemTags.create(new ResourceLocation(CubicRacers.MODID, name));
        }
    }
    */
}
