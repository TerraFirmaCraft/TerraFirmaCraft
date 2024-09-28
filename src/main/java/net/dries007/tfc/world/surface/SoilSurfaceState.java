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
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

public class SoilSurfaceState implements SurfaceState
{
    public static final Noise2D PATCH_NOISE = new OpenSimplex2D(18273952837592L).octaves(2).spread(0.04f);

    public static SurfaceState buildType(SoilBlockType type)
    {
        final ImmutableList<SurfaceState> regions = ImmutableList.of(
            sand(),
            transition(sand(), soil(type, SoilBlockType.Variant.SANDY_LOAM)),
            soil(type, SoilBlockType.Variant.SANDY_LOAM),
            transition(soil(type, SoilBlockType.Variant.SANDY_LOAM), soil(type, SoilBlockType.Variant.LOAM)),
            soil(type, SoilBlockType.Variant.LOAM),
            transition(soil(type, SoilBlockType.Variant.LOAM), soil(type, SoilBlockType.Variant.SILTY_LOAM)),
            soil(type, SoilBlockType.Variant.SILTY_LOAM),
            transition(soil(type, SoilBlockType.Variant.SILTY_LOAM), soil(type, SoilBlockType.Variant.SILT)),
            soil(type, SoilBlockType.Variant.SILT)
        );
        return type == SoilBlockType.GRASS ? new SoilSurfaceState.NeedsPostProcessing(regions) : new SoilSurfaceState(regions);
    }

    public static SurfaceState buildDryDirt(SoilBlockType type)
    {
        final ImmutableList<SurfaceState> regions = ImmutableList.of(
            soil(type, SoilBlockType.Variant.SANDY_LOAM),
            transition(soil(type, SoilBlockType.Variant.SANDY_LOAM), soil(type, SoilBlockType.Variant.LOAM)),
            soil(type, SoilBlockType.Variant.LOAM),
            transition(soil(type, SoilBlockType.Variant.LOAM), soil(type, SoilBlockType.Variant.SILTY_LOAM)),
            soil(type, SoilBlockType.Variant.SILTY_LOAM),
            transition(soil(type, SoilBlockType.Variant.SILTY_LOAM), soil(type, SoilBlockType.Variant.SILT)),
            soil(type, SoilBlockType.Variant.SILT)
        );
        return new SoilSurfaceState(regions);
    }

    public static SurfaceState buildSandOrGravel(boolean sandIsSandstone)
    {
        final SurfaceState sand = sandIsSandstone ? sandstone() : sand();
        final SurfaceState gravel = gravel();
        return new SoilSurfaceState(ImmutableList.of(
            sand,
            transition(sand, gravel),
            gravel,
            gravel,
            gravel,
            gravel,
            gravel,
            gravel,
            gravel
        ));
    }

    public static SurfaceState buildSand(boolean hasSandstone)
    {
        final SurfaceState sand = sand();
        final SurfaceState sandstone = hasSandstone ? sandstone() : sand();
        return new SoilSurfaceState(ImmutableList.of(
            sand,
            sand,
            sand,
            sand,
            sand,
            transition(sand, sandstone),
            sandstone,
            sandstone,
            sandstone
        ));
    }

    private static SurfaceState transition(SurfaceState first, SurfaceState second)
    {
        return context -> {
            final BlockPos pos = context.pos();
            final double noise = PATCH_NOISE.noise(pos.getX(), pos.getZ());
            return noise > 0 ? first.getState(context) : second.getState(context);
        };
    }

    private static SurfaceState sand()
    {
        return context -> context.getRock().sand().defaultBlockState();
    }

    private static SurfaceState sandstone()
    {
        return context -> context.getRock().sandstone().defaultBlockState();
    }

    private static SurfaceState gravel()
    {
        return context -> context.getRock().gravel().defaultBlockState();
    }

    private static SurfaceState soil(SoilBlockType type, SoilBlockType.Variant variant)
    {
        final Supplier<Block> block = TFCBlocks.SOIL.get(type).get(variant);
        return context -> block.get().defaultBlockState();
    }

    private final List<SurfaceState> regions;

    private SoilSurfaceState(List<SurfaceState> regions)
    {
        this.regions = regions;
    }

    @Override
    public BlockState getState(SurfaceBuilderContext context)
    {
        // Bias a little towards sand regions
        // Without: pure sand < 55mm, mixed sand < 110mm. With: pure sand < 73mm, mixed sand < 126mm
        final float rainfall = context.groundwater();
        final int index = (int) Mth.clampedMap(rainfall, 20, 500, 0, regions.size() - 0.01f);

        return regions.get(index).getState(context);
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
