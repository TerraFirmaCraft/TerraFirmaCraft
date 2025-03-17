/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.common.blocks.SandstoneBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeNoise;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.settings.RockSettings;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceStates;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

import static net.dries007.tfc.world.TFCChunkGenerator.SEA_LEVEL_Y;
import static net.dries007.tfc.world.surface.SurfaceStates.DRY_MUD;
import static net.dries007.tfc.world.surface.SurfaceStates.MUD;

public class RockyPlateauSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = seed -> new RockyPlateauSurfaceBuilder(seed);

    private final long seed;

    public RockyPlateauSurfaceBuilder(Seed seed)
    {
        this.seed = seed.seed();
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final double weight = context.weight();
        final NormalSurfaceBuilder surfaceBuilder = NormalSurfaceBuilder.ROCKY;
        if (weight > 0.9 && startY < 86 && context.baseGroundwater() == 0)
        {
            surfaceBuilder.buildSurface(context, startY, endY, SurfaceStates.SALTED_EARTH, SurfaceStates.DRY_MUD, SurfaceStates.RAW);
        }
        else if (startY - 2 > BiomeNoise.hills(seed, 22, 32).noise(context.pos().getX(), context.pos().getZ()))
        {
            surfaceBuilder.buildSurface(context, startY, endY, SurfaceStates.RAW, SurfaceStates.RAW, SurfaceStates.RAW);
        }
        else
        {
            surfaceBuilder.buildSurface(context, startY, endY, SurfaceStates.TOP_GRASS_TO_SAND, SurfaceStates.MID_DIRT_TO_SAND, SurfaceStates.RAW);
        }
    }
}