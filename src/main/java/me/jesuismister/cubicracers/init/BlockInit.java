package me.jesuismister.cubicracers.init;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.block.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class BlockInit {;
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CubicRacers.MODID);

    //BLOCKS
    public static final DeferredBlock<Block> BOOSTER = registerBlock(
            "booster",
            (properties) -> new BoosterBlock(properties
                    .destroyTime(1.5F)
                    .explosionResistance(6.0F)
                    .sound(SoundType.AMETHYST)
                    .lightLevel(state -> 3)
            ));

    public static final DeferredBlock<Block> ROAD_BLOCK = registerBlock(
            "road_block",
            (properties) -> new RoadBlock(properties
                    .destroyTime(1.5F)
                    .explosionResistance(6.0F)
                    .sound(SoundType.STONE)
            ));

    public static final DeferredBlock<Block> ROAD_BLOCK_DIRT = registerBlock(
            "road_block_dirt",
            (properties) -> new RoadBlock(properties
                    .destroyTime(0.5F)
                    .explosionResistance(0.5F)
                    .sound(SoundType.GRAVEL)
            ));

    public static final DeferredBlock<Block> ROAD_BLOCK_SAND = registerBlock(
            "road_block_sand",
            (properties) -> new RoadBlock(properties
                    .destroyTime(0.5F)
                    .explosionResistance(0.5F)
                    .sound(SoundType.SAND)
            ));

    public static final DeferredBlock<Block> ROAD_BLOCK_SNOW = registerBlock(
            "road_block_snow",
            (properties) -> new RoadBlock(properties
                    .destroyTime(0.5F)
                    .explosionResistance(0.5F)
                    .sound(SoundType.AMETHYST)
            ));

    public static final DeferredBlock<Block> STARTING_BLOCK = registerBlock(
            "starting_block",
            (properties) -> new RoadBlock(properties
                    .destroyTime(1.5F)
                    .explosionResistance(6.0F)
                    .sound(SoundType.STONE)
            ));

    public static final DeferredBlock<Block> KART_CONTROLLER = registerBlock(
            "kart_controller",
            (properties) -> new KartController(properties
                    .destroyTime(5.0F)
                    .explosionResistance(6.0F)
                    .sound(SoundType.METAL)
                    .pushReaction(PushReaction.IGNORE)
            ));

    public static final DeferredBlock<Block> HOLLOW_ROAD_BLOCK = registerBlock(
            "hollow_road_block",
            (properties) -> new HollowRoadBlock(properties
                    .explosionResistance(1000.0F)
                    .instabreak()
                    .sound(SoundType.STONE)
                    .pushReaction(PushReaction.IGNORE)
            ));

    public static final DeferredBlock<Block> RED_BOUNCING_MUSHROOM_BLOCK = registerBlock(
            "red_bouncing_mushroom_block",
            (properties) -> new BouncingMushroomBlock(properties
                    .destroyTime(1.5F)
                    .explosionResistance(6.0F)
                    .sound(SoundType.SHROOMLIGHT)
            ));

    public static final DeferredBlock<Block> GLIDE_TRIGGER_BLOCK = registerBlock(
            "glide_trigger_block",
            (properties) -> new RoadBlock(properties
                    .destroyTime(1.5F)
                    .explosionResistance(6.0F)
                    .sound(SoundType.AMETHYST)
                    .lightLevel(state -> 3)
            ));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ItemInit.ITEMS.registerItem(name, (properties) -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
