/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.Map;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.fluids.RiverWaterFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.BiomeSourceExtension;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.noise.ChunkNoiseSamplingSettings;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.NoiseSampler;
import net.dries007.tfc.world.noise.TrilinearInterpolator;
import net.dries007.tfc.world.noise.TrilinearInterpolatorList;
import net.dries007.tfc.world.region.RegionPartition;
import net.dries007.tfc.world.region.RiverEdge;
import net.dries007.tfc.world.region.Units;
import net.dries007.tfc.world.river.Flow;
import net.dries007.tfc.world.river.MidpointFractal;
import net.dries007.tfc.world.river.RiverBlendType;
import net.dries007.tfc.world.river.RiverInfo;
import net.dries007.tfc.world.river.RiverNoiseSampler;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public class ChunkNoiseFiller extends ChunkHeightFiller
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
    private final ProtoChunk chunk;
    private final int chunkMinX, chunkMinZ; // Min block positions for the chunk
    private final Heightmap oceanFloor, worldSurface;
    private final CarvingMask airCarvingMask; // Only air carving mask is marked

    // Rivers
    private final Beardifier beardifier;
    private final MutableDensityFunctionContext mutableDensityFunctionContext;
    private final FluidState riverWater;
    private final @Nullable RiverInfo[] riverData; // 16 x 16 river info. May be null.
    private final Flow[] riverFlows; // 5 x 5 quart position sampled, pre-interpolated river flows. Not null.

    // Noise interpolation
    private final ChunkNoiseSamplingSettings settings;
    private final TrilinearInterpolatorList interpolator;

    // Noise Caves
    private final TrilinearInterpolator noiseCaves;

    // Noodle Caves
    private final TrilinearInterpolator noodleToggle;
    private final TrilinearInterpolator noodleThickness;
    private final TrilinearInterpolator noodleRidgeA;
    private final TrilinearInterpolator noodleRidgeB;

    // Aquifer + Noise -> BlockState
    private final TFCAquifer aquifer;
    private final ChunkBaseBlockSource baseBlockSource;

    private final int[] surfaceHeight; // 16x16, block pos resolution
    private final BiomeExtension[] localBiomes; // 16x16, block pos resolution
    private final BiomeExtension[] localBiomesNoRivers; // 16x16, block pos resolution
    private final double[] localBiomeWeights; // 16x16, block pos resolution

    // Current local position / context
    private double cellDeltaX, cellDeltaZ; // Delta within a noise cell
    private int lastCellZ; // Last cell Z, needed due to a quick in noise interpolator

    public ChunkNoiseFiller(
        ProtoChunk chunk,
        Object2DoubleMap<BiomeExtension>[] sampledBiomeWeights,
        BiomeSourceExtension biomeSource,
        Map<BiomeExtension, BiomeNoiseSampler> biomeNoiseSamplers,
        Map<RiverBlendType, RiverNoiseSampler> riverNoiseSamplers,
        Noise2D shoreSampler,
        NoiseSampler sampler,
        ChunkBaseBlockSource baseBlockSource,
        ChunkNoiseSamplingSettings settings,
        int seaLevel,
        Beardifier beardifier
    )
    {
        super(sampledBiomeWeights, biomeSource, biomeNoiseSamplers, riverNoiseSamplers, shoreSampler, seaLevel);

        this.chunk = chunk;
        this.chunkMinX = chunk.getPos().getMinBlockX();
        this.chunkMinZ = chunk.getPos().getMinBlockZ();
        this.oceanFloor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        this.worldSurface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        this.airCarvingMask = chunk.getOrCreateCarvingMask(GenerationStep.Carving.AIR);

        this.beardifier = beardifier;
        this.mutableDensityFunctionContext = new MutableDensityFunctionContext(new BlockPos.MutableBlockPos());
        this.riverWater = TFCFluids.RIVER_WATER.get().defaultFluidState();
        this.riverData = new RiverInfo[16 * 16];
        this.riverFlows = new Flow[5 * 5];

        sampleRiverData();

        this.settings = settings;
        this.interpolator = TrilinearInterpolatorList.create(settings);
        this.baseBlockSource = baseBlockSource;

        // Noise Caves
        this.noiseCaves = interpolator.add(sampler.noiseCaves);

        // Noodle Caves
        this.noodleToggle = interpolator.add(sampler.noodleToggle);
        this.noodleThickness = interpolator.add(sampler.noodleThickness);
        this.noodleRidgeA = interpolator.add(sampler.noodleRidgeA);
        this.noodleRidgeB = interpolator.add(sampler.noodleRidgeB);

        // Aquifer
        this.aquifer = new TFCAquifer(chunk.getPos(), settings, baseBlockSource, seaLevel, sampler.positionalRandomFactory, sampler.barrierNoise);

        this.surfaceHeight = new int[16 * 16];
        this.localBiomes = new BiomeExtension[16 * 16];
        this.localBiomesNoRivers = new BiomeExtension[16 * 16];
        this.localBiomeWeights = new double[16 * 16];
    }

    public TFCAquifer aquifer()
    {
        return aquifer;
    }

    public int[] surfaceHeight()
    {
        return surfaceHeight;
    }

    public BiomeExtension[] localBiomes()
    {
        return localBiomes;
    }

    public BiomeExtension[] localBiomesNoRivers()
    {
        return localBiomesNoRivers;
    }

    public double[] localBiomeWeights()
    {
        return localBiomeWeights;
    }

    /**
     * Sample biomes and height per chunk
     * Computes the surface height and local biome arrays
     * Initializes aquifer positions based on the surface height
     */
    public void sampleAquiferSurfaceHeight(Sampler<BiomeExtension> biomeSampler)
    {
        final boolean debugAquiferSurfaceHeight = false;

        // Initialize aquifer with surface height
        // The aquifer needs a 4x4, chunk pos resolution of the estimated maximum allowable aquifer height
        // At each position, we sample from a 5x5, at half chunk resolution (so a full chunk's distance away)
        // This requires sampling, in total, at half chunk resolution, from the (-2, -2) chunk to the (+3, +3) chunk, inclusive.
        // The total sampled area is 11x11
        final double[] sampledHeight = new double[11 * 11];
        final int[] aquiferSurfaceHeights = aquifer.surfaceHeights();

        for (int x = 0; x < 11; x++)
        {
            for (int z = 0; z < 11; z++)
            {
                final int actualX = chunkMinX - 32 + (x << 3);
                final int actualZ = chunkMinZ - 32 + (z << 3);
                final BiomeExtension biome = biomeSampler.get(actualX, actualZ);
                final BiomeNoiseSampler sampler = biomeNoiseSamplers.get(biome);

                double aquiferSurfaceHeight = biome.getAquiferSurfaceHeight(sampler, actualX, actualZ);

                if (aquiferSurfaceHeight > seaLevel - 24 && sampleRiverDistSq(actualX, actualZ) < 15 * 15)
                {
                    // When near a river, force aquifers below the river in a wide radius (15 blocks)
                    aquiferSurfaceHeight = seaLevel - 24;
                }

                if (aquiferSurfaceHeight > seaLevel)
                {
                    // Above sea level, we reduce the overall height *above* sea level, to more eagerly prevent above-surface level fluid aquifers
                    aquiferSurfaceHeight = 0.3 * seaLevel + 0.7 * aquiferSurfaceHeight;
                }
                sampledHeight[x + 11 * z] = aquiferSurfaceHeight;
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
                        minAquiferSurfaceHeight = Math.min(minAquiferSurfaceHeight, sampledHeight[(xIndex + dx) + 11 * (zIndex + dz)]);
                    }
                }

                aquiferSurfaceHeights[x + 4 * z] = (int) minAquiferSurfaceHeight;
            }
        }

        if (debugAquiferSurfaceHeight)
        {
            for (int x = 0; x < 16; x++)
            {
                for (int z = 0; z < 16; z++)
                {
                    setupColumn(x, z);
                    setDebugState((int) sampledHeight[((x >> 3) + 4) + 11 * ((z >> 3) + 4)], Blocks.YELLOW_STAINED_GLASS); // Sampled height
                    setDebugState(aquiferSurfaceHeights[5], Blocks.LIME_STAINED_GLASS); // Aquifer surface height, per-chunk basis
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
    public double[] createSlopeMap()
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
     * Fills the entire chunk
     */
    public void fillFromNoise()
    {
        interpolator.initializeForFirstCellX();
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int cellX = 0; cellX < settings.cellCountXZ(); cellX++)
        {
            interpolator.advanceCellX(cellX);
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
            interpolator.swapSlices();
        }
    }

    /**
     * Fills a single column
     */
    private void fillColumn(BlockPos.MutableBlockPos cursor, int cellX, int cellZ)
    {
        final boolean debugFillColumn = false;

        prepareColumnBiomeWeights();
        sampleColumnHeightAndBiome(biomeWeights1, true);

        final int localIndex = localX + 16 * localZ;
        final int heightNoiseValue = surfaceHeight[localIndex]; // sample height, using the just-computed biome weights
        final BiomeExtension localBiome = localBiomes[localIndex];

        final Flow flow = localBiome.hasRivers() ? calculateFlowAt(cellX, cellZ) : Flow.NONE;

        final int maxFilledY = 1 + Math.max(heightNoiseValue, seaLevel);
        final int maxFilledCellY = Math.min(settings.cellCountY() - 1, 1 + Math.floorDiv(maxFilledY, settings.cellHeight()) - settings.firstCellY());
        final int maxFilledSectionY = Math.min(chunk.getSectionsCount() - 1, 1 + chunk.getSectionIndex(maxFilledY));

        // Top down iteration
        // 1. We need to mark exposed air below the first solid ground as carving mask applicable.
        // 2. We need to record the highest height (be it water or solid) for height map creation
        boolean topBlockPlaced = false;
        boolean topSolidBlockPlaced = false;

        LevelChunkSection section = chunk.getSection(maxFilledSectionY);
        int lastSectionIndex = maxFilledSectionY;
        for (int cellY = maxFilledCellY; cellY >= 0; --cellY)
        {
            interpolator.selectCellYZ(cellY, lastCellZ);
            interpolator.updateForXZ(cellDeltaX, cellDeltaZ);

            for (int localCellY = settings.cellHeight() - 1; localCellY >= 0; --localCellY)
            {
                final int y = (settings.firstCellY() + cellY) * settings.cellHeight() + localCellY;
                if (y >= maxFilledY)
                {
                    // skip this height. This helps to prevent over-height aquifers where possible.
                    continue;
                }

                final int localY = y & 15;
                final int sectionIndex = chunk.getSectionIndex(y);
                if (lastSectionIndex != sectionIndex)
                {
                    section = chunk.getSection(sectionIndex);
                    lastSectionIndex = sectionIndex;
                }

                final double cellDeltaY = (double) localCellY / settings.cellHeight();

                interpolator.updateForY(cellDeltaY);

                final double noise = calculateNoiseAtHeight(y, heightNoiseValue);
                final BlockState state = calculateBlockStateAtNoise(y, noise);
                final FluidState fluid = state.getFluidState();

                if (debugFillColumn && y < heightNoiseValue && noise < 0)
                {
                    // Below surface height, that has been carved out by BiomeNoiseSampler carving (not caves)
                    setDebugState(y, Blocks.RED_STAINED_GLASS);
                }

                // Set block
                cursor.setY(y);
                if (!state.isAir())
                {
                    // Need to account for underground rivers in this y level check, thus the smaller value between sea level and height noise
                    if (fluid.getType() == Fluids.WATER && flow != Flow.NONE && y >= Math.min(seaLevel - 4, heightNoiseValue))
                    {
                        // Place a flowing fluid block according to the river flow at this location
                        if (debugFillColumn)
                        {
                            setDebugState(y, Blocks.GREEN_STAINED_GLASS);
                        }
                        else
                        {
                            section.setBlockState(localX, localY, localZ, riverWater.setValue(RiverWaterFluid.FLOW, flow).createLegacyBlock(), false);
                        }
                    }
                    else
                    {
                        if (debugFillColumn)
                        {
                            if (fluid.getType() == Fluids.WATER)
                            {
                                setDebugState(y, Blocks.LIGHT_BLUE_STAINED_GLASS);
                            }
                            else if (fluid.getType() == Fluids.LAVA)
                            {
                                setDebugState(y, Blocks.ORANGE_STAINED_GLASS);
                            }
                            else if (fluid.getType() == TFCFluids.SALT_WATER.getSource() && y >= SEA_LEVEL_Y - 1)
                            {
                                setDebugState(y, Blocks.BLUE_STAINED_GLASS);
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
                                setDebugState(y, Blocks.LIGHT_GRAY_STAINED_GLASS);
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

                if (debugFillColumn && y == heightNoiseValue)
                {
                    setDebugState(y, Blocks.BLACK_STAINED_GLASS);
                }
            }

            if (debugFillColumn)
            {
                setPerColumnDebugStates();
            }
        }
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

        // Apply transformations from rivers
        // Each river blend type applies to the initial noise value, and then is weighted by its blend weight
        final double initialNoise = noise;
        noise = 0;
        for (RiverBlendType type : RiverBlendType.ALL)
        {
            final double weight = riverBlendWeights[type.ordinal()];
            if (type == RiverBlendType.NONE)
            {
                noise += weight * initialNoise;
            }
            else if (weight > 0)
            {
                final RiverNoiseSampler sampler = riverNoiseSamplers.get(type);
                noise += weight * sampler.noise(y, initialNoise);
            }
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
    private BlockState calculateBlockStateAtNoise(int y, double terrainNoise)
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
        mutableDensityFunctionContext.cursor().set(blockX, y, blockZ);
        terrainAndCaveNoise += beardifier.compute(mutableDensityFunctionContext);

        final BlockState aquiferState = aquifer.sampleState(blockX, y, blockZ, terrainAndCaveNoise);
        if (aquiferState != null)
        {
            return aquiferState;
        }
        return baseBlockSource.getBaseBlock(blockX, y, blockZ);
    }

    @Nullable
    @Override
    protected RiverInfo sampleRiverInfo(boolean useCache)
    {
        return useCache
            ? riverData[localX + 16 * localZ]
            : sampleRiverEdge(biomeSource.getPartition(blockX, blockZ));
    }

    @Override
    protected void updateLocalCaches(Object2DoubleMap<BiomeExtension> biomeWeights, BiomeExtension biomeAt, @Nullable RiverInfo info, double height)
    {
        final int localIndex = localX + 16 * localZ;

        localBiomesNoRivers[localIndex] = biomeAt;
        if (height <= SEA_LEVEL_Y + 1 && info != null && info.normDistSq() < 1.1 && biomeAt.hasRivers())
        {
            biomeAt = TFCBiomes.RIVER;
        }

        localBiomes[localIndex] = biomeAt;
        localBiomeWeights[localIndex] = biomeWeights.getOrDefault(biomeAt, 0.5);
        surfaceHeight[localIndex] = (int) height;

        baseBlockSource.useAccurateBiome(localX, localZ, biomeAt);
    }

    private void sampleRiverData()
    {
        // Despite sampling river information on a per-block scale, flow gets sampled on a quart scale and interpolated
        // It looks better this way, as flow is not otherwise interpolated and this avoids some harsher transitions between line segments.

        final RegionPartition.Point point = biomeSource.getPartition(chunkMinX, chunkMinZ);

        // Sample on a per-block scale, copying the flow into the quart-scale flows as well
        for (int localX = 0; localX < 16; localX++)
        {
            for (int localZ = 0; localZ < 16; localZ++)
            {
                setupColumn(chunkMinX + localX, chunkMinZ + localZ);
                final RiverInfo info = sampleRiverEdge(point);

                riverData[localX + 16 * localZ] = info;
            }
        }

        // Sample the remaining points outside the chunk (on the right / down edge)
        for (int quartX = 0; quartX < 5; quartX++)
        {
            for (int quartZ = 0; quartZ < 5; quartZ++)
            {
                final int localX = quartX << 2;
                final int localZ = quartZ << 2;

                // Copy from the river data, if in range.
                // Technically there is an edge case were the partition point is actually one block out of range, but it shouldn't matter
                final RiverInfo info;
                if (quartX < 4 && quartZ < 4)
                {
                    info = riverData[localX + 16 * localZ];
                }
                else
                {
                    setupColumn(chunkMinX + localX, chunkMinZ + localZ);
                    info = sampleRiverEdge(point);
                }

                riverFlows[quartX + 5 * quartZ] = info != null && info.normDistSq() < 0.28 ? info.flow() : Flow.NONE;
            }
        }
    }

    private double sampleRiverDistSq(int blockX, int blockZ)
    {
        final RegionPartition.Point point = biomeSource.getPartition(blockX, blockZ);

        double minDist = Float.MAX_VALUE;

        double exactGridX = Units.blockToGridExact(blockX);
        double exactGridZ = Units.blockToGridExact(blockZ);

        for (RiverEdge edge : point.rivers())
        {
            final MidpointFractal fractal = edge.fractal();
            if (fractal.maybeIntersect(exactGridX, exactGridZ, minDist))
            {
                double dist = fractal.intersectDistance(exactGridX, exactGridZ);
                if (dist < minDist)
                {
                    minDist = dist;
                }
            }
        }
        return minDist * Units.GRID_WIDTH_IN_BLOCK * Units.GRID_WIDTH_IN_BLOCK;
    }

    private Flow calculateFlowAt(int cellX, int cellZ)
    {
        // Interpolate flow for this column
        final Flow flow00 = riverFlows[cellX + 5 * cellZ];
        final Flow flow10 = riverFlows[cellX + 5 * (cellZ + 1)];
        final Flow flow01 = riverFlows[(cellX + 1) + 5 * cellZ];
        final Flow flow11 = riverFlows[(cellX + 1) + 5 * (cellZ + 1)];

        return Flow.lerp(flow00, flow01, flow10, flow11, (float) cellDeltaX, (float) cellDeltaZ);
    }

    // ===== Debug =====

    private void setPerColumnDebugStates()
    {
        final RiverInfo river = riverData[localX + 16 * localZ];
        if (river != null)
        {
            int y = 130 + (int) (Math.sqrt(river.distSq() + 0.01f) * 0.5);
            if (y > 140) y = 140;
            setDebugState(y, Blocks.PURPLE_STAINED_GLASS); // Each block up is distance = 2

            y = 150 + (int) (Math.sqrt(river.normDistSq() + 0.01f) * 10f);
            if (y > 160) y = 160;
            setDebugState(y, Blocks.MAGENTA_STAINED_GLASS); // Each block up is 0.1 norm distance
        }

        // 120 shows river biomes as per the local biome source
        if (localBiomes[localX + 16 * localZ] == TFCBiomes.RIVER)
        {
            setDebugState(120, Blocks.MAGENTA_STAINED_GLASS); // River biome present + used in the local biomes
        }

        if (chunk.getNoiseBiome(localX >> 2, 120, localZ >> 2).value() == biomeSource.getBiomeFromExtension(TFCBiomes.RIVER).value())
        {
            setDebugState(115, Blocks.PINK_STAINED_GLASS); // River biome present in chunk
        }
    }

    private void setDebugState(int y, Block block)
    {
        chunk.getSection(chunk.getSectionIndex(y)).setBlockState(localX, y & 15, localZ, block.defaultBlockState(), false);
    }
}
