/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeNoise;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public class ShoreAndOceanSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory NORMAL = seed -> new ShoreAndOceanSurfaceBuilder(seed, SurfaceStates.SHORE_SURFACE, SurfaceStates.SHORE_UNDERLAYER, 6, false, false, false, NormalSurfaceBuilder.ROCKY.apply(seed));
    public static final SurfaceBuilderFactory SANDY = seed -> new ShoreAndOceanSurfaceBuilder(seed, SurfaceStates.SHORE_SAND, SurfaceStates.SHORE_SANDSTONE, 6, false, false, false, NormalSurfaceBuilder.ROCKY.apply(seed));
    public static final SurfaceBuilderFactory FORCE_RARE_SAND = seed -> new ShoreAndOceanSurfaceBuilder(seed, SurfaceStates.RARE_SHORE_SAND, SurfaceStates.RARE_SHORE_SANDSTONE, 6, false, false, false, NormalSurfaceBuilder.ROCKY.apply(seed));
    public static final SurfaceBuilderFactory GRAVELLY = seed -> new ShoreAndOceanSurfaceBuilder(seed, SurfaceStates.GRAVEL, SurfaceStates.RAW, 6, false, false, false, NormalSurfaceBuilder.ROCKY.apply(seed));
    public static final SurfaceBuilderFactory OCEAN = seed -> new ShoreAndOceanSurfaceBuilder(seed, SurfaceStates.SHORE_SURFACE, SurfaceStates.SHORE_UNDERLAYER, 6, false, false, false, SimpleSurfaceBuilder.OCEAN_MUD.apply(seed));
    public static final SurfaceBuilderFactory SEA_CLIFFS = seed -> new ShoreAndOceanSurfaceBuilder(seed, SurfaceStates.SHORE_SURFACE, SurfaceStates.SHORE_UNDERLAYER, 2, false, false, false, NormalSurfaceBuilder.ROCKY.apply(seed));
    public static final SurfaceBuilderFactory OLD_SHIELD_VOLCANO = seed -> new ShoreAndOceanSurfaceBuilder(seed, SurfaceStates.VOLCANIC_SHORE_SAND, SurfaceStates.VOLCANIC_SHORE_SANDSTONE, 6, true, false, false, ShieldVolcanoSurfaceBuilder.ACTIVE.apply(seed));
    public static final SurfaceBuilderFactory ACTIVE_SHIELD_VOLCANO = seed -> new ShoreAndOceanSurfaceBuilder(seed, SurfaceStates.VOLCANIC_SHORE_SAND, SurfaceStates.VOLCANIC_SHORE_SANDSTONE, 2, false, true, false, ShieldVolcanoSurfaceBuilder.DORMANT.apply(seed));
    public static final SurfaceBuilderFactory MOUNTAINS = seed -> new ShoreAndOceanSurfaceBuilder(seed, SurfaceStates.GRAVEL, SurfaceStates.RAW, 2, false, false, false, NormalSurfaceBuilder.ROCKY);
    public static final SurfaceBuilderFactory VOLCANIC_MOUNTAINS = seed -> new ShoreAndOceanSurfaceBuilder(seed, SurfaceStates.GRAVEL, SurfaceStates.RAW, 2, false, false, false, SimpleSurfaceBuilder.ROCKY_VOLCANIC_SOIL.apply(seed));
    public static final SurfaceBuilderFactory ROCKY_SHORE = seed -> new ShoreAndOceanSurfaceBuilder(seed, SurfaceStates.RAW, SurfaceStates.RAW, 6, false, false, false, SimpleSurfaceBuilder.ROCKY_SHORE.apply(seed));
    public static final SurfaceBuilderFactory OCEAN_RIDGE = seed -> new ShoreAndOceanSurfaceBuilder(seed, SurfaceStates.SHORE_SURFACE, SurfaceStates.SHORE_UNDERLAYER, 6, false, false, true, SimpleSurfaceBuilder.ROCKY_SHORE.apply(seed));

    final Seed seed;
    final SurfaceState surface;
    final SurfaceState subsurface;
    final int sandHeight;
    final boolean isShieldVolcano;
    final boolean isActiveShieldVolcano;
    final boolean isOceanRidge;
    final SurfaceBuilder landBuilder;
    private final NormalNoise icebergPillarNoise;
    private final NormalNoise icebergPillarRoofNoise;
    private final NormalNoise icebergSurfaceNoise;
    private final Noise2D patternedNoise;

    /**
     *
     * {@link net.minecraft.data.worldgen.NoiseData} for values
     * {@link SurfaceSystem}'s constructor for the specific noises used
     */
    protected ShoreAndOceanSurfaceBuilder(Seed seed, SurfaceState surface, SurfaceState subsurface, int sandHeight, boolean shieldVolcano, boolean activeShieldVolcano, boolean oceanRidge, SurfaceBuilder landBuilder)
    {
        this.seed = seed;
        this.surface = surface;
        this.subsurface = subsurface;
        this.sandHeight = sandHeight;
        this.isShieldVolcano = shieldVolcano;
        this.isActiveShieldVolcano = activeShieldVolcano;
        this.isOceanRidge = oceanRidge;
        this.landBuilder = landBuilder;

        final RandomSource random = seed.forkStable().fork();

        this.icebergPillarNoise = NormalNoise.create(random, new NormalNoise.NoiseParameters(-6, 1.0D, 1.0D, 1.0D, 1.0D));
        this.icebergPillarRoofNoise = NormalNoise.create(random, new NormalNoise.NoiseParameters(-3, 1.0D));
        this.icebergSurfaceNoise = NormalNoise.create(random, new NormalNoise.NoiseParameters(-6, 1.0D, 1.0D, 1.0D));
        this.patternedNoise = BiomeNoise.seaIceNoise(seed.forkStable().next());
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final BlockPos pos = context.pos();
        final int x = pos.getX();
        final int z = pos.getZ();
        final int tideLevel = (int) BiomeNoise.shoreTideLevelNoise(seed).noise(x, z);
        final int sandHeightAbsolute = tideLevel + sandHeight;
        final int seaLevel = context.getSeaLevel();

        // Track ocean surface when relevant
        final int oceanFloorY;
        if (startY > seaLevel)
        {
            oceanFloorY = startY;
        }
        else
        {
            oceanFloorY = context.chunk().getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
        }

        // If below beach level, ocean decorator can take over. Guaranteed below water level
        if (oceanFloorY < tideLevel - 5)
        {
            if (isOceanRidge)
            {
                OceanRidgeSurfaceBuilder.INSTANCE.apply(seed).buildSurface(context, startY, endY);
            }
            else
            {
                NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.OCEAN_MUD, SurfaceStates.OCEAN_MUD, SurfaceStates.OCEAN_MUD, SurfaceStates.OCEAN_MUD, SurfaceStates.OCEAN_MUD);
            }
        }
        else if (oceanFloorY <= sandHeightAbsolute)
        {
            // Special cases for shield volcano
            if (isShieldVolcano)
            {
                if (isActiveShieldVolcano)
                {
                    // Still want lava flows to continue onto beaches.
                    buildLavaFlowSurface(context, startY, endY, x, z);
                }
                else
                {
                    ShieldVolcanoSurfaceBuilder.SHORE.apply(seed).buildSurface(context, startY, endY);
                }
            }
            else
            {
                // Otherwise, make a shore from the specified materials
                NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, surface, surface, subsurface, surface, surface);
            }
        }
        else
        {
            // Above the shore level, apply the specified surface builder
            landBuilder.buildSurface(context, startY, endY);
        }

        // Frozen ocean extension is expensive, only run it if there's a chance of it doing something
        if (startY <= seaLevel)
        {
            frozenOceanExtension(context, startY, endY, oceanFloorY, seaLevel);
        }
    }

    /**
     * Essentially mimics @link ShieldVolcanoSurfaceBuilder for placement of fresh lava flows
     * but using different materials
     */
    private void buildLavaFlowSurface(SurfaceBuilderContext context, int startY, int endY, int x, int z)
    {
        final Noise2D smoothNoise = BiomeNoise.lavaFlowMaterial(seed.seed());
        final double noiseValue = smoothNoise.noise(x, z);
        final Noise2D lavaFlows = BiomeNoise.lavaFlow(seed.seed());
        final double flowValue = lavaFlows.noise(x, z);

        if (flowValue < 0.40)
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, surface, surface, subsurface, surface, surface);
        else if (flowValue < 0.50)
        {
            if (noiseValue > 0)
                NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.SNOWY_BASALT_GRAVEL, surface, subsurface, surface, surface);
            else
                NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, surface, surface, subsurface, surface, surface);
        }
        else if (flowValue < 0.75)
        {
            if (noiseValue > 0)
                NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.SNOWY_BASALT_GRAVEL, SurfaceStates.BASALT_GRAVEL, SurfaceStates.BASALT, SurfaceStates.BASALT_GRAVEL, surface);
            else
                NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.SNOWY_BASALT_COBBLE, SurfaceStates.BASALT_COBBLE, SurfaceStates.BASALT, SurfaceStates.BASALT_COBBLE, surface);
        }
        else
        {
            if (noiseValue > -0.6)
                NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.SNOWY_BASALT, SurfaceStates.BASALT, SurfaceStates.BASALT, SurfaceStates.BASALT, SurfaceStates.BASALT_COBBLE);
            else
                NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.SNOWY_BASALT_COBBLE, SurfaceStates.BASALT_COBBLE, SurfaceStates.BASALT, SurfaceStates.BASALT, SurfaceStates.BASALT_COBBLE);
        }
    }

    /**
     * Modified from {@link net.minecraft.world.level.levelgen.SurfaceSystem#frozenOceanExtension(int, Biome, BlockColumn, BlockPos.MutableBlockPos, int, int, int)}
     */
    private void frozenOceanExtension(SurfaceBuilderContext context, int startY, int endY, int oceanFloorY, int seaLevel)
    {
        final OverworldClimateModel model = OverworldClimateModel.getIfPresent(context.level());
        if (model == null)
        {
            return;
        }

        final int x = context.pos().getX();
        final int z = context.pos().getZ();

        final float maxAnnualTemperature = model.getAverageMonthlyTemperature(z, seaLevel, context.averageTemperature(), 1, true);
        if (maxAnnualTemperature > 2)
        {
            // This is run for all climates, so we want to check if ice can exist as early as possible.
            return;
        }

        final double baseNoise = Math.min(
            Math.abs(icebergSurfaceNoise.getValue(x, 0, z) * 8.25),
            icebergPillarNoise.getValue(x * 1.28, 0, z * 1.28) * 15
        );

        if (baseNoise > 1.8) // Try to place iceberg if noise is sufficient
        {
            // Scale down and then phase out icebergs near shores
            final float temperatureFactor = Mth.clampedMap(maxAnnualTemperature, -1, 2, 1, 0.5f);
            final float depthFactor = Mth.clampedMap(oceanFloorY, seaLevel - 20, seaLevel - 6, 1, 0);

            final double pillarNoise = Math.abs(icebergPillarRoofNoise.getValue(x * 1.17, 0, z * 1.17) * 1.5);
            double icebergMaxY = temperatureFactor * depthFactor * Math.min(
                baseNoise * baseNoise * 1.2,
                Math.ceil(pillarNoise * 30) + 11
            );

            // Check again whether an iceberg is expected at this position
            if (icebergMaxY < 2)
            {
                placeSeaIce(context, x, z, seaLevel, maxAnnualTemperature);
            }
            else
            {
                final BlockState packedIce = Blocks.PACKED_ICE.defaultBlockState();
                final BlockState snow = Blocks.SNOW_BLOCK.defaultBlockState();
                final RandomSource random = context.random();

                final double icebergMinY;

                icebergMinY = seaLevel - icebergMaxY - 7;
                icebergMaxY += seaLevel;

                final int snowDepth = 2 + random.nextInt(4);
                final int snowBoundaryY = seaLevel + 18 + random.nextInt(10);

                int placedSnow = 0;
                for (int y = Math.max(startY, (int) icebergMaxY + 1); y >= endY; --y)
                {
                    final BlockState state = context.getBlockState(y);
                    if ((state.isAir() && y < icebergMaxY && random.nextDouble() > 0.01)
                        || ((state.getBlock() == TFCBlocks.SALT_WATER.get() || state.getBlock() == Blocks.WATER) && y > (int) icebergMinY && y < seaLevel && icebergMinY != 0 && random.nextDouble() > 0.15))
                    {
                        if (placedSnow <= snowDepth && y > snowBoundaryY)
                        {
                            context.setBlockState(y, snow);
                            ++placedSnow;
                        }
                        else
                        {
                            context.setBlockState(y, packedIce);
                        }
                    }
                }
            }
        }
        else // Place sea ice if no iceberg at this spot
        {
            placeSeaIce(context, x, z, seaLevel, maxAnnualTemperature);
        }
    }

    private void placeSeaIce(SurfaceBuilderContext context, int x, int z, int seaLevel, float maxAnnualTemperature)
    {
        final boolean placeIce;
        final double iceStart = 1.5;
        final double solidIceStart = -0.5;

        final BlockState seaIce = TFCBlocks.SEA_ICE.get().defaultBlockState();

        // Skip sampling the cellular noise if cold enough for solid ice/too warm for ice
        if (maxAnnualTemperature < solidIceStart)
        {
            placeIce = true;
        }
        else if (maxAnnualTemperature > iceStart)
        {
            placeIce = false;
        }
        else
        {
            final double tempFactor = Mth.clampedMap(maxAnnualTemperature, iceStart, solidIceStart, 0.3, 0.04);
            placeIce = this.patternedNoise.noise(x, z) > tempFactor;
        }

        if (placeIce)
        {
            final int y = seaLevel - 1;
            final BlockState state = context.getBlockState(y);
            if (state.getBlock() == TFCBlocks.SALT_WATER.get() || state.getBlock() == Blocks.WATER)
            {
                context.setBlockState(y, seaIce);
            }
        }
    }
}