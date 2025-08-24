/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
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
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

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

    public static final SurfaceState COARSE_ARIDISOL_BASE = SoilSurfaceState.soil(SoilBlockType.COARSE_DIRT, SoilBlockType.Variant.ARIDISOL);
    public static final SurfaceState COARSE_ANDISOL_BASE = SoilSurfaceState.soil(SoilBlockType.COARSE_DIRT, SoilBlockType.Variant.ANDISOL);
    public static final SurfaceState DRY_MUD = context -> TFCBlocks.HARDENED_CLAY.get().defaultBlockState();
    public static final SurfaceState SALTED_EARTH = context -> TFCBlocks.HALITE.get().defaultBlockState();

    public static final SurfaceState RIVER_SAND = context -> context.getSeaLevelRock().sand().defaultBlockState();
    public static final SurfaceState YELLOW_SAND = context -> TFCBlocks.SAND.get(SandBlockType.YELLOW).get().defaultBlockState();
    public static final SurfaceState YELLOW_SANDSTONE = context -> TFCBlocks.SANDSTONE.get(SandBlockType.YELLOW).get(SandstoneBlockType.RAW).get().defaultBlockState();
    public static final SurfaceState RED_SAND = context -> TFCBlocks.SAND.get(SandBlockType.RED).get().defaultBlockState();
    public static final SurfaceState RED_SANDSTONE = context -> TFCBlocks.SANDSTONE.get(SandBlockType.RED).get(SandstoneBlockType.RAW).get().defaultBlockState();
    public static final SurfaceState BROWN_SAND = context -> TFCBlocks.SAND.get(SandBlockType.BROWN).get().defaultBlockState();
    public static final SurfaceState BROWN_SANDSTONE = context -> TFCBlocks.SANDSTONE.get(SandBlockType.BROWN).get(SandstoneBlockType.RAW).get().defaultBlockState();
    public static final SurfaceState WHITE_SAND = context -> TFCBlocks.SAND.get(SandBlockType.WHITE).get().defaultBlockState();
    public static final SurfaceState WHITE_SANDSTONE = context -> TFCBlocks.SANDSTONE.get(SandBlockType.WHITE).get(SandstoneBlockType.RAW).get().defaultBlockState();
    public static final SurfaceState BLACK_SAND = context -> TFCBlocks.SAND.get(SandBlockType.BLACK).get().defaultBlockState();
    public static final SurfaceState BLACK_SANDSTONE = context -> TFCBlocks.SANDSTONE.get(SandBlockType.BLACK).get(SandstoneBlockType.RAW).get().defaultBlockState();
    public static final SurfaceState GREEN_SAND = context -> TFCBlocks.SAND.get(SandBlockType.GREEN).get().defaultBlockState();
    public static final SurfaceState GREEN_SANDSTONE = context -> TFCBlocks.SANDSTONE.get(SandBlockType.GREEN).get(SandstoneBlockType.RAW).get().defaultBlockState();
    public static final SurfaceState PINK_SAND = context -> TFCBlocks.SAND.get(SandBlockType.PINK).get().defaultBlockState();
    public static final SurfaceState PINK_SANDSTONE = context -> TFCBlocks.SANDSTONE.get(SandBlockType.PINK).get(SandstoneBlockType.RAW).get().defaultBlockState();

    public static final SurfaceState SHORE_MUD = context -> TFCBlocks.SOIL.get(SoilBlockType.MUD).get(SoilBlockType.Variant.ENTISOL).get().defaultBlockState();

    /**
     * Default surface builders, Climate sensitive
     */
    public static final SurfaceState TOP_GRASS_TO_GRAVEL = SoilSurfaceState.buildSurfaceType(SoilBlockType.GRASS, SurfaceStates.GRAVEL);
    public static final SurfaceState TOP_GRASS_TO_SAND = SoilSurfaceState.buildSurfaceType(SoilBlockType.GRASS, SurfaceStates.SAND);
    public static final SurfaceState MID_DIRT_TO_GRAVEL = SoilSurfaceState.buildMidType(SoilBlockType.DIRT, SurfaceStates.GRAVEL);
    public static final SurfaceState MID_DIRT_TO_SAND = SoilSurfaceState.buildMidType(SoilBlockType.DIRT, SurfaceStates.SAND);
    public static final SurfaceState VOLCANIC_TOP_GRASS_TO_GRAVEL = SoilSurfaceState.buildVolcanicSurfaceType(SoilBlockType.GRASS, SurfaceStates.BASALT_GRAVEL);
    public static final SurfaceState VOLCANIC_MID_DIRT_TO_GRAVEL = SoilSurfaceState.buildVolcanicMidType(SoilBlockType.DIRT, SurfaceStates.BASALT_GRAVEL);
    public static final SurfaceState VOLCANIC_TOP_GRASS_TO_LOCAL_GRAVEL = SoilSurfaceState.buildVolcanicSurfaceType(SoilBlockType.GRASS, SurfaceStates.GRAVEL);
    public static final SurfaceState VOLCANIC_MID_DIRT_TO_LOCAL_GRAVEL = SoilSurfaceState.buildVolcanicMidType(SoilBlockType.DIRT, SurfaceStates.GRAVEL);

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

    /**
     * Shore surface builders - used for beaches
     */

    /**
     * Selected rarely by the shore sand SurfaceState, this defaults to white sand unless certain climatic requirements are met
     */
    public static final SurfaceState RARE_SHORE_SAND = new SurfaceState()
    {
        private final Supplier<Block> pinkSand = TFCBlocks.SAND.get(SandBlockType.PINK);
        private final Supplier<Block> greenSand = TFCBlocks.SAND.get(SandBlockType.GREEN);
        private final Supplier<Block> blackSand = TFCBlocks.SAND.get(SandBlockType.BLACK);
        private final Supplier<Block> whiteSand = TFCBlocks.SAND.get(SandBlockType.WHITE);

        @Override
        public BlockState getState(SurfaceBuilderContext context)
        {
            if (context.groundWater() > 300f && context.averageTemperature() > 15f)
            {
                return pinkSand.get().defaultBlockState();
            }
            else if ((context.getSeaLevelRock().mafic().isPresent() ? context.getSeaLevelRock().mafic().get() : false))
            {
                if (context.groundWater() > 300f)
                {
                    return greenSand.get().defaultBlockState();
                }
                return blackSand.get().defaultBlockState();
            }
            else
            {
                return whiteSand.get().defaultBlockState();
            }
        }
    };

    /**
     * Similar to rare shore sand, but forces volcanic types and green sand is rarer
     */
    public static final SurfaceState VOLCANIC_SHORE_SAND = new SurfaceState()
    {
        private final Supplier<Block> greenSand = TFCBlocks.SAND.get(SandBlockType.GREEN);
        private final Supplier<Block> blackSand = TFCBlocks.SAND.get(SandBlockType.BLACK);

        @Override
        public BlockState getState(SurfaceBuilderContext context)
        {
            if (context.groundWater() > 420f)
            {
                return greenSand.get().defaultBlockState();
            }
            return blackSand.get().defaultBlockState();
        }
    };

    /**
     * Selects between three common sand types or the rare type based on absolute-value noise map
     */
    public static final SurfaceState SHORE_SAND = new SurfaceState()
    {
        private final Supplier<Block> redSand = TFCBlocks.SAND.get(SandBlockType.RED);
        private final Supplier<Block> brownSand = TFCBlocks.SAND.get(SandBlockType.BROWN);
        private final Supplier<Block> yellowSand = TFCBlocks.SAND.get(SandBlockType.YELLOW);

        @Override
        public BlockState getState(SurfaceBuilderContext context)
        {
            final BlockPos pos = context.pos();
            final int x = pos.getX();
            final int z = pos.getZ();
            final float variantNoiseValue = (float) sandVariantNoise().noise(x, z);
            if (variantNoiseValue > 0.55) return RARE_SHORE_SAND.getState(context);
            else if (variantNoiseValue > 0.2) return yellowSand.get().defaultBlockState();
            else if (variantNoiseValue > 0.1) return brownSand.get().defaultBlockState();
            else return redSand.get().defaultBlockState();
        }
    };

    /**
     * Selects between placing shore sands or gravel, with gravel more common in cold climates and sand more common in warm climates
     */
    public static final SurfaceState SHORE_SURFACE = new SurfaceState()
    {
        @Override
        public BlockState getState(SurfaceBuilderContext context)
        {
            final BlockPos pos = context.pos();
            final int x = pos.getX();
            final int z = pos.getZ();
            final float variantNoiseValue = (float) sandGravelBeachNoise().noise(x, z);
            final double gravelCutoff = Mth.clampedMap(context.averageTemperature(), -15, 25, -0.7, 0.7);
            return (variantNoiseValue > gravelCutoff ? GRAVEL : SHORE_SAND).getState(context);
        }
    };

    public static final SurfaceState RARE_SHORE_SANDSTONE = new SurfaceState()
    {
        private final Supplier<Block> pinkSandstone = TFCBlocks.SANDSTONE.get(SandBlockType.PINK).get(SandstoneBlockType.RAW);
        private final Supplier<Block> greenSandstone = TFCBlocks.SANDSTONE.get(SandBlockType.GREEN).get(SandstoneBlockType.RAW);
        private final Supplier<Block> blackSandstone = TFCBlocks.SANDSTONE.get(SandBlockType.BLACK).get(SandstoneBlockType.RAW);
        private final Supplier<Block> whiteSandstone = TFCBlocks.SANDSTONE.get(SandBlockType.WHITE).get(SandstoneBlockType.RAW);

        @Override
        public BlockState getState(SurfaceBuilderContext context)
        {
            if (context.groundWater() > 300f && context.averageTemperature() > 15f)
            {
                return pinkSandstone.get().defaultBlockState();
            }
            else if ((context.getSeaLevelRock().mafic().isPresent() ? context.getSeaLevelRock().mafic().get() : false))
            {
                if (context.groundWater() > 300f)
                {
                    return greenSandstone.get().defaultBlockState();
                }
                return blackSandstone.get().defaultBlockState();
            }
            else
            {
                return whiteSandstone.get().defaultBlockState();
            }
        }
    };

    public static final SurfaceState VOLCANIC_SHORE_SANDSTONE = new SurfaceState()
    {
        private final Supplier<Block> greenSandstone = TFCBlocks.SANDSTONE.get(SandBlockType.GREEN).get(SandstoneBlockType.RAW);
        private final Supplier<Block> blackSandstone = TFCBlocks.SANDSTONE.get(SandBlockType.BLACK).get(SandstoneBlockType.RAW);

        @Override
        public BlockState getState(SurfaceBuilderContext context)
        {
            if (context.groundWater() > 420f)
            {
                return greenSandstone.get().defaultBlockState();
            }
            return blackSandstone.get().defaultBlockState();
        }
    };

    public static final SurfaceState SHORE_SANDSTONE = new SurfaceState()
    {
        private final Supplier<Block> redSandstone = TFCBlocks.SANDSTONE.get(SandBlockType.RED).get(SandstoneBlockType.RAW);
        private final Supplier<Block> brownSandstone = TFCBlocks.SANDSTONE.get(SandBlockType.BROWN).get(SandstoneBlockType.RAW);
        private final Supplier<Block> yellowSandstone = TFCBlocks.SANDSTONE.get(SandBlockType.YELLOW).get(SandstoneBlockType.RAW);

        @Override
        public BlockState getState(SurfaceBuilderContext context)
        {
            final BlockPos pos = context.pos();
            final int x = pos.getX();
            final int z = pos.getZ();
            final float variantNoiseValue = (float) sandVariantNoise().noise(x, z);
            if (variantNoiseValue > 0.8) return RARE_SHORE_SANDSTONE.getState(context);
            else if (variantNoiseValue > 0.4) return yellowSandstone.get().defaultBlockState();
            else if (variantNoiseValue > 0.2) return redSandstone.get().defaultBlockState();
            else return brownSandstone.get().defaultBlockState();
        }
    };

    public static final SurfaceState SHORE_UNDERLAYER = new SurfaceState()
    {
        @Override
        public BlockState getState(SurfaceBuilderContext context)
        {
            final BlockPos pos = context.pos();
            final int x = pos.getX();
            final int z = pos.getZ();
            final float variantNoiseValue = (float) sandGravelBeachNoise().noise(x, z);
            final double gravelCutoff = Mth.clampedMap(context.averageTemperature(), -15, 25, -0.7, 0.7);
            return (variantNoiseValue > gravelCutoff ? RAW : SHORE_SANDSTONE).getState(context);
        }
    };

    public static final SurfaceState TOP_GRASS_TO_SHORE_SAND = SoilSurfaceState.buildSurfaceType(SoilBlockType.GRASS, SurfaceStates.SHORE_SAND);
    public static final SurfaceState MID_DIRT_TO_SHORE_SAND = SoilSurfaceState.buildMidType(SoilBlockType.DIRT, SurfaceStates.SHORE_SAND);
    public static final SurfaceState VOLCANIC_TOP_GRASS_TO_SHORE_SAND = SoilSurfaceState.buildVolcanicSurfaceType(SoilBlockType.GRASS, SurfaceStates.SHORE_SAND);
    public static final SurfaceState VOLCANIC_MID_DIRT_TO_SHORE_SAND = SoilSurfaceState.buildVolcanicMidType(SoilBlockType.DIRT, SurfaceStates.SHORE_SAND);

    public static final SurfaceState WATER = context -> context.salty() ?
        TFCFluids.SALT_WATER.createSourceBlock() :
        Fluids.WATER.defaultFluidState().createLegacyBlock();

    public static Noise2D sandVariantNoise()
    {
        return new OpenSimplex2D(36263276L).octaves(5).spread(0.0003f).abs();
    }

    public static Noise2D sandGravelBeachNoise()
    {
        return new OpenSimplex2D(124154L).octaves(3).spread(0.00002f);
    }
}
