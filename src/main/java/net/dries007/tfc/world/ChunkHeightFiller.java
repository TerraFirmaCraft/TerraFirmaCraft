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

import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.BiomeSourceExtension;
import net.dries007.tfc.world.region.RegionPartition;
import net.dries007.tfc.world.region.RiverEdge;
import net.dries007.tfc.world.region.Units;
import net.dries007.tfc.world.river.Flow;
import net.dries007.tfc.world.river.MidpointFractal;
import net.dries007.tfc.world.river.RiverBlendType;
import net.dries007.tfc.world.river.RiverInfo;
import net.dries007.tfc.world.river.RiverNoiseSampler;

public class ChunkHeightFiller
{
    protected static final int RIVER_TYPE_NONE = RiverBlendType.NONE.ordinal();
    protected static final int RIVER_TYPE_CAVE = RiverBlendType.CAVE.ordinal();

    protected final Map<BiomeExtension, BiomeNoiseSampler> biomeNoiseSamplers; // Biome -> Noise Samplers
    protected final Object2DoubleMap<BiomeNoiseSampler> columnBiomeNoiseSamplers; // Per column weighted map of biome noises samplers

    protected final Object2DoubleMap<BiomeExtension>[] sampledBiomeWeights; // 7x7 array of biome weights, at quart pos resolution
    protected final Object2DoubleMap<BiomeExtension> biomeWeights1; // Local biome weights, for individual column adjustment

    // Rivers
    protected final BiomeSourceExtension biomeSource;
    protected final Map<RiverBlendType, RiverNoiseSampler> riverNoiseSamplers;
    protected final double[] riverBlendWeights; // Indexed by RiverBlendType.ordinal

    protected int blockX, blockZ; // Absolute x/z positions
    protected int localX, localZ; // Chunk-local x/z

    public ChunkHeightFiller(Object2DoubleMap<BiomeExtension>[] sampledBiomeWeights, BiomeSourceExtension biomeSource, Map<BiomeExtension, BiomeNoiseSampler> biomeNoiseSamplers, Map<RiverBlendType, RiverNoiseSampler> riverNoiseSamplers)
    {
        this.biomeNoiseSamplers = biomeNoiseSamplers;
        this.columnBiomeNoiseSamplers = new Object2DoubleOpenHashMap<>();
        this.sampledBiomeWeights = sampledBiomeWeights;
        this.biomeWeights1 = new Object2DoubleOpenHashMap<>();

        this.biomeSource = biomeSource;
        this.riverNoiseSamplers = riverNoiseSamplers;
        this.riverBlendWeights = new double[RiverBlendType.SIZE];
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

        // Requires the column to be initialized (just x/z)
        double totalHeight = 0, shoreHeight = 0;
        double shoreWeight = 0;
        BiomeExtension biomeAt, normalBiomeAt = null, shoreBiomeAt = null;
        double maxNormalWeight = 0, maxShoreWeight = 0; // Partition on biome type

        for (Object2DoubleMap.Entry<BiomeExtension> entry : biomeWeights.object2DoubleEntrySet())
        {
            final double weight = entry.getDoubleValue();
            final BiomeExtension biome = entry.getKey();
            final BiomeNoiseSampler sampler = biomeNoiseSamplers.get(biome);

            assert sampler != null : "Non-existent sampler for biome: " + biome.key();

            if (columnBiomeNoiseSamplers.containsKey(sampler))
            {
                columnBiomeNoiseSamplers.mergeDouble(sampler, weight, Double::sum);
            }
            else
            {
                sampler.setColumn(blockX, blockZ);
                columnBiomeNoiseSamplers.put(sampler, weight);
            }

            double height = weight * sampler.height();
            totalHeight += height;

            if (biome.isShore())
            {
                shoreHeight += height;
                shoreWeight += weight;
                if (maxShoreWeight < weight)
                {
                    shoreBiomeAt = entry.getKey();
                    maxShoreWeight = weight;
                }
            }
            else if (maxNormalWeight < weight)
            {
                normalBiomeAt = entry.getKey();
                maxNormalWeight = weight;
            }
        }

        double height = totalHeight;
        biomeAt = normalBiomeAt;

        if ((shoreWeight > 0.6 || maxShoreWeight > maxNormalWeight) && shoreBiomeAt != null)
        {
            // Flatten beaches above a threshold, creates cliffs where the beach ends
            double aboveWaterDelta = height - shoreHeight / shoreWeight;
            if (aboveWaterDelta > 0)
            {
                if (aboveWaterDelta > 20)
                {
                    aboveWaterDelta = 20;
                }
                double adjustedAboveWaterDelta = 0.02 * aboveWaterDelta * (40 - aboveWaterDelta) - 0.48;
                height = shoreHeight / shoreWeight + adjustedAboveWaterDelta;
            }
            biomeAt = shoreBiomeAt;
        }

        assert biomeAt != null;

        computeInitialRiverWeights(biomeWeights);

        final double initialCaveWeight = adjustWeightsForRiverCaves();
        final RiverInfo info = sampleRiverInfo(useCache);

        height = adjustHeightForRiverContributions(height, info, initialCaveWeight);

        if (useCache)
        {
            updateLocalCaches(biomeWeights, biomeAt, info, height);
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

    /**
     * Adjusts {@link #riverBlendWeights} to bias towards river caves, creating sharper cutoffs and preventing caves
     * from pinching off rivers.
     * @return The initial weight of the river cave type.
     */
    private double adjustWeightsForRiverCaves()
    {
        // Adjust bias for river cave to create sharp cutoffs at borders, helps prevent caves from breaking up rivers
        final double initialCaveWeight = riverBlendWeights[RIVER_TYPE_CAVE];
        if (initialCaveWeight > 0)
        {
            // Delegate weight entirely to the cave carver after a point, and let it handle interpolation into the mouth of the cave
            // This needs to be very carefully managed not to pinch off the edge, and interpolating a canyon and cave together leads to subpar results.
            // So, we supply the river carve weight (initial value) to the cave carver, and run it at 1.0 weight instead, which will create a smooth transition.
            final double adjustedCaveWeight = initialCaveWeight < 0.25 ?
                Mth.map(initialCaveWeight, 0.0, 0.25, 0, 0.1) :
                1.0 - riverBlendWeights[RIVER_TYPE_NONE];

            for (RiverBlendType type : RiverBlendType.ALL)
            {
                final double weight = riverBlendWeights[type.ordinal()];
                riverBlendWeights[type.ordinal()] = weight * (1.0 - adjustedCaveWeight) / (1.0 - initialCaveWeight);
            }

            riverBlendWeights[RIVER_TYPE_CAVE] = adjustedCaveWeight;
        }

        return initialCaveWeight;
    }

    private double adjustHeightForRiverContributions(final double height, @Nullable RiverInfo info, double initialCaveWeight)
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
                    final double riverHeight = sampler.setColumnAndSampleHeight(info, blockX, blockZ, height, initialCaveWeight);
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

    protected void updateLocalCaches(Object2DoubleMap<BiomeExtension> biomeWeights, BiomeExtension biomeAt, @Nullable RiverInfo info, double height) {}

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
