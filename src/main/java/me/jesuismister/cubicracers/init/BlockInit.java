package me.jesuismister.cubicracers.init;

import me.jesuismister.cubicracers.CubicRacers;
import me.jesuismister.cubicracers.block.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CubicRacers.MODID);

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ItemInit.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    //BLOCKS
    public static final RegistryObject<Block> BOOSTER = registerBlock("booster", () ->
            new BoosterBlock(BlockBehaviour.Properties.copy(Blocks.MAGMA_BLOCK).lightLevel((p_152684_) -> {
                return 3;
            }).sound(SoundType.AMETHYST)));

    public static final RegistryObject<Block> ROAD_BLOCK = registerBlock("road_block", () ->
            new RoadBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));

    public static final RegistryObject<Block> ROAD_BLOCK_DIRT = registerBlock("road_block_dirt", () ->
            new RoadBlock(BlockBehaviour.Properties.copy(Blocks.DIRT)));

    public static final RegistryObject<Block> ROAD_BLOCK_SAND = registerBlock("road_block_sand", () ->
            new RoadBlock(BlockBehaviour.Properties.copy(Blocks.SAND)));

    public static final RegistryObject<Block> ROAD_BLOCK_SNOW = registerBlock("road_block_snow", () ->
            new RoadBlock(BlockBehaviour.Properties.copy(Blocks.SNOW_BLOCK)));

    public static final RegistryObject<Block> STARTING_BLOCK = registerBlock("starting_block", () ->
            new RoadBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));

    public static final RegistryObject<Block> KART_CONTROLLER = registerBlock("kart_controller", () ->
            new KartController(BlockBehaviour.Properties.copy(Blocks.PINK_GLAZED_TERRACOTTA)));

    public static final RegistryObject<Block> HOLLOW_ROAD_BLOCK = registerBlock("hollow_road_block", () ->
            new HollowRoadBlock(BlockBehaviour.Properties.copy(Blocks.SLIME_BLOCK).sound(SoundType.STONE)));

    public static final RegistryObject<Block> RED_BOUNCING_MUSHROOM_BLOCK = registerBlock("red_bouncing_mushroom_block", () ->
            new BouncingMushroomBlock(BlockBehaviour.Properties.copy(Blocks.RED_MUSHROOM_BLOCK)));
}
