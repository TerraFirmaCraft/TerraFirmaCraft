package net.dries007.tfc.integration.recipes;

import java.util.Arrays;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import com.alcatrazescapee.mcjunitlib.framework.*;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;

@IntegrationTestClass("recipes")
@SuppressWarnings("ConstantConditions")
public class LandslideRecipeTests
{
    @IntegrationTestFactory("landslide_base")
    public Stream<DynamicIntegrationTest> testVanillaDirtLandslides()
    {
        return Stream.of(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.COARSE_DIRT).map(block -> {
            String name = block.getRegistryName().getPath();
            return DynamicIntegrationTest.create(name, helper -> {
                helper.placeBlock(new BlockPos(1, 3, 2), Direction.UP, block);
                helper.assertAirAt(new BlockPos(1, 4, 2), name + " should have fallen");
                helper.assertBlockAt(new BlockPos(1, 1, 1), Blocks.DIRT, name + " should have landed");
            });
        });
    }

    @IntegrationTestFactory("landslide_base")
    public Stream<DynamicIntegrationTest> testVanillaBlockLandslides()
    {
        return Stream.of(Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE).map(block -> {
            String name = block.getRegistryName().getPath();
            return DynamicIntegrationTest.create(name, helper -> {
                helper.placeBlock(new BlockPos(1, 3, 2), Direction.UP, block);
                helper.assertAirAt(new BlockPos(1, 4, 2), name + " should have fallen");
                helper.assertBlockAt(new BlockPos(1, 1, 1), block, name + " should have landed");
            });
        });
    }

    @IntegrationTestFactory("landslide_base")
    public Stream<DynamicIntegrationTest> testDirtLandslides()
    {
        return Stream.of(SoilBlockType.DIRT, SoilBlockType.GRASS)
            .flatMap(type -> Arrays.stream(SoilBlockType.Variant.values()).map(variant -> {
                Block startBlock = TFCBlocks.SOIL.get(type).get(variant).get();
                Block endBlock = TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(variant).get();
                String name = startBlock.getRegistryName().getPath();
                return DynamicIntegrationTest.create(name, helper -> {
                    helper.placeBlock(new BlockPos(1, 3, 2), Direction.UP, startBlock);
                    helper.assertAirAt(new BlockPos(1, 4, 2), name + " should have fallen");
                    helper.assertBlockAt(new BlockPos(1, 1, 1), endBlock, name + " should have landed");
                });
            }));
    }

    @IntegrationTestFactory("landslide_base")
    public Stream<DynamicIntegrationTest> testClayDirtLandslides()
    {
        return Stream.of(SoilBlockType.CLAY, SoilBlockType.CLAY_GRASS)
            .flatMap(type -> Arrays.stream(SoilBlockType.Variant.values()).map(variant -> {
                Block startBlock = TFCBlocks.SOIL.get(type).get(variant).get();
                Block endBlock = TFCBlocks.SOIL.get(SoilBlockType.CLAY).get(variant).get();
                String name = startBlock.getRegistryName().getPath();
                return DynamicIntegrationTest.create(name, helper -> {
                    helper.placeBlock(new BlockPos(1, 3, 2), Direction.UP, startBlock);
                    helper.assertAirAt(new BlockPos(1, 4, 2), name + " should have fallen");
                    helper.assertBlockAt(new BlockPos(1, 1, 1), endBlock, name + " should have landed");
                });
            }));
    }

    @IntegrationTest("landslide_with_piston")
    public void testFallOnPistonMove(IntegrationTestHelper helper)
    {
        helper.pullLever(new BlockPos(0, 4, 0));
        helper.assertAirAt(new BlockPos(2, 4, 1), "block should've fallen once piston moved");
        helper.assertBlockAt(new BlockPos(2, 1, 1), TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(SoilBlockType.Variant.LOAM).get(), "block should have landed once piston moved");
    }

    @IntegrationTest("landslide_with_piston")
    public void testPistonOpenClose4Ticks(IntegrationTestHelper helper)
    {
        helper.pullLever(new BlockPos(0, 4, 0));
        helper.runAfter(4, () -> helper.pullLever(new BlockPos(0, 4, 0)));
        helper.assertAirAt(new BlockPos(2, 4, 1), "block should've fallen once piston moved");
        helper.assertBlockAt(new BlockPos(2, 3, 1), Blocks.RED_CONCRETE, "piston moved block should not have been broken");
        helper.assertBlockAt(new BlockPos(2, 1, 1), TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(SoilBlockType.Variant.LOAM).get(), "block should have landed once piston moved");
    }

    @IntegrationTest("landslide_with_piston")
    public void testPistonOpenClose4TicksVanilla(IntegrationTestHelper helper)
    {
        helper.setBlockState(new BlockPos(2, 4, 1), Blocks.WHITE_CONCRETE_POWDER.defaultBlockState());
        helper.runAfter(4, () -> helper.pullLever(new BlockPos(0, 4, 0)))
            .thenRun(4, () -> helper.pullLever(new BlockPos(0, 4, 0)));
        helper.assertAirAt(new BlockPos(2, 4, 1), "block should've fallen once piston moved");
        helper.assertBlockAt(new BlockPos(2, 3, 1), Blocks.RED_CONCRETE, "piston moved block should not have been broken");
        helper.assertBlockAt(new BlockPos(2, 1, 1), Blocks.WHITE_CONCRETE_POWDER, "block should have landed once piston moved");
    }
}
