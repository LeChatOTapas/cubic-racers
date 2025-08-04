package me.jesuismister.cubicracers.init;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.block.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockInit {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CubicRacers.MODID);

    //BLOCKS
    public static final DeferredBlock<Block> BOOSTER = registerBlock("booster", () ->
            new BoosterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MAGMA_BLOCK).lightLevel((p_152684_) -> {
                return 3;
            }).sound(SoundType.AMETHYST)));

    public static final DeferredBlock<Block> ROAD_BLOCK = registerBlock("road_block", () ->
            new RoadBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));

    public static final DeferredBlock<Block> ROAD_BLOCK_DIRT = registerBlock("road_block_dirt", () ->
            new RoadBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT)));

    public static final DeferredBlock<Block> ROAD_BLOCK_SAND = registerBlock("road_block_sand", () ->
            new RoadBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SAND)));

    public static final DeferredBlock<Block> ROAD_BLOCK_SNOW = registerBlock("road_block_snow", () ->
            new RoadBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK)));

    public static final DeferredBlock<Block> STARTING_BLOCK = registerBlock("starting_block", () ->
            new RoadBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));

    public static final DeferredBlock<Block> KART_CONTROLLER = registerBlock("kart_controller", () ->
            new KartController(BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_GLAZED_TERRACOTTA)));

    public static final DeferredBlock<Block> HOLLOW_ROAD_BLOCK = registerBlock("hollow_road_block", () ->
            new HollowRoadBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SLIME_BLOCK).sound(SoundType.STONE)));

    public static final DeferredBlock<Block> RED_BOUNCING_MUSHROOM_BLOCK = registerBlock("red_bouncing_mushroom_block", () ->
            new BouncingMushroomBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_MUSHROOM_BLOCK)));

    public static final DeferredBlock<Block> GLIDE_TRIGGER_BLOCK = registerBlock("glide_trigger_block", () ->
            new BoosterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MAGMA_BLOCK).lightLevel((p_152684_) -> {
                return 3;
            }).sound(SoundType.AMETHYST)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ItemInit.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
