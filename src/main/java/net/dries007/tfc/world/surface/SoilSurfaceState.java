/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface;

import java.util.List;
import java.util.function.Supplier;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

public class SoilSurfaceState implements SurfaceState
{
    public static final Noise2D PATCH_NOISE = new OpenSimplex2D(18273952837592L).octaves(2).spread(0.04f);

    public static SurfaceState buildSnowableSurface(SurfaceState snow, SurfaceState typical)
    {
        final ImmutableList<SurfaceState> regions = ImmutableList.of(
            snow,
            snow,
            transition(snow, typical),
            typical,
            typical,
            typical,
            typical,
            typical,
            typical,
            typical,
            typical,
            typical,
            typical,
            typical,
            typical,
            typical,
            typical,
            typical
        );
        return new SoilSurfaceState(regions);
    }

    public static SurfaceState buildSurfaceType(SoilBlockType type, SurfaceState dry)
    {
        final ImmutableList<SurfaceState> regions = ImmutableList.of(
            SurfaceStates.SNOW,
            SurfaceStates.SNOW,
            transition(SurfaceStates.SNOW, dry),
            dry,
            transition(dry, SurfaceStates.COARSE_ARIDISOL_BASE),
            SurfaceStates.COARSE_ARIDISOL_BASE,
            transition(SurfaceStates.COARSE_ARIDISOL_BASE, soil(type, SoilBlockType.Variant.ARIDISOL)),
            soil(type, SoilBlockType.Variant.ARIDISOL),
            soil(type, SoilBlockType.Variant.ARIDISOL),
            blobTransition(soil(type, SoilBlockType.Variant.ARIDISOL), transitioningSoil(type)),
            transitioningSoil(type),
            transitioningSoil(type),
            transitioningSoil(type),
            transitioningSoil(type),
            transitioningSoil(type),
            transitioningSoil(type),
            transitioningSoil(type),
            transitioningSoil(type)
        );
        return type == SoilBlockType.GRASS ? new SoilSurfaceState.NeedsPostProcessing(regions) : new SoilSurfaceState(regions);
    }

    public static SurfaceState buildVolcanicSurfaceType(SoilBlockType type, SurfaceState dry)
    {
        final ImmutableList<SurfaceState> regions = ImmutableList.of(
            SurfaceStates.SNOW,
            SurfaceStates.SNOW,
            transition(SurfaceStates.SNOW, dry),
            dry,
            transition(dry, SurfaceStates.COARSE_ANDISOL_BASE),
            SurfaceStates.COARSE_ANDISOL_BASE,
            transition(SurfaceStates.COARSE_ANDISOL_BASE, soil(type, SoilBlockType.Variant.ANDISOL)),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL)
        );
        return type == SoilBlockType.GRASS ? new SoilSurfaceState.NeedsPostProcessing(regions) : new SoilSurfaceState(regions);
    }

    public static SurfaceState buildMidType(SoilBlockType type, SurfaceState dry)
    {
        final ImmutableList<SurfaceState> regions = ImmutableList.of(
            SurfaceStates.PACKED_ICE,
            blobTransition(SurfaceStates.PACKED_ICE, dry),
            dry,
            dry,
            transition(dry, SurfaceStates.COARSE_ARIDISOL_BASE),
            SurfaceStates.COARSE_ARIDISOL_BASE,
            transition(SurfaceStates.COARSE_ARIDISOL_BASE, soil(type, SoilBlockType.Variant.ARIDISOL)),
            soil(type, SoilBlockType.Variant.ARIDISOL),
            soil(type, SoilBlockType.Variant.ARIDISOL),
            blobTransition(soil(type, SoilBlockType.Variant.ARIDISOL), transitioningSoil(type)),
            transitioningSoil(type),
            transitioningSoil(type),
            transitioningSoil(type),
            transitioningSoil(type),
            transitioningSoil(type),
            transitioningSoil(type),
            transitioningSoil(type),
            transitioningSoil(type)
        );
        return type == SoilBlockType.GRASS ? new SoilSurfaceState.NeedsPostProcessing(regions) : new SoilSurfaceState(regions);
    }

    public static SurfaceState buildVolcanicMidType(SoilBlockType type, SurfaceState dry)
    {
        final ImmutableList<SurfaceState> regions = ImmutableList.of(
            SurfaceStates.PACKED_ICE,
            blobTransition(SurfaceStates.PACKED_ICE, dry),
            dry,
            dry,
            transition(dry, SurfaceStates.COARSE_ANDISOL_BASE),
            SurfaceStates.COARSE_ANDISOL_BASE,
            transition(SurfaceStates.COARSE_ANDISOL_BASE, soil(type, SoilBlockType.Variant.ANDISOL)),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL),
            soil(type, SoilBlockType.Variant.ANDISOL)
        );
        return type == SoilBlockType.GRASS ? new SoilSurfaceState.NeedsPostProcessing(regions) : new SoilSurfaceState(regions);
    }

    public static SurfaceState buildUnderType()
    {
        final ImmutableList<SurfaceState> regions = ImmutableList.of(
            SurfaceStates.RAW,
            SurfaceStates.RAW,
            blobTransition(SurfaceStates.RAW, SurfaceStates.GRAVEL),
            SurfaceStates.GRAVEL,
            SurfaceStates.GRAVEL,
            SurfaceStates.GRAVEL,
            SurfaceStates.GRAVEL,
            SurfaceStates.GRAVEL,
            SurfaceStates.GRAVEL,
            SurfaceStates.GRAVEL,
            SurfaceStates.GRAVEL,
            SurfaceStates.GRAVEL,
            SurfaceStates.GRAVEL,
            SurfaceStates.GRAVEL,
            SurfaceStates.GRAVEL,
            SurfaceStates.GRAVEL,
            SurfaceStates.GRAVEL,
            SurfaceStates.GRAVEL
        );
        return new SoilSurfaceState(regions);
    }

    public static SurfaceState buildDryDirt(SoilBlockType type)
    {
        final ImmutableList<SurfaceState> regions = ImmutableList.of(
            SurfaceStates.SNOW,
            SurfaceStates.SNOW,
            transition(SurfaceStates.SNOW, soil(type, SoilBlockType.Variant.ARIDISOL)),
            soil(type, SoilBlockType.Variant.ARIDISOL),
            soil(type, SoilBlockType.Variant.ARIDISOL),
            soil(type, SoilBlockType.Variant.ARIDISOL),
            soil(type, SoilBlockType.Variant.ARIDISOL),
            soil(type, SoilBlockType.Variant.ARIDISOL),
            soil(type, SoilBlockType.Variant.ARIDISOL),
            blobTransition(soil(type, SoilBlockType.Variant.ARIDISOL), transitioningSoil(type)),
            transitioningSoil(type),
            transitioningSoil(type),
            transitioningSoil(type),
            transitioningSoil(type),
            transitioningSoil(type),
            transitioningSoil(type),
            transitioningSoil(type),
            transitioningSoil(type)
        );
        return new SoilSurfaceState(regions);
    }

    public static SurfaceState soil(SoilBlockType type, SoilBlockType.Variant variant)
    {
        final Supplier<Block> block = TFCBlocks.SOIL.get(type).get(variant);
        return context -> block.get().defaultBlockState();
    }

    public static SurfaceState transitioningSoil(SoilBlockType type)
    {
        return transitioningSoil(type, SoilBlockType.Variant.ENTISOL, SoilBlockType.Variant.OXISOL, 16f, 16.7f);
    }

    public static SurfaceState transitioningSoil(SoilBlockType blockType, SoilBlockType.Variant coldSoilType, SoilBlockType.Variant hotSoilType, float transitionStartTemp, float transitionEndTemp)
    {
        return context -> {
            // First, check if near a "flooding" river, and place silt if so
            if (context.baseGroundwater() > 25 && Math.abs(context.rainVariance()) > 0.5)
            {
                return TFCBlocks.SOIL.get(blockType).get(SoilBlockType.Variant.FLUVISOL).get().defaultBlockState();
            }
            // Then run through the temperature calculations
            final float temp = context.averageTemperature();
            final BlockState coldBlock = TFCBlocks.SOIL.get(blockType).get(coldSoilType).get().defaultBlockState();
            if (temp < transitionStartTemp)
            {
                return coldBlock;
            }
            final BlockState hotBlock = TFCBlocks.SOIL.get(blockType).get(hotSoilType).get().defaultBlockState();
            if (temp > transitionEndTemp)
            {
                return hotBlock;
            }
            final BlockPos pos = context.pos();
            final double noise = PATCH_NOISE.noise(pos.getX(), pos.getZ());
            return noise > 0 ? hotBlock : coldBlock;
        };
    }

    private static SurfaceState transition(SurfaceState first, SurfaceState second)
    {
        return context -> (Helpers.hash(729375982L, context.pos()) & 127) > 63 ?
            first.getState(context) : second.getState(context);
    }

    private static SurfaceState blobTransition(SurfaceState first, SurfaceState second)
    {
        return context -> {
            final BlockPos pos = context.pos();
            final double noise = PATCH_NOISE.noise(pos.getX(), pos.getZ());
            return noise > 0 ? first.getState(context) : second.getState(context);
        };
    }

    private final List<SurfaceState> regions;

    private SoilSurfaceState(List<SurfaceState> regions)
    {
        this.regions = regions;
    }

    @Override
    public BlockState getState(SurfaceBuilderContext context)
    {
        final float rainfall = context.groundWater();
        final float temperature = Helpers.adjustAverageTemperatureByElevation(context.pos().getY(), context.averageTemperature(), context.getSeaLevel());

        // Rain-controlled surface: <64 pure gravel, <91 mixed gravel/dirt, <118 dirt, <145 mixed dirt/grass, otherwise grass
        final int rainIndex = (int) Mth.clampedMap(rainfall, 35, 450, 3, regions.size() - 0.01f);

        // Temperature-controlled surface: <-17.4 pure snow, <-16.6 mixed gravel/snow <-15.7 pure gravel, <-15 mixed gravel/dirt, <14.1, <-13.2 mixed dirt/grass, otherwise grass
        // -17c = Koppen EF/ET Border
        // -12c = Koppen ET Border
        final int tempIndex = (int) Mth.clampedMap(temperature, -19, -4, 0, regions.size() - 0.01f);

        return regions.get(Math.min(rainIndex, tempIndex)).getState(context);
    }

    static class NeedsPostProcessing extends SoilSurfaceState
    {
        private NeedsPostProcessing(List<SurfaceState> regions)
        {
            super(regions);
        }

        @Override
        public void setState(SurfaceBuilderContext context)
        {
            context.chunk().setBlockState(context.pos(), getState(context), false);
            context.chunk().markPosForPostprocessing(context.pos());
        }
    }
}
