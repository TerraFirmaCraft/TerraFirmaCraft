/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;

import net.dries007.tfc.common.blocks.SandstoneBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.chunkdata.RockData;

public class SurfaceStates
{
    public static final ISurfaceState RAW = (rockData, x, y, z, temperature, rainfall, salty) -> rockData.getRock(x, y, z).getBlock(Rock.BlockType.RAW).defaultBlockState();
    public static final ISurfaceState COBBLE = (rockData, x, y, z, temperature, rainfall, salty) -> rockData.getRock(x, y, z).getBlock(Rock.BlockType.COBBLE).defaultBlockState();
    public static final ISurfaceState GRAVEL = (rockData, x, y, z, temperature, rainfall, salty) -> rockData.getRock(x, y, z).getBlock(Rock.BlockType.GRAVEL).defaultBlockState();

    /**
     * Grass / Dirt / Gravel, or Sand / Sand / Sandstone
     */
    public static final ISurfaceState TOP_SOIL = new SoilSurfaceState(SoilBlockType.GRASS);
    public static final ISurfaceState MID_SOIL = new SoilSurfaceState(SoilBlockType.DIRT);
    public static final ISurfaceState LOW_SOIL = new DeepSoilSurfaceState();

    public static final ISurfaceState TOP_UNDERWATER = new UnderwaterSurfaceState(false);
    public static final ISurfaceState LOW_UNDERWATER = new UnderwaterSurfaceState(true);

    public static final ISurfaceState SHORE_SAND = (rockData, x, y, z, temperature, rainfall, salty) -> rockData.getBottomRock(x, z).getSand().defaultBlockState();
    public static final ISurfaceState SHORE_SANDSTONE = (rockData, x, y, z, temperature, rainfall, salty) -> rockData.getBottomRock(x, z).getSandstone().defaultBlockState();

    public static final ISurfaceState RARE_SHORE_SAND = new ISurfaceState()
    {
        private final Supplier<Block> pinkSand = TFCBlocks.SAND.get(SandBlockType.PINK);
        private final Supplier<Block> blackSand = TFCBlocks.SAND.get(SandBlockType.BLACK);

        @Override
        public BlockState state(RockData rockData, int x, int y, int z, float temperature, float rainfall, boolean salty)
        {
            if (rainfall > 300f && temperature > 15f)
            {
                return pinkSand.get().defaultBlockState();
            }
            else if (rainfall > 300f)
            {
                return blackSand.get().defaultBlockState();
            }
            else
            {
                return rockData.getBottomRock(x, z).getSand().defaultBlockState();
            }
        }
    };

    public static final ISurfaceState RARE_SHORE_SANDSTONE = new ISurfaceState()
    {
        private final Supplier<Block> pinkSandstone = TFCBlocks.SANDSTONE.get(SandBlockType.PINK).get(SandstoneBlockType.RAW);
        private final Supplier<Block> blackSandstone = TFCBlocks.SANDSTONE.get(SandBlockType.BLACK).get(SandstoneBlockType.RAW);

        @Override
        public BlockState state(RockData rockData, int x, int y, int z, float temperature, float rainfall, boolean salty)
        {
            if (rainfall > 300f && temperature > 15f)
            {
                return pinkSandstone.get().defaultBlockState();
            }
            else if (rainfall > 300f)
            {
                return blackSandstone.get().defaultBlockState();
            }
            else
            {
                return rockData.getBottomRock(x, z).getSandstone().defaultBlockState();
            }
        }
    };

    public static final ISurfaceState WATER = (rockData, x, y, z, temperature, rainfall, salty) -> salty ? TFCFluids.SALT_WATER.getSourceBlock() : Fluids.WATER.defaultFluidState().createLegacyBlock();
}
