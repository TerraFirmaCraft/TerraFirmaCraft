package net.dries007.tfc.world;

import java.util.BitSet;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.dries007.tfc.common.fluids.RiverWaterFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.world.biome.BiomeVariants;
import net.dries007.tfc.world.biome.RiverSource;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.carver.CarverHelpers;
import net.dries007.tfc.world.noise.ChunkNoiseSampler;
import net.dries007.tfc.world.noise.ChunkNoiseSamplingSettings;
import net.dries007.tfc.world.noise.NoiseSampler;
import net.dries007.tfc.world.river.Flow;

public class ChunkNoiseFiller
{
    // Initialized from the chunk
    private final LevelAccessor world;
    private final ChunkAccess chunk;
    private final int chunkX, chunkZ; // Min block positions for the chunk
    private final int quartX, quartZ; // Min quart positions for the chunk
    private final Object2DoubleMap<BiomeNoiseSampler> biomeSamplers;
    private final Heightmap oceanFloor, worldSurface;
    //private final BitSet carvingMask; // We mark the air carving mask for everything
    //private final Aquifer aquifer;
    //private final ChunkBaseBlockSource stoneSource;

    private final RiverSource riverSource;
    private final FluidState riverWater;
    private final Flow[] flows;

    private final Map<BiomeVariants, BiomeNoiseSampler> biomeNoiseSamplers;
    private final ChunkNoiseSampler chunkSampler;
    private final ChunkNoiseSamplingSettings settings;

    // Externally sampled biome weight arrays, and a weight map for local sampling
    private final Object2DoubleMap<Biome>[] sampledBiomeWeights;
    private final Object2DoubleMap<Biome> biomeWeights1;

    // Result arrays, set for each x/z in the chunk
    private final int[] surfaceHeight;
    private final Biome[] localBiomes;

    // Current local position / context
    private int x, z; // Absolute x/z positions
    private int localX, localZ; // Chunk-local x/z
    private double cellDeltaX, cellDeltaZ; // Delta within a noise cell
    private int lastCellZ; // Last cell Z, needed due to a quick in noise interpolator

    ChunkNoiseFiller(LevelAccessor world, ChunkAccess chunk, Object2DoubleMap<Biome>[] sampledBiomeWeights, int[] surfaceHeight, Biome[] localBiomes, RiverSource riverSource, Map<BiomeVariants, BiomeNoiseSampler> biomeNoiseSamplers, NoiseSampler noiseSampler, ChunkNoiseSamplingSettings settings)
    {
        this.world = world;
        this.chunk = chunk;
        this.chunkX = chunk.getPos().getMinBlockX();
        this.chunkZ = chunk.getPos().getMinBlockZ();
        this.quartX = QuartPos.fromBlock(chunkX);
        this.quartZ = QuartPos.fromBlock(chunkZ);
        this.biomeSamplers = new Object2DoubleOpenHashMap<>();
        this.riverWater = TFCFluids.RIVER_WATER.get().defaultFluidState();

        this.flows = buildFlowMap();

        this.oceanFloor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        this.worldSurface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        // this.carvingMask = CarverHelpers.getCarvingMask(chunk, getNoiseGeneratorSettings().noiseSettings().height());

        this.sampledBiomeWeights = sampledBiomeWeights;
        this.biomeWeights1 = new Object2DoubleOpenHashMap<>();

        this.surfaceHeight = surfaceHeight;
        this.localBiomes = localBiomes;

        this.riverSource = riverSource;
        this.biomeNoiseSamplers = biomeNoiseSamplers;
        this.chunkSampler = new ChunkNoiseSampler(noiseSampler, settings);
        this.settings = settings;
    }

    /**
     * Fills the entire chunk
     */
    void fillFromNoise()
    {
        chunkSampler.initializeForFirstCellX();
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int cellX = 0; cellX < settings.cellCountXZ(); cellX++)
        {
            chunkSampler.advanceCellX(cellX);
            for (int cellZ = 0; cellZ < settings.cellCountXZ(); cellZ++)
            {
                // skip cell Y
                for (int localCellX = 0; localCellX < settings.cellWidth(); localCellX++)
                {
                    x = chunkX + cellX * settings.cellWidth() + localCellX;
                    localX = x & 15;
                    cellDeltaX = (double) localCellX / settings.cellWidth();

                    // cannot update for x here because we first need to update for yz. So we do all three each time per cell
                    for (int localCellZ = 0; localCellZ < settings.cellWidth(); localCellZ++)
                    {
                        z = chunkZ + cellZ * settings.cellWidth() + localCellZ;
                        lastCellZ = cellZ; // needed for the noise interpolator
                        localZ = z & 15;
                        cellDeltaZ = (double) localCellZ / settings.cellWidth();

                        mutablePos.set(x, 0, z);
                        fillColumn(mutablePos, cellX, cellZ);
                    }
                }
            }
            chunkSampler.swapSlices();
        }
    }

    /**
     * Fills a single column
     *
     * Deprecation for the use of {@link BlockState#getLightEmission()}
     */
    @SuppressWarnings("deprecation")
    void fillColumn(BlockPos.MutableBlockPos mutablePos, int cellX, int cellZ)
    {
        prepareColumnBiomeWeights(); // Before iterating y, setup x/z biome sampling

        final double heightNoiseValue = sampleColumnHeightAndBiome(biomeWeights1, true); // sample height, using the just-computed biome weights

        // Only calculate flow if we're in a river type biome
        Flow flow = Flow.NONE;
        if (TFCBiomes.getExtensionOrThrow(world, localBiomes[localX + 16 * localZ]).getVariants().isRiver())
        {
            // Interpolate flow for this column
            final Flow flow00 = flows[cellX + 5 * cellZ];
            final Flow flow10 = flows[cellX + 5 * (cellZ + 1)];
            final Flow flow01 = flows[(cellX + 1) + 5 * cellZ];
            final Flow flow11 = flows[(cellX + 1) + 5 * (cellZ + 1)];

            flow = Flow.lerp(flow00, flow01, flow10, flow11, (float) cellDeltaX, (float) cellDeltaZ);
        }

        final int maxFilledY = 1 + Math.max((int) heightNoiseValue, TFCChunkGenerator.SEA_LEVEL_Y);
        final int maxFilledCellY = Math.min(settings.cellCountY() - 1, 1 + Math.floorDiv(maxFilledY, settings.cellHeight()) - settings.firstCellY());
        final int maxFilledSectionY = Math.min(chunk.getSectionsCount() - 1, 1 + chunk.getSectionIndex(maxFilledY));

        // Top down iteration
        // 1. We need to mark exposed air below the first solid ground as carving mask applicable.
        // 2. We need to record the highest height (be it water or solid) for height map creation
        boolean topBlockPlaced = false;
        boolean topSolidBlockPlaced = false;

        LevelChunkSection section = chunk.getSection(maxFilledSectionY);
        for (int cellY = maxFilledCellY; cellY >= 0; --cellY)
        {
            chunkSampler.selectCellYZ(cellY, lastCellZ);

            for (int localCellY = settings.cellHeight() - 1; localCellY >= 0; --localCellY)
            {
                int y = (settings.firstCellY() + cellY) * settings.cellHeight() + localCellY;
                int localY = y & 15;
                int lastSectionIndex = chunk.getSectionIndex(y);
                if (chunk.getSectionIndex(section.bottomBlockY()) != lastSectionIndex)
                {
                    section = chunk.getSection(lastSectionIndex);
                }

                double cellDeltaY = (double) localCellY / settings.cellHeight();

                chunkSampler.updateForY(cellDeltaY);
                chunkSampler.updateForX(cellDeltaX);

                final double noise = calculateNoiseAtHeight(y, heightNoiseValue);
                final BlockState state = modifyNoiseAndGetState(null, null, x, y, z, noise); // todo: ????
                final FluidState fluid = state.getFluidState();

                // todo: carving masks
                final int carvingMaskIndex = CarverHelpers.maskIndex(localX, y, localZ, settings.minY());

                // Set block
                mutablePos.setY(y);
                if (!state.isAir())
                {
                    // Need to account for underground rivers in this y level check, thus the smaller value between sea level and height noise
                    if (fluid.getType() == Fluids.WATER && flow != Flow.NONE && y >= Math.min(TFCChunkGenerator.SEA_LEVEL_Y - 4, heightNoiseValue))
                    {
                        // Place a flowing fluid block according to the river flow at this location
                        section.setBlockState(localX, localY, localZ, riverWater.setValue(RiverWaterFluid.FLOW, flow).createLegacyBlock());
                    }
                    else
                    {
                        section.setBlockState(localX, localY, localZ, state, false);
                    }
                    if (false /* aquifer.shouldScheduleFluidUpdate() */ && !fluid.isEmpty()) // todo: aquifers
                    {
                        chunk.markPosForPostprocessing(mutablePos);
                    }

                    // Handle lava
                    if (state.getLightEmission() != 0 && chunk instanceof ProtoChunk protoChunk)
                    {
                        protoChunk.addLight(mutablePos);
                    }
                }

                // Update heightmaps and carving masks
                if (state.isAir()) // Air
                {
                    if (topSolidBlockPlaced)
                    {
                        // Air under solid blocks, so mark as carved, and replace with cave air
                        // todo: carvingMask.set(carvingMaskIndex);
                        section.setBlockState(localX, localY, localZ, Blocks.CAVE_AIR.defaultBlockState(), false);
                    }
                }
                else if (!fluid.isEmpty()) // Fluids
                {
                    if (!topBlockPlaced)
                    {
                        // Check carving mask
                        topBlockPlaced = true;
                        worldSurface.update(localX, y, localZ, state);
                    }
                    if (topSolidBlockPlaced)
                    {
                        // Fluids under solid blocks, so mark as carved
                        // todo: carvingMask.set(carvingMaskIndex);
                    }
                }
                else // Solid rock
                {
                    // Update both heightmaps
                    if (!topBlockPlaced)
                    {
                        topBlockPlaced = true;
                        worldSurface.update(localX, y, localZ, state);
                    }
                    if (!topSolidBlockPlaced)
                    {
                        topSolidBlockPlaced = true;
                        oceanFloor.update(localX, y, localZ, state);
                    }
                }
            }
        }
    }

    /**
     * Builds a 6x6, 4x4 resolution slope map for a chunk
     * This is enough to do basic linear interpolation for every point within the chunk.
     *
     * @return A measure of how slope-y the chunk is. Values roughly in [0, 13), although technically can be >13
     */
    @SuppressWarnings("PointlessArithmeticExpression")
    double[] buildSlopeMap()
    {
        double[] sampledHeightMap = new double[7 * 7]; // A 7x7, 4x4 resolution map of heights in the chunk, offset by (-1, -1)

        // Interior points - record from the existing positions in the chunk
        for (int x = 0; x < 4; x++)
        {
            for (int z = 0; z < 4; z++)
            {
                sampledHeightMap[(x + 1) + 7 * (z + 1)] = surfaceHeight[(x << 2) + 16 * (z << 2)];
            }
        }

        // Exterior points
        for (int i = 0; i < TFCChunkGenerator.EXTERIOR_POINTS_COUNT; i++)
        {
            int x = TFCChunkGenerator.EXTERIOR_POINTS[i << 1];
            int z = TFCChunkGenerator.EXTERIOR_POINTS[(i << 1) | 1];

            int x0 = chunkX + ((x - 1) << 2);
            int z0 = chunkZ + ((z - 1) << 2);

            setupColumn(x0, z0);
            sampledHeightMap[x + 7 * z] = sampleColumnHeightAndBiome(sampledBiomeWeights[x + z * 7], false);
        }

        double[] slopeMap = new double[6 * 6];
        for (int x = 0; x < 6; x++)
        {
            for (int z = 0; z < 6; z++)
            {
                // Math people (including myself) cry at what I'm calling 'the derivative'
                final double nw = sampledHeightMap[(x + 0) + 7 * (z + 0)];
                final double ne = sampledHeightMap[(x + 1) + 7 * (z + 0)];
                final double sw = sampledHeightMap[(x + 0) + 7 * (z + 1)];
                final double se = sampledHeightMap[(x + 1) + 7 * (z + 1)];

                final double center = (nw + ne + sw + se) / 4;
                final double slope = Math.abs(nw - center) + Math.abs(ne - center) + Math.abs(sw - center) + Math.abs(se - center);
                slopeMap[x + 6 * z] = slope;
            }
        }
        return slopeMap;
    }

    Flow[] buildFlowMap()
    {
        final Flow[] flowMap = new Flow[5 * 5];
        for (int x = 0; x < 5; x++)
        {
            for (int z = 0; z < 5; z++)
            {
                flowMap[x + 5 * z] = riverSource.getRiverFlow(quartX + x, quartZ + z);
            }
        }
        return flowMap;
    }

    /**
     * Initializes {@link #biomeWeights1} from the sampled biome weights
     */
    void prepareColumnBiomeWeights()
    {
        final int index4X = (localX >> 2) + 1;
        final int index4Z = (localZ >> 2) + 1;

        final double lerpX = (localX - ((localX >> 2) << 2)) * (1 / 4d);
        final double lerpZ = (localZ - ((localZ >> 2) << 2)) * (1 / 4d);

        biomeWeights1.clear();
        TFCChunkGenerator.sampleBiomesCornerContribution(biomeWeights1, sampledBiomeWeights[index4X + index4Z * 7], (1 - lerpX) * (1 - lerpZ));
        TFCChunkGenerator.sampleBiomesCornerContribution(biomeWeights1, sampledBiomeWeights[(index4X + 1) + index4Z * 7], lerpX * (1 - lerpZ));
        TFCChunkGenerator.sampleBiomesCornerContribution(biomeWeights1, sampledBiomeWeights[index4X + (index4Z + 1) * 7], (1 - lerpX) * lerpZ);
        TFCChunkGenerator.sampleBiomesCornerContribution(biomeWeights1, sampledBiomeWeights[(index4X + 1) + (index4Z + 1) * 7], lerpX * lerpZ);
    }

    /**
     * For a given (x, z) position, samples the provided biome weight map to calculate the height at that location, and the biome
     *
     * @param updateArrays If the local biome and height arrays should be updated, if we are sampling within the chunk
     * @return The maximum height at this location
     */
    double sampleColumnHeightAndBiome(Object2DoubleMap<Biome> biomeWeights, boolean updateArrays)
    {
        biomeSamplers.clear();

        // Requires the column to be initialized (just x/z)
        double totalHeight = 0, riverHeight = 0, shoreHeight = 0;
        double riverWeight = 0, shoreWeight = 0;
        Biome biomeAt = null, normalBiomeAt = null, riverBiomeAt = null, shoreBiomeAt = null;
        double maxNormalWeight = 0, maxRiverWeight = 0, maxShoreWeight = 0; // Partition on biome type

        Biome oceanicBiomeAt = null;
        double oceanicWeight = 0, maxOceanicWeight = 0; // Partition on ocean/non-ocean or water type.

        for (Object2DoubleMap.Entry<Biome> entry : biomeWeights.object2DoubleEntrySet())
        {
            final double weight = entry.getDoubleValue();
            final BiomeVariants variants = TFCBiomes.getExtensionOrThrow(world, entry.getKey()).getVariants();
            final BiomeNoiseSampler sampler = biomeNoiseSamplers.get(variants);

            if (biomeSamplers.containsKey(sampler))
            {
                biomeSamplers.mergeDouble(sampler, weight, Double::sum);
            }
            else
            {
                sampler.setColumn(x, z);
                biomeSamplers.put(sampler, weight);
            }

            double height = weight * sampler.height();
            totalHeight += height;

            // Partition into river / shore / normal for standard biome transformations
            if (variants.isRiver())
            {
                riverHeight += height;
                riverWeight += weight;
                if (maxRiverWeight < weight)
                {
                    riverBiomeAt = entry.getKey();
                    maxRiverWeight = weight;
                }
            }
            else if (variants.isShore())
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

            // Also record oceanic biome types
            if (variants.isSalty())
            {
                oceanicWeight += weight;
                if (maxOceanicWeight < weight)
                {
                    oceanicBiomeAt = entry.getKey();
                    maxOceanicWeight = weight;
                }
            }
        }

        double actualHeight = totalHeight;
        if (riverWeight > 0.6 && riverBiomeAt != null)
        {
            // Primarily river biomes.
            // Based on the oceanic weight, we may apply a modifier which scales rivers down, and creates sharp cliffs near river borders.
            // If oceanic weight is high, this effect is ignored, and we intentionally weight towards the oceanic biome.
            double aboveWaterDelta = Mth.clamp(actualHeight - riverHeight / riverWeight, 0, 20);
            double adjustedAboveWaterDelta = 0.02 * aboveWaterDelta * (40 - aboveWaterDelta) - 0.48;
            double actualHeightWithRiverContribution = riverHeight / riverWeight + adjustedAboveWaterDelta;

            // Contribution of ocean type biomes to the 'normal' weight.
            double normalWeight = 1 - riverWeight - shoreWeight;
            double oceanicContribution = Mth.clamp(oceanicWeight == 0 || normalWeight == 0 ? 0 : oceanicWeight / normalWeight, 0, 1);
            if (oceanicContribution < 0.5)
            {
                actualHeight = Mth.lerp(2 * oceanicContribution, actualHeightWithRiverContribution, actualHeight);
                biomeAt = riverBiomeAt;
            }
            else
            {
                // Consider this primarily an oceanic weight area, in biome only. Do not adjust the nominal height.
                biomeAt = oceanicBiomeAt;
            }
        }
        else if (riverWeight > 0 && normalBiomeAt != null)
        {
            double adjustedRiverWeight = 0.6 * riverWeight;
            actualHeight = (totalHeight - riverHeight) * ((1 - adjustedRiverWeight) / (1 - riverWeight)) + riverHeight * (adjustedRiverWeight / riverWeight);

            biomeAt = normalBiomeAt;
        }
        else if (normalBiomeAt != null)
        {
            biomeAt = normalBiomeAt;
        }

        if ((shoreWeight > 0.6 || maxShoreWeight > maxNormalWeight) && shoreBiomeAt != null)
        {
            // Flatten beaches above a threshold, creates cliffs where the beach ends
            double aboveWaterDelta = actualHeight - shoreHeight / shoreWeight;
            if (aboveWaterDelta > 0)
            {
                if (aboveWaterDelta > 20)
                {
                    aboveWaterDelta = 20;
                }
                double adjustedAboveWaterDelta = 0.02 * aboveWaterDelta * (40 - aboveWaterDelta) - 0.48;
                actualHeight = shoreHeight / shoreWeight + adjustedAboveWaterDelta;
            }
            biomeAt = shoreBiomeAt;
        }

        if (updateArrays)
        {
            localBiomes[localX + 16 * localZ] = biomeAt;
            surfaceHeight[localX + 16 * localZ] = (int) actualHeight;
        }

        if (biomeAt == null)
        {
            throw new NullPointerException();
        }
        return actualHeight;
    }

    double calculateNoiseAtHeight(int y, double heightNoiseValue)
    {
        double noise = 0;
        for (Object2DoubleMap.Entry<BiomeNoiseSampler> entry : biomeSamplers.object2DoubleEntrySet())
        {
            final BiomeNoiseSampler sampler = entry.getKey();
            noise += sampler.noise(y) * entry.getDoubleValue();
        }

        noise = 0.4f - noise; // > 0 is solid
        if (y > heightNoiseValue)
        {
            noise -= (y - heightNoiseValue) * 0.2f; // Quickly drop noise down to zero if we're above the expected height
        }

        return Mth.clamp(noise, -1, 1);
    }

    BlockState modifyNoiseAndGetState(Aquifer aquifer, ChunkBaseBlockSource stoneSource, int x, int y, int z, double noise)
    {
        // todo: what?
        return chunkSampler.sample(x, y, z, noise);
        //noise = noodleCaveSampler.sample(noise, x, y, z, minY, cellDeltaZ);
        //noise = Math.min(noise, cavifierSampler.sample(cellDeltaZ));
        //return aquifer.computeState(stoneSource, x, y, z, noise);
    }

    /**
     * Initializes enough to call {@link #sampleColumnHeightAndBiome(Object2DoubleMap, boolean)}
     */
    void setupColumn(int x, int z)
    {
        this.x = x;
        this.z = z;
        this.localX = x & 15;
        this.localZ = z & 15;
    }
}
