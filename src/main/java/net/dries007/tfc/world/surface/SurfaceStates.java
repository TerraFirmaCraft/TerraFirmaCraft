/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface;

import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.common.blocks.SandstoneBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Helpers;

public final class SurfaceStates
{
    /**
     * Simple surface states that may be used by climate sensitive states
     */
    public static final SurfaceState RAW = context -> context.getRock().raw().defaultBlockState();
    public static final SurfaceState COBBLE = context -> context.getRock().cobble().defaultBlockState();
    public static final SurfaceState GRAVEL = context -> context.getRock().gravel().defaultBlockState();
    public static final SurfaceState SAND = context -> context.getRock().sand().defaultBlockState();
    public static final SurfaceState SANDSTONE = context -> context.getRock().sandstone().defaultBlockState();

    public static final SurfaceState BASALT = context -> TFCBlocks.ROCK_BLOCKS.get(Rock.BASALT).get(Rock.BlockType.RAW).get().defaultBlockState();
    public static final SurfaceState BASALT_COBBLE = context -> TFCBlocks.ROCK_BLOCKS.get(Rock.BASALT).get(Rock.BlockType.COBBLE).get().defaultBlockState();
    public static final SurfaceState BASALT_GRAVEL = context -> TFCBlocks.ROCK_BLOCKS.get(Rock.BASALT).get(Rock.BlockType.GRAVEL).get().defaultBlockState();
    public static final SurfaceState BASALT_MORAINE = context -> (Helpers.hash(729375982L, context.pos()) & 127) > 96 ?
        TFCBlocks.ROCK_BLOCKS.get(Rock.BASALT).get(Rock.BlockType.COBBLE).get().defaultBlockState() :
        TFCBlocks.ROCK_BLOCKS.get(Rock.BASALT).get(Rock.BlockType.GRAVEL).get().defaultBlockState();

    public static final SurfaceState TUFF = context -> TFCBlocks.ROCK_BLOCKS.get(Rock.TUFF).get(Rock.BlockType.RAW).get().defaultBlockState();
    public static final SurfaceState TUFF_GRAVEL = context -> TFCBlocks.ROCK_BLOCKS.get(Rock.TUFF).get(Rock.BlockType.GRAVEL).get().defaultBlockState();

    public static final SurfaceState MORAINE = context -> (Helpers.hash(729375982L, context.pos()) & 127) > 96 ?
        context.getRock().cobble().defaultBlockState() : context.getRock().gravel().defaultBlockState();
    public static final SurfaceState SAND_AND_GRAVEL = context -> (Helpers.hash(728275914L, context.pos()) & 127) > 48 ?
        context.getRock().sand().defaultBlockState() : context.getRock().gravel().defaultBlockState();

    public static final SurfaceState PACKED_ICE = context -> Blocks.PACKED_ICE.defaultBlockState();
    public static final SurfaceState BLUE_ICE = context -> Blocks.BLUE_ICE.defaultBlockState();
    public static final SurfaceState SNOW = context -> Blocks.SNOW_BLOCK.defaultBlockState();

    public static final SurfaceState COARSE_SANDY_LOAM_BASE = SoilSurfaceState.soil(SoilBlockType.COARSE_DIRT, SoilBlockType.Variant.SANDY_LOAM);
    public static final SurfaceState DRY_MUD = context -> TFCBlocks.HARDENED_CLAY.get().defaultBlockState();
    public static final SurfaceState SALTED_EARTH = context -> TFCBlocks.HALITE.get().defaultBlockState();

    public static final SurfaceState RIVER_SAND = context -> context.getSeaLevelRock().sand().defaultBlockState();
    public static final SurfaceState SHORE_SAND = context -> context.getBottomRock().sand().defaultBlockState();
    public static final SurfaceState SHORE_SANDSTONE = context -> context.getBottomRock().sandstone().defaultBlockState();
    public static final SurfaceState SHORE_MUD = context -> TFCBlocks.SOIL.get(SoilBlockType.MUD).get(SoilBlockType.Variant.SANDY_LOAM).get().defaultBlockState();

    /**
     * Default surface builders, Climate sensitive
     */
    public static final SurfaceState TOP_GRASS_TO_GRAVEL = SoilSurfaceState.buildSurfaceType(SoilBlockType.GRASS, SurfaceStates.GRAVEL);
    public static final SurfaceState TOP_GRASS_TO_SAND = SoilSurfaceState.buildSurfaceType(SoilBlockType.GRASS, SurfaceStates.SAND);
    public static final SurfaceState MID_DIRT_TO_GRAVEL = SoilSurfaceState.buildMidType(SoilBlockType.DIRT, SurfaceStates.GRAVEL);
    public static final SurfaceState MID_DIRT_TO_SAND = SoilSurfaceState.buildMidType(SoilBlockType.DIRT, SurfaceStates.SAND);
    public static final SurfaceState VOLCANIC_TOP_GRASS_TO_GRAVEL = SoilSurfaceState.buildSurfaceType(SoilBlockType.GRASS, SurfaceStates.BASALT_GRAVEL);
    public static final SurfaceState VOLCANIC_MID_DIRT_TO_GRAVEL = SoilSurfaceState.buildMidType(SoilBlockType.DIRT, SurfaceStates.BASALT_GRAVEL);

    public static final SurfaceState UNDER_GRAVEL = SoilSurfaceState.buildUnderType();

    public static final SurfaceState MUD = SoilSurfaceState.buildSurfaceType(SoilBlockType.MUD, SurfaceStates.GRAVEL);

    /**
     * Snowy surface builders - used when a SurfaceState should be replaced by snow blocks in the appropriate climate
     */
    public static final SurfaceState SNOWY_RAW = SoilSurfaceState.buildSnowableSurface(SNOW, RAW);
    public static final SurfaceState SNOWY_COBBLE = SoilSurfaceState.buildSnowableSurface(SNOW, COBBLE);
    public static final SurfaceState SNOWY_GRAVEL = SoilSurfaceState.buildSnowableSurface(SNOW, GRAVEL);
    public static final SurfaceState SNOWY_SAND = SoilSurfaceState.buildSnowableSurface(SNOW, SAND);
    public static final SurfaceState SNOWY_SANDSTONE = SoilSurfaceState.buildSnowableSurface(SNOW, SANDSTONE);

    public static final SurfaceState SNOWY_MORAINE = SoilSurfaceState.buildSnowableSurface(SNOW, MORAINE);
    public static final SurfaceState SNOWY_BASALT = SoilSurfaceState.buildSnowableSurface(SNOW, BASALT);
    public static final SurfaceState SNOWY_BASALT_COBBLE = SoilSurfaceState.buildSnowableSurface(SNOW, BASALT_COBBLE);
    public static final SurfaceState SNOWY_BASALT_GRAVEL = SoilSurfaceState.buildSnowableSurface(SNOW, BASALT_GRAVEL);
    public static final SurfaceState SNOWY_BASALT_MORAINE = SoilSurfaceState.buildSnowableSurface(SNOW, BASALT_MORAINE);
    public static final SurfaceState SNOWY_SAND_AND_GRAVEL = SoilSurfaceState.buildSnowableSurface(SNOW, SAND_AND_GRAVEL);


    public static final SurfaceState RARE_SHORE_SAND = new SurfaceState()
    {
        private final Supplier<Block> pinkSand = TFCBlocks.SAND.get(SandBlockType.PINK);
        private final Supplier<Block> blackSand = TFCBlocks.SAND.get(SandBlockType.BLACK);

        @Override
        public BlockState getState(SurfaceBuilderContext context)
        {
            if (context.groundWater() > 300f && context.averageTemperature() > 15f)
            {
                return pinkSand.get().defaultBlockState();
            }
            else if (context.groundWater() > 300f)
            {
                return blackSand.get().defaultBlockState();
            }
            else
            {
                return context.getBottomRock().sand().defaultBlockState();
            }
        }
    };

    public static final SurfaceState RARE_SHORE_SANDSTONE = new SurfaceState()
    {
        private final Supplier<Block> pinkSandstone = TFCBlocks.SANDSTONE.get(SandBlockType.PINK).get(SandstoneBlockType.RAW);
        private final Supplier<Block> blackSandstone = TFCBlocks.SANDSTONE.get(SandBlockType.BLACK).get(SandstoneBlockType.RAW);

        @Override
        public BlockState getState(SurfaceBuilderContext context)
        {
            if (context.groundWater() > 300f && context.averageTemperature() > 15f)
            {
                return pinkSandstone.get().defaultBlockState();
            }
            else if (context.groundWater() > 300f)
            {
                return blackSandstone.get().defaultBlockState();
            }
            else
            {
                return context.getBottomRock().sandstone().defaultBlockState();
            }
        }
    };

    public static final SurfaceState WATER = context -> context.salty() ?
        TFCFluids.SALT_WATER.createSourceBlock() :
        Fluids.WATER.defaultFluidState().createLegacyBlock();
}
