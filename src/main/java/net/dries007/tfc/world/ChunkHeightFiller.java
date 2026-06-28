/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.Arrays;
import java.util.Map;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.biome.BiomeBlendType;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.BiomeSourceExtension;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.region.RegionPartition;
import net.dries007.tfc.world.region.RiverEdge;
import net.dries007.tfc.world.region.Units;
import net.dries007.tfc.world.river.Flow;
import net.dries007.tfc.world.river.MidpointFractal;
import net.dries007.tfc.world.river.RiverBlendType;
import net.dries007.tfc.world.river.RiverInfo;
import net.dries007.tfc.world.river.RiverNoiseSampler;
import net.dries007.tfc.world.shore.ShoreBlendType;
import net.dries007.tfc.world.shore.ShoreNoiseSampler;
import net.dries007.tfc.world.volcano.CenteredFeatureBlendType;
import net.dries007.tfc.world.volcano.CenteredFeatureNoiseSampler;

public class ChunkHeightFiller
{
    protected static final int RIVER_TYPE_NONE = RiverBlendType.NONE.ordinal();
    protected static final int RIVER_TYPE_CAVE = RiverBlendType.CAVE.ordinal();
    public static final int NOT_PRESENT_RETURN = Integer.MIN_VALUE;

    protected final Map<BiomeExtension, BiomeNoiseSampler> biomeNoiseSamplers; // Biome -> Noise Samplers
    protected final Object2DoubleMap<BiomeNoiseSampler> columnBiomeNoiseSamplers; // Per column weighted map of biome noises samplers

    protected final Object2DoubleMap<BiomeExtension>[] sampledBiomeWeights; // 7x7 array of biome weights, at quart pos resolution
    protected final Object2DoubleMap<BiomeExtension> biomeWeights1; // Local biome weights, for individual column adjustment

    // Rivers
    protected final BiomeSourceExtension biomeSource;
    protected final Map<RiverBlendType, RiverNoiseSampler> riverNoiseSamplers;
    protected final double[] riverBlendWeights; // Indexed by RiverBlendType.ordinal

    // Shores
    protected final Map<ShoreBlendType, ShoreNoiseSampler> shoreNoiseSamplers;
    protected final double[] shoreBlendWeights; // Indexed by ShoreBlendType.ordinal
    protected final int seaLevel;
    protected final Noise2D tideHeightNoise;

    // Centered Features, such as volcanoes
    protected final Map<CenteredFeatureBlendType, CenteredFeatureNoiseSampler> volcanoNoiseSamplers;

    protected int blockX, blockZ; // Absolute x/z positions
    protected int localX, localZ; // Chunk-local x/z

    public ChunkHeightFiller(Object2DoubleMap<BiomeExtension>[] sampledBiomeWeights, BiomeSourceExtension biomeSource, Map<BiomeExtension, BiomeNoiseSampler> biomeNoiseSamplers, Map<RiverBlendType, RiverNoiseSampler> riverNoiseSamplers, Map<ShoreBlendType, ShoreNoiseSampler> shoreNoiseSamplers, Map<CenteredFeatureBlendType, CenteredFeatureNoiseSampler> volcanoNoiseSamplers, int seaLevel, Noise2D tideHeightNoise)
    {
        this.biomeNoiseSamplers = biomeNoiseSamplers;
        this.columnBiomeNoiseSamplers = new Object2DoubleOpenHashMap<>();
        this.sampledBiomeWeights = sampledBiomeWeights;
        this.biomeWeights1 = new Object2DoubleOpenHashMap<>();

        this.biomeSource = biomeSource;
        this.riverNoiseSamplers = riverNoiseSamplers;
        this.riverBlendWeights = new double[RiverBlendType.SIZE];

        this.shoreNoiseSamplers = shoreNoiseSamplers;
        this.shoreBlendWeights = new double[ShoreBlendType.SIZE];
        this.seaLevel = seaLevel;
        this.tideHeightNoise = tideHeightNoise;

        this.volcanoNoiseSamplers = volcanoNoiseSamplers;
    }

    /**
     * Samples the height at a specific location. This is the only exposed public method available
     * from {@link ChunkHeightFiller}, any other manipulations require a complete {@link ChunkNoiseFiller}
     *
     * @param blockX The block X.
     * @param blockZ The block Z.
     * @return The approximate height of the world at that location.
     */
    public double sampleHeight(int blockX, int blockZ)
    {
        setupColumn(blockX, blockZ);
        prepareColumnBiomeWeights();
        return sampleColumnHeightAndBiome(biomeWeights1, false);
    }

    /**
     * Initializes {@link #biomeWeights1} from the sampled biome weights
     */
    protected final void prepareColumnBiomeWeights()
    {
        ChunkBiomeSampler.sampleBiomesColumn(biomeWeights1, sampledBiomeWeights, localX, localZ);
    }

    /**
     * For a given (x, z) position, samples the provided biome weight map to calculate the height at that location, and the biome
     *
     * @param useCache If, in the stateful implementation, arrays corresponding to position within the chunk should be updated.
     * @return The maximum height at this location
     */
    protected final double sampleColumnHeightAndBiome(Object2DoubleMap<BiomeExtension> biomeWeights, boolean useCache)
    {
        columnBiomeNoiseSamplers.clear();

        double height = 0, normalHeight = 0, shoreHeight = 0;
        double shoreWeight = 0, oceanWeight = 0;

        BiomeExtension biomeAt = null, normalBiomeAt = null, shoreBiomeAt = null, oceanBiomeAt = null;
        double maxNormalWeight = 0, maxShoreWeight = 0, maxOceanWeight = 0; // Partition on biome type

        boolean anySaltyBiomesNearby = false;
        for (Object2DoubleMap.Entry<BiomeExtension> entry : biomeWeights.object2DoubleEntrySet())
        {
            final double biomeWeight = entry.getDoubleValue();
            final BiomeExtension biome = entry.getKey();
            final BiomeNoiseSampler sampler = biomeNoiseSamplers.get(biome);

            if (biome.isSalty())
            {
                anySaltyBiomesNearby = true;
            }

            assert sampler != null : "Non-existent sampler for biome: " + biome.key();

            if (columnBiomeNoiseSamplers.containsKey(sampler))
            {
                columnBiomeNoiseSamplers.mergeDouble(sampler, biomeWeight, Double::sum);
            }
            else
            {
                sampler.setColumn(blockX, blockZ);
                columnBiomeNoiseSamplers.put(sampler, biomeWeight);
            }

            final double biomeHeight = biomeWeight * sampler.height();
            height += biomeHeight;

            if (biome.isShore())
            {
                shoreHeight += biomeHeight;
                shoreWeight += biomeWeight;
                if (maxShoreWeight < biomeWeight)
                {
                    shoreBiomeAt = biome;
                    maxShoreWeight = biomeWeight;
                }
            }
            else if (biome.biomeBlendType() == BiomeBlendType.OCEAN)
            {
                oceanWeight += biomeWeight;
                if (maxOceanWeight < biomeWeight)
                {
                    oceanBiomeAt = biome;
                    maxOceanWeight = biomeWeight;
                }
            }
            else
            {
                normalHeight += biomeHeight;
                if (maxNormalWeight < biomeWeight)
                {
                    normalBiomeAt = biome;
                    maxNormalWeight = biomeWeight;
                }
            }
        }

        biomeAt = normalBiomeAt;
        computeInitialShoreWeights(biomeWeights);

        final double landWeight = 1 - oceanWeight - shoreWeight;
        if (shoreWeight > 0 && shoreBiomeAt != null)
        {
            height = adjustHeightForShoreContributions(height, oceanWeight, landWeight, shoreWeight, maxShoreWeight, shoreBiomeAt, shoreHeight, normalHeight);
            if (shoreWeight > 0.5) biomeAt = shoreBiomeAt;
        }

        if (biomeAt == null)
        {
            biomeAt = oceanBiomeAt;
        }

        if (oceanWeight >= 0.25)
        {
            final double tideAdjustedSeaEdgeHeight = tideHeightNoise.noise(blockX, blockZ) - 4;
            height = Mth.clampedMap(landWeight, 0.32, 0.36, Math.min(height, tideAdjustedSeaEdgeHeight), height);
        }

        assert biomeAt != null;

        computeInitialRiverWeights(biomeWeights);

        final RiverInfo info = sampleRiverInfo(useCache);

        height = adjustHeightForRiverContributions(height, info);

        final double preVolcanicHeight = height;

        // Centered features (volcanoes) are last to override rivers and beaches
        height = adjustHeightForVolcanic(height);

        // The minimum depth below the surface that noise (excluding river noise) should not be allowed to carve
        // Typically 0, allowing carving anywhere, but where centered noise features (volcanoes) elevate the terrain the surface is guaranteed to be intact to some depth
        final double volcanoHeightDelta = height - preVolcanicHeight;
        final int surfaceIntegrityDepth;
        if (volcanoHeightDelta > 0)
        {
            surfaceIntegrityDepth = (int) volcanoHeightDelta * 3;
        }
        else
        {
            surfaceIntegrityDepth = 0;
        }

        if (useCache)
        {
            updateLocalCaches(biomeWeights, biomeAt, info, height, preVolcanicHeight, anySaltyBiomesNearby, surfaceIntegrityDepth);
        }

        return height;
    }

    protected void setupColumn(int x, int z)
    {
        this.blockX = x;
        this.blockZ = z;
        this.localX = x & 15;
        this.localZ = z & 15;
    }

    /**
     * Initializes {@link #shoreBlendWeights} from the biome weights, using the shore type of each biome.
     */
    private void computeInitialShoreWeights(Object2DoubleMap<BiomeExtension> biomeWeights)
    {
        // Sum weights by biome extension -> river blend type first
        Arrays.fill(shoreBlendWeights, 0d);
        for (Object2DoubleMap.Entry<BiomeExtension> entry : biomeWeights.object2DoubleEntrySet())
        {
            shoreBlendWeights[entry.getKey().shoreBlendType().ordinal()] += entry.getDoubleValue();
        }
    }

    private double adjustHeightForShoreContributions(final double height, final double oceanWeight, final double landWeight, final double shoreWeight, final double thisWeight, final BiomeExtension biome, final double shoreHeight, final double normalHeight)
    {

        // Iterate through blend types, and sample once
        // Each sampler gets the original terrain height, modifies it, and is interpolated together
        double shoreBlendHeight = 0d;
        for (ShoreBlendType type : ShoreBlendType.ALL)
        {
            final double weight = shoreBlendWeights[type.ordinal()];
            final ShoreNoiseSampler sampler = shoreNoiseSamplers.get(type);
            if (type == ShoreBlendType.NONE)
            {
                shoreBlendHeight += weight * height;
            }
            else if (weight > 0)
            {
                final double newShoreHeight = sampler.setColumnAndSampleHeight(height, blockX, blockZ, oceanWeight, landWeight, shoreWeight, thisWeight, biome, shoreHeight, normalHeight);
                shoreBlendHeight += weight * newShoreHeight;
            }
        }
        return shoreBlendHeight;
    }

    /**
     * Initializes {@link #riverBlendWeights} from the biome weights, using the river type of each biome.
     */
    private void computeInitialRiverWeights(Object2DoubleMap<BiomeExtension> biomeWeights)
    {
        // Sum weights by biome extension -> river blend type first
        Arrays.fill(riverBlendWeights, 0d);
        for (Object2DoubleMap.Entry<BiomeExtension> entry : biomeWeights.object2DoubleEntrySet())
        {
            riverBlendWeights[entry.getKey().riverBlendType().ordinal()] += entry.getDoubleValue();
        }
    }

    private double adjustHeightForRiverContributions(final double height, @Nullable RiverInfo info)
    {
        // Only perform river modifications if there's a river anywhere in sight
        if (info != null)
        {
            // Iterate through blend types, and sample once
            // Each sampler gets the original terrain height, modifies it, and is interpolated together
            double riverBlendHeight = 0d;
            for (RiverBlendType type : RiverBlendType.ALL)
            {
                final double weight = riverBlendWeights[type.ordinal()];
                final RiverNoiseSampler sampler = riverNoiseSamplers.get(type);
                if (type == RiverBlendType.NONE)
                {
                    riverBlendHeight += weight * height;
                }
                else if (weight > 0)
                {
                    final double riverHeight = sampler.setColumnAndSampleHeight(info, blockX, blockZ, height, weight);
                    riverBlendHeight += weight * riverHeight;
                }
            }
            return riverBlendHeight;
        }
        else
        {
            // Otherwise, we do a hack here - we set the weights to 1.0 at 'NONE' instead.
            // So later when we sample noise, we don't call `noise()` on any samplers that didn't initialize, because the river info was null.
            Arrays.fill(riverBlendWeights, 0);
            riverBlendWeights[RIVER_TYPE_NONE] = 1.0;
            return height;
        }
    }

    private double adjustHeightForVolcanic(final double heightIn)
    {
        double volcanoHeight = NOT_PRESENT_RETURN;

        for (CenteredFeatureBlendType type : CenteredFeatureBlendType.ALL)
        {
            final CenteredFeatureNoiseSampler sampler = volcanoNoiseSamplers.get(type);
            volcanoHeight = Math.max(sampler.setColumnAndSampleHeight(heightIn, blockX, blockZ, biomeSource), volcanoHeight);
        }

        return volcanoHeight == NOT_PRESENT_RETURN ? heightIn : volcanoHeight;
    }

    protected void updateLocalCaches(Object2DoubleMap<BiomeExtension> biomeWeights, BiomeExtension biomeAt, @Nullable RiverInfo info, double height, double preVolcanicHeight, boolean couldBeSalty, int surfaceIntegrityDepth) {}

    @Nullable
    protected RiverInfo sampleRiverInfo(boolean useCache)
    {
        return sampleRiverEdge(biomeSource.getPartition(blockX, blockZ));
    }

    @Nullable
    protected final RiverInfo sampleRiverEdge(RegionPartition.Point point)
    {
        final float limitDistInGridSq = 50f * 50f / (Units.GRID_WIDTH_IN_BLOCK * Units.GRID_WIDTH_IN_BLOCK);
        double minDist = limitDistInGridSq; // Only concern ourselves with rivers within a range of 50 ^2 blocks. This helps `maybeIntersect` fail more often.
        double minDistAdjusted = Float.MAX_VALUE;
        RiverEdge minEdge = null;

        double exactGridX = Units.blockToGridExact(blockX);
        double exactGridZ = Units.blockToGridExact(blockZ);

        for (RiverEdge edge : point.rivers())
        {
            final MidpointFractal fractal = edge.fractal();
            if (fractal.maybeIntersect(exactGridX, exactGridZ, minDist))
            {
                // Minimum by square distance would get us the closest edge, but would fail in the case some edges are wider than others
                // Since in most situations, we're actually concerned about distance / width, we want to have the one with the highest weight in that respect.
                final double dist = fractal.intersectDistance(exactGridX, exactGridZ);
                if (dist < limitDistInGridSq) // Extra check that we intersect at a shorter distance than can possibly affect this location
                {
                    final double distAdjusted = dist / edge.widthSq();
                    if (distAdjusted < minDistAdjusted)
                    {
                        minDist = dist;
                        minDistAdjusted = distAdjusted;
                        minEdge = edge;
                    }
                }
            }
        }

        if (minEdge != null)
        {
            final double realWidth = minEdge.widthSq(exactGridX, exactGridZ);
            final Flow flow = minEdge.fractal().calculateFlow(exactGridX, exactGridZ);

            // minDist is in grid^2
            // convert it to block^2
            minDist *= Units.GRID_WIDTH_IN_BLOCK * Units.GRID_WIDTH_IN_BLOCK;

            return new RiverInfo(minEdge, flow, minDist, realWidth);
        }
        return null;
    }
}
