/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import net.minecraft.fluid.Fluids;

import net.dries007.tfc.common.blocks.SandstoneBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.types.Rock;

public class SurfaceStates
{
    public static final ISurfaceState RAW = (rockData, x, y, z, temperature, rainfall, salty) -> rockData.getRock(x, y, z).getBlock(Rock.BlockType.RAW).defaultBlockState();
    public static final ISurfaceState COBBLE = (rockData, x, y, z, temperature, rainfall, salty) -> rockData.getRock(x, y, z).getBlock(Rock.BlockType.COBBLE).defaultBlockState();
    public static final ISurfaceState GRAVEL = (rockData, x, y, z, temperature, rainfall, salty) -> rockData.getTopRock(x, z).getBlock(Rock.BlockType.GRAVEL).defaultBlockState();

    /**
     * Grass / Dirt / Gravel, or Sand / Sand / Sandstone
     */
    public static final ISurfaceState TOP_SOIL = new SoilSurfaceState(SoilBlockType.GRASS);
    public static final ISurfaceState MID_SOIL = new SoilSurfaceState(SoilBlockType.DIRT);
    public static final ISurfaceState LOW_SOIL = new DeepSoilSurfaceState();

    public static final ISurfaceState TOP_UNDERWATER = new UnderwaterSurfaceState(false);
    public static final ISurfaceState LOW_UNDERWATER = new UnderwaterSurfaceState(true);

    public static final ISurfaceState SHORE_SAND = (rockData, x, y, z, temperature, rainfall, salty) -> TFCBlocks.SAND.get(rockData.getTopRock(x, z).getBeachSandColor()).get().defaultBlockState();
    public static final ISurfaceState SHORE_SANDSTONE = (rockData, x, y, z, temperature, rainfall, salty) -> TFCBlocks.SANDSTONE.get(rockData.getTopRock(x, z).getBeachSandColor()).get(SandstoneBlockType.RAW).get().defaultBlockState();

    public static final ISurfaceState RARE_SHORE_SAND = (rockData, x, y, z, temperature, rainfall, salty) -> {
        if (rainfall > 300f && temperature > 15f)
        {
            return TFCBlocks.SAND.get(SandBlockType.PINK).get().defaultBlockState();
        }
        else if (rainfall > 300f)
        {
            return TFCBlocks.SAND.get(SandBlockType.BLACK).get().defaultBlockState();
        }
        else
        {
            return TFCBlocks.SAND.get(rockData.getTopRock(x, z).getBeachSandColor()).get().defaultBlockState();
        }
    };

    public static final ISurfaceState RARE_SHORE_SANDSTONE = (rockData, x, y, z, temperature, rainfall, salty) -> {
        if (rainfall > 300f && temperature > 15f)
        {
            return TFCBlocks.SANDSTONE.get(SandBlockType.PINK).get(SandstoneBlockType.RAW).get().defaultBlockState();
        }
        else if (rainfall > 300f)
        {
            return TFCBlocks.SANDSTONE.get(SandBlockType.BLACK).get(SandstoneBlockType.RAW).get().defaultBlockState();
        }
        else
        {
            return TFCBlocks.SANDSTONE.get(rockData.getTopRock(x, z).getBeachSandColor()).get(SandstoneBlockType.RAW).get().defaultBlockState();
        }
    };

    public static final ISurfaceState WATER = (rockData, x, y, z, temperature, rainfall, salty) -> salty ? TFCFluids.SALT_WATER.getSourceBlock() : Fluids.WATER.defaultFluidState().createLegacyBlock();
}
