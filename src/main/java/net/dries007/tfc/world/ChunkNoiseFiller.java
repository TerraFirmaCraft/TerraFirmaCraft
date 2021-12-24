/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.GenerationStep;
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
import net.dries007.tfc.world.noise.ChunkNoiseSamplingSettings;
import net.dries007.tfc.world.noise.NoiseSampler;
import net.dries007.tfc.world.noise.TrilinearInterpolator;
import net.dries007.tfc.world.river.Flow;

public class ChunkNoiseFiller
{
    /**
     * Positions used by the derivative sampling map. This represents a 7x7 grid of 4x4 sub chunks / biome positions, where 0, 0 = -1, -1 relative to the target chunk.
     * Each two pairs of integers is a position, wrapping around the entire outside the chunk (not including the 4x4 of positions in the interior
     */
    public static final int[] EXTERIOR_POINTS = Util.make(new int[2 * (7 * 7 - 4 * 4)], array -> {
        int index = 0;
        for (int x = 0; x < 7; x++)
        {
            for (int z = 0; z < 7; z++)
            {
                if (x < 1 || z < 1 || x > 4 || z > 4)
                {
                    array[index++] = x;
                    array[index++] = z;
                }
            }
        }
    });

    public static final int EXTERIOR_POINTS_COUNT = EXTERIOR_POINTS.length >> 1;

    // Initialized from the chunk
    private final LevelAccessor level;
    private final ProtoChunk chunk;
    private final int chunkMinX, chunkMinZ; // Min block positions for the chunk
    private final int quartX, quartZ; // Min quart positions for the chunk
    private final Heightmap oceanFloor, worldSurface;
    private final CarvingMask airCarvingMask; // Only air carving mask is marked

    // Rivers
    private final RiverSource riverSource;
    private final FluidState riverWater;
    private final Flow[] flows;

    // Noise interpolation
    private final ChunkNoiseSamplingSettings settings;
    private final List<TrilinearInterpolator> interpolators;

    // Noise Caves
    private final TrilinearInterpolator noiseCaves;

    // Noodle Caves
    private final TrilinearInterpolator noodleToggle;
    private final TrilinearInterpolator noodleThickness;
    private final TrilinearInterpolator noodleRidgeA;
    private final TrilinearInterpolator noodleRidgeB;

    // Aquifer + Noise -> BlockState
    private final Aquifer aquifer;
    private final ChunkBaseBlockSource baseBlockSource;

    private final Map<BiomeVariants, BiomeNoiseSampler> biomeNoiseSamplers; // Biome -> Noise Samplers
    private final Object2DoubleMap<BiomeNoiseSampler> columnBiomeNoiseSamplers; // Per column weighted map of biome noises samplers

    private final Object2DoubleMap<Biome>[] sampledBiomeWeights; // 7x7 array of biome weights, at quart pos resolution
    private final Object2DoubleMap<Biome> biomeWeights1; // Local biome weights, for individual column adjustment

    private final int[] surfaceHeight; // 16x16, block pos resolution
    private final Biome[] localBiomes; // 16x16, block pos resolution

    // Current local position / context
    private int blockX, blockZ; // Absolute x/z positions
    private int localX, localZ; // Chunk-local x/z
    private double cellDeltaX, cellDeltaZ; // Delta within a noise cell
    private int lastCellZ; // Last cell Z, needed due to a quick in noise interpolator

    public ChunkNoiseFiller(LevelAccessor level, ProtoChunk chunk, Object2DoubleMap<Biome>[] sampledBiomeWeights, RiverSource riverSource, Map<BiomeVariants, BiomeNoiseSampler> biomeNoiseSamplers, NoiseSampler sampler, ChunkBaseBlockSource baseBlockSource, ChunkNoiseSamplingSettings settings, int seaLevel)
    {
        this.level = level;
        this.chunk = chunk;
        this.chunkMinX = chunk.getPos().getMinBlockX();
        this.chunkMinZ = chunk.getPos().getMinBlockZ();
        this.quartX = QuartPos.fromBlock(chunkMinX);
        this.quartZ = QuartPos.fromBlock(chunkMinZ);
        this.oceanFloor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        this.worldSurface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        this.airCarvingMask = chunk.getOrCreateCarvingMask(GenerationStep.Carving.AIR);

        this.riverSource = riverSource;
        this.riverWater = TFCFluids.RIVER_WATER.get().defaultFluidState();
        this.flows = buildFlowMap();

        this.settings = settings;
        this.interpolators = new ArrayList<>();
        this.baseBlockSource = baseBlockSource;

        // Noise Caves
        this.noiseCaves = addInterpolator(sampler.noiseCaves);

        // Noodle Caves
        this.noodleToggle = addInterpolator(sampler.noodleToggle);
        this.noodleThickness = addInterpolator(sampler.noodleThickness);
        this.noodleRidgeA = addInterpolator(sampler.noodleRidgeA);
        this.noodleRidgeB = addInterpolator(sampler.noodleRidgeB);

        // Aquifer
        this.aquifer = new Aquifer(chunk.getPos(), settings, baseBlockSource, seaLevel, sampler.positionalRandomFactory, sampler.barrierNoise, sampler.fluidLevelFloodednessNoise, sampler.fluidLevelSpreadNoise, sampler.lavaNoise);

        this.biomeNoiseSamplers = biomeNoiseSamplers;
        this.columnBiomeNoiseSamplers = new Object2DoubleOpenHashMap<>();
        this.sampledBiomeWeights = sampledBiomeWeights;
        this.biomeWeights1 = new Object2DoubleOpenHashMap<>();

        this.surfaceHeight = new int[16 * 16];
        this.localBiomes = new Biome[16 * 16];
    }

    /**
     * Sample biomes and height per chunk
     * Computes the surface height and local biome arrays
     * Initializes aquifer positions based on the surface height
     */
    public void setupAquiferSurfaceHeight(Sampler<Biome> biomeSampler)
    {
        final boolean debugAquiferSurfaceHeight = false;

        // Initialize aquifer with surface height
        // The aquifer needs a 4x4, chunk pos resolution of the estimated maximum allowable aquifer height
        // At each position, we sample from a 5x5, at half chunk resolution (so a full chunk's distance away)
        // This requires sampling, in total, at half chunk resolution, from the (-2, -2) chunk to the (+3, +3) chunk, inclusive.
        // The total biome array is 11x11
        final BiomeVariants[] sampledBiomes = new BiomeVariants[11 * 11];
        final int[] aquiferSurfaceHeights = aquifer.getSurfaceHeights();

        for (int x = 0; x < 11; x++)
        {
            for (int z = 0; z < 11; z++)
            {
                final int actualX = chunkMinX - 32 + (x << 3);
                final int actualZ = chunkMinZ - 32 + (z << 3);
                final Biome biome = biomeSampler.get(actualX, actualZ);
                sampledBiomes[x + 11 * z] = TFCBiomes.getExtensionOrThrow(level, biome).variants();
            }
        }

        // Then, for each region in the 4x4 aquifer surface heights, we populate it based on a 5x5 surrounding area
        for (int x = 0; x < 4; x++)
        {
            for (int z = 0; z < 4; z++)
            {
                double minAquiferSurfaceHeight = Double.MAX_VALUE;

                final int xIndex = (1 + x) << 1;
                final int zIndex = (1 + z) << 1;

                for (int dx = -2; dx <= 2; dx++)
                {
                    for (int dz = -2; dz <= 2; dz++)
                    {
                        final int actualX = chunkMinX - 16 + SectionPos.sectionToBlockCoord(x) + (dx << 3);
                        final int actualZ = chunkMinZ - 16 + SectionPos.sectionToBlockCoord(z) + (dz << 3);
                        double aquiferSurfaceHeight;

                        final BiomeVariants biome = sampledBiomes[(xIndex + dx) + 11 * (zIndex + dz)];
                        if (biome.isRiver())
                        {
                            aquiferSurfaceHeight = TFCChunkGenerator.SEA_LEVEL_Y - 12;
                        }
                        else
                        {
                            // Typical biomes
                            final BiomeNoiseSampler sampler = biomeNoiseSamplers.get(biome);
                            sampler.setColumn(actualX, actualZ);
                            aquiferSurfaceHeight = Math.max(biome.getAquiferSurfaceHeight(sampler.height()), 0);

                            // todo: make this a derived property from BiomeVariants
                            // Biomes with unusually low altitudes
                            if (biome == TFCBiomes.SHORE || biome == TFCBiomes.LOW_CANYONS || biome == TFCBiomes.LOWLANDS || biome == TFCBiomes.LAKE)
                            {
                                aquiferSurfaceHeight -= 8;
                            }
                            // Biomes with generally high altitudes
                            if (biome == TFCBiomes.OLD_MOUNTAINS || biome == TFCBiomes.MOUNTAINS || biome == TFCBiomes.PLATEAU || biome == TFCBiomes.ROLLING_HILLS)
                            {
                                aquiferSurfaceHeight += 8;
                            }
                            if (biome.isSalty()) // Oceans and adjacent
                            {
                                aquiferSurfaceHeight -= 12;
                            }
                        }

                        minAquiferSurfaceHeight = Math.min(minAquiferSurfaceHeight, aquiferSurfaceHeight);
                    }
                }

                aquiferSurfaceHeights[x + 4 * z] = (int) minAquiferSurfaceHeight;
            }
        }

        if (debugAquiferSurfaceHeight)
        {
            final int y = aquiferSurfaceHeights[5]; // This chunk, at (1, 1)
            final LevelChunkSection section = chunk.getSection(chunk.getSectionIndex(y));
            for (int x = 0; x < 16; x++)
            {
                for (int z = 0; z < 16; z++)
                {
                    section.setBlockState(x, y & 15, z, Blocks.GREEN_STAINED_GLASS.defaultBlockState(), false);
                }
            }
        }
    }

    public int[] getSurfaceHeight()
    {
        return surfaceHeight;
    }

    public Biome[] getLocalBiomes()
    {
        return localBiomes;
    }

    /**
     * Fills the entire chunk
     */
    public void fillFromNoise()
    {
        initializeForFirstCellX();
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int cellX = 0; cellX < settings.cellCountXZ(); cellX++)
        {
            advanceCellX(cellX);
            for (int cellZ = 0; cellZ < settings.cellCountXZ(); cellZ++)
            {
                // skip cell Y
                for (int localCellX = 0; localCellX < settings.cellWidth(); localCellX++)
                {
                    blockX = chunkMinX + cellX * settings.cellWidth() + localCellX;
                    localX = blockX & 15;
                    cellDeltaX = (double) localCellX / settings.cellWidth();

                    // cannot update for x here because we first need to update for yz. So we do all three each time per cell
                    for (int localCellZ = 0; localCellZ < settings.cellWidth(); localCellZ++)
                    {
                        blockZ = chunkMinZ + cellZ * settings.cellWidth() + localCellZ;
                        lastCellZ = cellZ; // needed for the noise interpolator
                        localZ = blockZ & 15;
                        cellDeltaZ = (double) localCellZ / settings.cellWidth();

                        mutablePos.set(blockX, 0, blockZ);
                        fillColumn(mutablePos, cellX, cellZ);
                    }
                }
            }
            swapSlices();
        }
    }

    /**
     * Builds a 6x6, 4x4 resolution slope map for a chunk
     * This is enough to do basic linear interpolation for every point within the chunk.
     *
     * @return A measure of how slope-y the chunk is. Values roughly in [0, 13), although technically can be >13
     */
    @SuppressWarnings("PointlessArithmeticExpression")
    public double[] getSlopeMap()
    {
        final int[] quartSurfaceHeight = new int[7 * 7]; // 7x7, quart pos resolution

        // Interior points - record from the existing positions in the chunk
        for (int x = 0; x < 4; x++)
        {
            for (int z = 0; z < 4; z++)
            {
                // Copy from surface height, where possible
                quartSurfaceHeight[(x + 1) + 7 * (z + 1)] = surfaceHeight[(x << 2) + 16 * (z << 2)];
            }
        }

        // Exterior points
        for (int i = 0; i < EXTERIOR_POINTS_COUNT; i++)
        {
            int x = EXTERIOR_POINTS[i << 1];
            int z = EXTERIOR_POINTS[(i << 1) | 1];

            int x0 = chunkMinX + ((x - 1) << 2);
            int z0 = chunkMinZ + ((z - 1) << 2);

            setupColumn(x0, z0);
            quartSurfaceHeight[x + 7 * z] = (int) sampleColumnHeightAndBiome(sampledBiomeWeights[x + z * 7], false);
        }

        double[] slopeMap = new double[6 * 6];
        for (int x = 0; x < 6; x++)
        {
            for (int z = 0; z < 6; z++)
            {
                // Math people (including myself) cry at what I'm calling 'the derivative'
                final double nw = quartSurfaceHeight[(x + 0) + 7 * (z + 0)];
                final double ne = quartSurfaceHeight[(x + 1) + 7 * (z + 0)];
                final double sw = quartSurfaceHeight[(x + 0) + 7 * (z + 1)];
                final double se = quartSurfaceHeight[(x + 1) + 7 * (z + 1)];

                final double center = (nw + ne + sw + se) / 4;
                final double slope = Math.abs(nw - center) + Math.abs(ne - center) + Math.abs(sw - center) + Math.abs(se - center);
                slopeMap[x + 6 * z] = slope;
            }
        }
        return slopeMap;
    }

    /**
     * Fills a single column
     *
     * Deprecation for the use of {@link BlockState#getLightEmission()}
     */
    @SuppressWarnings("deprecation")
    private void fillColumn(BlockPos.MutableBlockPos cursor, int cellX, int cellZ)
    {
        final boolean debugFillColumn = false;

        prepareColumnBiomeWeights();
        sampleColumnHeightAndBiome(biomeWeights1, true);

        final int heightNoiseValue = surfaceHeight[localX + 16 * localZ]; // sample height, using the just-computed biome weights
        final Flow flow = calculateFlowAt(cellX, cellZ);

        final int maxFilledY = 1 + Math.max(heightNoiseValue, TFCChunkGenerator.SEA_LEVEL_Y);
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
            selectCellYZ(cellY, lastCellZ);
            updateForXZ(cellDeltaX, cellDeltaZ);

            for (int localCellY = settings.cellHeight() - 1; localCellY >= 0; --localCellY)
            {
                final int y = (settings.firstCellY() + cellY) * settings.cellHeight() + localCellY;
                final int localY = y & 15;
                final int sectionIndex = chunk.getSectionIndex(y);
                if (chunk.getSectionIndex(section.bottomBlockY()) != sectionIndex)
                {
                    section = chunk.getSection(sectionIndex);
                }

                final double cellDeltaY = (double) localCellY / settings.cellHeight();

                updateForY(cellDeltaY);

                final double noise = calculateNoiseAtHeight(y, heightNoiseValue);
                final BlockState state = calculateBlockStateAtNoise(blockX, y, blockZ, noise);
                final FluidState fluid = state.getFluidState();

                if (debugFillColumn && y < heightNoiseValue && noise < 0)
                {
                    // Below surface height, that has been carved out by BiomeNoiseSampler carving (not caves)
                    section.setBlockState(localX, localY, localZ, Blocks.RED_STAINED_GLASS.defaultBlockState(), false);
                }

                // Set block
                cursor.setY(y);
                if (!state.isAir())
                {
                    // Need to account for underground rivers in this y level check, thus the smaller value between sea level and height noise
                    if (fluid.getType() == Fluids.WATER && flow != Flow.NONE && y >= Math.min(TFCChunkGenerator.SEA_LEVEL_Y - 4, heightNoiseValue))
                    {
                        // Place a flowing fluid block according to the river flow at this location
                        section.setBlockState(localX, localY, localZ, debugFillColumn ? Blocks.BLUE_STAINED_GLASS.defaultBlockState() : riverWater.setValue(RiverWaterFluid.FLOW, flow).createLegacyBlock(), false);
                    }
                    else
                    {
                        if (debugFillColumn)
                        {
                            if (fluid.getType() == Fluids.WATER)
                            {
                                section.setBlockState(localX, localY, localZ, Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState(), false);
                            }
                            else if (fluid.getType() == Fluids.LAVA)
                            {
                                section.setBlockState(localX, localY, localZ, Blocks.ORANGE_STAINED_GLASS.defaultBlockState(), false);
                            }
                        }
                        else
                        {
                            section.setBlockState(localX, localY, localZ, state, false);
                        }
                    }
                    if (aquifer.shouldScheduleFluidUpdate() && !fluid.isEmpty())
                    {
                        chunk.markPosForPostprocessing(cursor);
                    }

                    // Handle lava
                    if (state.getLightEmission() != 0)
                    {
                        chunk.addLight(cursor);
                    }
                }

                // Update heightmaps and carving masks
                if (state.isAir()) // Air
                {
                    if (topSolidBlockPlaced)
                    {
                        // Air under solid blocks, so mark as carved, and replace with cave air
                        airCarvingMask.set(blockX, y, blockZ);
                        if (debugFillColumn)
                        {
                            if (section.getBlockState(localX, localY, localZ).isAir())
                            {
                                section.setBlockState(localX, localY, localZ, Blocks.LIGHT_GRAY_STAINED_GLASS.defaultBlockState(), false);
                            }
                        }
                        else
                        {
                            section.setBlockState(localX, localY, localZ, Blocks.CAVE_AIR.defaultBlockState(), false);
                        }
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
                        airCarvingMask.set(blockX, y, blockZ);
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

                if (debugFillColumn && y == heightNoiseValue + 1)
                {
                    section.setBlockState(localX, localY, localZ, Blocks.BLACK_STAINED_GLASS.defaultBlockState(), false);
                }
            }
        }
    }

    private Flow[] buildFlowMap()
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

    private Flow calculateFlowAt(int cellX, int cellZ)
    {
        if (TFCBiomes.getExtensionOrThrow(level, localBiomes[localX + 16 * localZ]).variants().isRiver())
        {
            // Interpolate flow for this column
            final Flow flow00 = flows[cellX + 5 * cellZ];
            final Flow flow10 = flows[cellX + 5 * (cellZ + 1)];
            final Flow flow01 = flows[(cellX + 1) + 5 * cellZ];
            final Flow flow11 = flows[(cellX + 1) + 5 * (cellZ + 1)];

            return Flow.lerp(flow00, flow01, flow10, flow11, (float) cellDeltaX, (float) cellDeltaZ);
        }
        return Flow.NONE;
    }

    /**
     * Initializes {@link #biomeWeights1} from the sampled biome weights
     */
    private void prepareColumnBiomeWeights()
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
    private double sampleColumnHeightAndBiome(Object2DoubleMap<Biome> biomeWeights, boolean updateArrays)
    {
        columnBiomeNoiseSamplers.clear();

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
            final BiomeVariants variants = TFCBiomes.getExtensionOrThrow(level, entry.getKey()).variants();
            final BiomeNoiseSampler sampler = biomeNoiseSamplers.get(variants);

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

        assert biomeAt != null;
        return actualHeight;
    }

    /**
     * @param y The y position
     * @param heightNoiseValue The calculated average height noise, from {@link BiomeNoiseSampler#height()}
     * @return The density noise for the given y position, where positive noise is solid, in the range [0, 1]
     */
    private double calculateNoiseAtHeight(int y, double heightNoiseValue)
    {
        double noise = 0;
        for (Object2DoubleMap.Entry<BiomeNoiseSampler> entry : columnBiomeNoiseSamplers.object2DoubleEntrySet())
        {
            // Positive values = air
            final BiomeNoiseSampler sampler = entry.getKey();
            noise += sampler.noise(y) * entry.getDoubleValue();
        }

        noise = BiomeNoiseSampler.AIR_THRESHOLD - noise; // Positive noise = solid
        if (y > heightNoiseValue)
        {
            // Slide down if we're above the expected height
            noise -= (y - heightNoiseValue) * 0.2f;
        }

        return Mth.clamp(noise, -1, 1);
    }

    /**
     * @param terrainNoise The terrain noise for the position. Positive values indicate solid terrain, in the range [-1, 1]
     * @return The block state for the position, including the aquifer, noise and noodle caves, and terrain.
     */
    public BlockState calculateBlockStateAtNoise(int x, int y, int z, double terrainNoise)
    {
        double terrainAndCaveNoise = terrainNoise;
        if (noodleToggle.sample() >= 0)
        {
            final double thickness = Mth.clampedMap(noodleThickness.sample(), -1, 1, 0.05, 0.1);
            final double ridgeA = Math.abs(1.5 * noodleRidgeA.sample()) - thickness;
            final double ridgeB = Math.abs(1.5 * noodleRidgeB.sample()) - thickness;
            final double ridge = Math.max(ridgeA, ridgeB);

            terrainAndCaveNoise = Math.min(terrainAndCaveNoise, ridge);
        }

        terrainAndCaveNoise = Math.min(terrainAndCaveNoise, noiseCaves.sample());

        final BlockState aquiferState = aquifer.sampleState(x, y, z, terrainAndCaveNoise);
        return Objects.requireNonNullElseGet(aquiferState, () -> baseBlockSource.getBaseBlock(x, y, z));

    }

    /**
     * Initializes enough to call {@link #sampleColumnHeightAndBiome(Object2DoubleMap, boolean)}
     */
    private void setupColumn(int x, int z)
    {
        this.blockX = x;
        this.blockZ = z;
        this.localX = x & 15;
        this.localZ = z & 15;
    }

    // Noise Interpolator helper methods

    private TrilinearInterpolator addInterpolator(TrilinearInterpolator.Source source)
    {
        final TrilinearInterpolator interpolator = new TrilinearInterpolator(settings, source);
        interpolators.add(interpolator);
        return interpolator;
    }

    private void initializeForFirstCellX()
    {
        interpolators.forEach(TrilinearInterpolator::initializeForFirstCellX);
    }

    private void advanceCellX(final int cellX)
    {
        interpolators.forEach(i -> i.advanceCellX(cellX));
    }

    private void selectCellYZ(final int cellY, final int cellZ)
    {
        interpolators.forEach(i -> i.selectCellYZ(cellY, cellZ));
    }

    private void updateForXZ(final double x, final double z)
    {
        interpolators.forEach(i -> i.updateForXZ(x, z));
    }

    private void updateForY(final double y)
    {
        interpolators.forEach(i -> i.updateForY(y));
    }

    private void swapSlices()
    {
        interpolators.forEach(TrilinearInterpolator::swapSlices);
    }
}
