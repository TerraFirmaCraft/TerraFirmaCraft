/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.*;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.dries007.tfc.world.biome.*;
import net.dries007.tfc.world.chunkdata.*;
import net.dries007.tfc.world.surfacebuilder.SurfaceBuilderContext;

import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.level.levelgen.synth.*;

public class TFCChunkGenerator extends ChunkGenerator implements ChunkGeneratorExtension
{
    public static final Codec<TFCChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BiomeSource.CODEC.fieldOf("biome_source").forGetter(c -> c.biomeSource),
        NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(c -> c.settings),
        Codec.BOOL.fieldOf("flat_bedrock").forGetter(c -> c.flatBedrock),
        Codec.LONG.fieldOf("seed").forGetter(c -> c.seed)
    ).apply(instance, TFCChunkGenerator::new));

    public static final int SEA_LEVEL_Y = 63; // Matches vanilla

    public static final double[] KERNEL_9x9 = makeKernel((x, z) -> 0.0211640211641D * (1 - 0.03125D * (z * z + x * x)), 4);
    public static final double[] KERNEL_5x5 = makeKernel((x, z) -> 0.08D * (1 - 0.125D * (z * z + x * x)), 2);

    /**
     * Positions used by the derivative sampling map. This represents a 7x7 grid of 4x4 sub chunks / biome positions, where 0, 0 = -1, -1 relative to the target chunk.
     * Each two pairs of integers is a position, wrapping around the entire outside of the chunk (not including the 4x4 of positions in the interior
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

    public static double[] makeKernel(ToDoubleBiFunction<Integer, Integer> func, int radius)
    {
        final int size = radius * 2 + 1;
        final double[] array = new double[size * size];
        double sum = 0;
        for (int x = 0; x < size; x++)
        {
            for (int z = 0; z < size; z++)
            {
                final double value = func.applyAsDouble(x - radius, z - radius);
                assert value >= 0 : "Invalid kernel value: " + value + " for x = " + x + ", z = " + z;
                array[x + z * size] = value;
                sum += value;
            }
        }
        assert 0.99 < sum && sum < 1.01 : "Invalid kernel sum: " + sum + " is not ~= 1.00";
        return array;
    }

    /**
     * Composes two levels of sampled weights. It takes two maps of two different resolutions, and re-weights the higher resolution one by replacing specific groups of samples with the respective weights from the lower resolution map.
     * Each element of the higher resolution map is replaced with a proportional average of the same group which is present in the lower resolution map.
     * This has the effect of blending specific groups at closer distances than others, allowing for both smooth and sharp biome transitions.
     *
     * Example:
     * - Low resolution: 30% Plains, 40% Mountains, 30% Hills, 10% River
     * - High resolution: 60% Plains, 40% River
     * - Groups are "River" and "Not River"
     * - For each element in the high resolution map:
     * - 60% Plains: Group "Not River", and is replaced with 60% * (30% Plains, 40% Mountains, 30% Hills) / 90%
     * - 50% River: Group "River", which is replaced with 40% * (10% River) / 10%
     * - Result: 18% Plains, 24% Mountains, 18% Hills, 40% River
     */
    public static <T> void composeSampleWeights(Object2DoubleMap<T> weightMap, Object2DoubleMap<T> groupWeightMap, ToIntFunction<T> groupFunction, int groups)
    {
        // First, we need to calculate the maximum weight per group
        double[] maxWeights = new double[groups];
        for (Object2DoubleMap.Entry<T> entry : groupWeightMap.object2DoubleEntrySet())
        {
            int group = groupFunction.applyAsInt(entry.getKey());
            if (group != -1)
            {
                maxWeights[group] += entry.getDoubleValue();
            }
        }

        // Then, we iterate through the smaller weight map and identify the actual weight that needs to be replaced with each group
        double[] actualWeights = new double[groups];
        ObjectIterator<Object2DoubleMap.Entry<T>> iterator = weightMap.object2DoubleEntrySet().iterator();
        while (iterator.hasNext())
        {
            Object2DoubleMap.Entry<T> entry = iterator.next();
            int group = groupFunction.applyAsInt(entry.getKey());
            if (group != -1)
            {
                actualWeights[group] += entry.getDoubleValue();
                iterator.remove();
            }
        }

        // Finally, insert the weights for each group as a portion of the actual weight
        for (Object2DoubleMap.Entry<T> entry : groupWeightMap.object2DoubleEntrySet())
        {
            int group = groupFunction.applyAsInt(entry.getKey());
            if (group != -1 && actualWeights[group] > 0 && maxWeights[group] > 0)
            {
                weightMap.put(entry.getKey(), entry.getDoubleValue() * actualWeights[group] / maxWeights[group]);
            }
        }
    }

    /**
     * This is the default instance used in the TFC preset, both on client and server
     */
    public static TFCChunkGenerator defaultChunkGenerator(Supplier<NoiseGeneratorSettings> noiseGeneratorSettings, Registry<Biome> biomeRegistry, long seed)
    {
        return new TFCChunkGenerator(TFCBiomeSource.defaultBiomeSource(seed, biomeRegistry), noiseGeneratorSettings, false, seed);
    }

    // Properties set from codec
    private final TFCBiomeSource specialBiomeSource; // narrowed to TFCBiomeSource
    private final Supplier<NoiseGeneratorSettings> settings;
    private final boolean flatBedrock;
    private final long seed;

    private final Map<BiomeVariants, BiomeNoiseSampler> biomeNoiseSamplers;
    private final SurfaceNoise surfaceDepthNoise;
    private final ChunkDataProvider chunkDataProvider;

    private final ThreadLocal<BiomeCache> biomeCache;

    private final int cellWidth; // The width of a noise cell
    private final int cellHeight; // The height of a noise cell
    private final int cellCountX;
    private final int cellCountY;
    private final int cellCountZ;

    private final NormalNoise aquiferBarrierNoise;
    private final NormalNoise aquiferWaterLevelNoise;
    private final NormalNoise aquiferLavaLevelNoise;

    private final Cavifier cavifier;
    private final NoodleCavifier noodleCavifier;

    @Nullable private NoiseGeneratorSettings cachedSettings;

    public TFCChunkGenerator(BiomeSource biomeSource, Supplier<NoiseGeneratorSettings> settings, boolean flatBedrock, long seed)
    {
        super(biomeSource, settings.get().structureSettings());

        if (!(biomeSource instanceof TFCBiomeSource))
        {
            throw new IllegalArgumentException("biome provider must extend " + TFCBiomeSource.class.getSimpleName());
        }

        this.settings = settings;
        this.cachedSettings = null;

        this.specialBiomeSource = (TFCBiomeSource) biomeSource;
        this.flatBedrock = flatBedrock;
        this.seed = seed;
        this.biomeNoiseSamplers = new HashMap<>();

        final WorldgenRandom random = new WorldgenRandom(seed);
        for (BiomeVariants variant : TFCBiomes.getVariants())
        {
            biomeNoiseSamplers.put(variant, variant.createNoiseSampler(seed));
        }

        final NoiseSettings noiseSettings = settings.get().noiseSettings();

        this.surfaceDepthNoise = new PerlinSimplexNoise(random, IntStream.rangeClosed(-3, 0));

        // Generators / Providers
        this.chunkDataProvider = new ChunkDataProvider(new TFCChunkDataGenerator(seed, random, this.specialBiomeSource.getLayerSettings())); // Chunk data
        this.specialBiomeSource.setChunkDataProvider(chunkDataProvider); // Allow biomes to use the chunk data temperature / rainfall variation
        this.biomeCache = ThreadLocal.withInitial(() -> new BiomeCache(8192, biomeSource));

        this.cellHeight = QuartPos.toBlock(noiseSettings.noiseSizeVertical());
        this.cellWidth = QuartPos.toBlock(noiseSettings.noiseSizeHorizontal());

        this.cellCountX = 16 / this.cellWidth;
        this.cellCountY = noiseSettings.height() / this.cellHeight;
        this.cellCountZ = 16 / this.cellWidth;

        this.aquiferBarrierNoise = NormalNoise.create(new SimpleRandomSource(random.nextLong()), -3, 1.0D);
        this.aquiferWaterLevelNoise = NormalNoise.create(new SimpleRandomSource(random.nextLong()), -3, 1.0D, 0.0D, 2.0D);
        this.aquiferLavaLevelNoise = NormalNoise.create(new SimpleRandomSource(random.nextLong()), -1, 1.0D, 0.0D);

        this.cavifier = new Cavifier(random, noiseSettings.minY() / this.cellHeight);
        this.noodleCavifier = new NoodleCavifier(seed);
    }

    @Override
    public ChunkDataProvider getChunkDataProvider()
    {
        return chunkDataProvider;
    }

    @Override
    protected Codec<TFCChunkGenerator> codec()
    {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seedIn)
    {
        return new TFCChunkGenerator(specialBiomeSource, settings, flatBedrock, seedIn);
    }

    public NoiseGeneratorSettings noiseGeneratorSettings()
    {
        if (cachedSettings == null)
        {
            cachedSettings = settings.get();
        }
        return cachedSettings;
    }

    @Override
    public void createBiomes(Registry<Biome> biomeIdRegistry, ChunkAccess chunk)
    {
        // todo: the column one seems to be a bit broken?
        ((ProtoChunk) chunk).setBiomes(new ChunkBiomeContainer(biomeIdRegistry, chunk, chunk.getPos(), specialBiomeSource));
    }

    /**
     * Noop - carvers are done at the beginning of feature stage, so the carver is free to check adjacent chunks for information
     */
    @Override
    public void applyCarvers(long worldSeed, BiomeManager biomeManagerIn, ChunkAccess chunkIn, GenerationStep.Carving step)
    {

    }

    /**
     * Surface is done in make base, bedrock is added here
     */
    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion world, ChunkAccess chunkIn)
    {
        final ProtoChunk chunk = (ProtoChunk) chunkIn;
        final ChunkPos chunkPos = chunk.getPos();
        final WorldgenRandom random = new WorldgenRandom();

        random.setBaseChunkSeed(chunkPos.x, chunkPos.z);
        makeBedrock(chunk, random);
    }

    @Override
    public TFCBiomeSource getBiomeSource()
    {
        return specialBiomeSource;
    }

    /**
     * This override just ignores strongholds conditionally as by default TFC does not generate them, but  {@link ChunkGenerator} hard codes them to generate.
     */
    @Override
    public void createStructures(RegistryAccess dynamicRegistry, StructureFeatureManager structureManager, ChunkAccess chunk, StructureManager templateManager, long seed)
    {
        final ChunkPos chunkPos = chunk.getPos();
        final Biome biome = this.specialBiomeSource.getNoiseBiome((chunkPos.x << 2) + 2, 0, (chunkPos.z << 2) + 2);
        for (Supplier<ConfiguredStructureFeature<?, ?>> supplier : biome.getGenerationSettings().structures())
        {
            // todo: mixin accessor
            this.createStructure(supplier.get(), dynamicRegistry, structureManager, chunk, templateManager, seed, biome);
        }
    }

    /**
     * This runs after biome generation. In order to do accurate surface placement, we build surfaces here as we can get better resolution than the default biome container based surface.
     */
    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, StructureFeatureManager structureFeatureManager, ChunkAccess chunkIn)
    {
        // Initialization
        final ProtoChunk chunk = (ProtoChunk) chunkIn;
        // todo: mixin accessor
        final LevelAccessor level = (LevelAccessor) chunk.levelHeightAccessor;
        final ChunkPos chunkPos = chunk.getPos();
        final WorldgenRandom random = new WorldgenRandom();
        final int chunkX = chunkPos.getMinBlockX(), chunkZ = chunkPos.getMinBlockZ();

        random.setBaseChunkSeed(chunkPos.x, chunkPos.z);

        final Biome[] localBiomes = new Biome[16 * 16];
        final double[] sampledHeightMap = new double[7 * 7];
        final int[] surfaceHeightMap = new int[16 * 16];

        final BiomeCache localBiomeCache = biomeCache.get();
        final ChunkBiomeContainer biomeContainer = chunk.getBiomes();
        assert biomeContainer != null;
        final Sampler<Biome> biomeAccessor = (x, z) -> {
            // First check the local chunk, if not then fallback to the cache
            if ((x >> 4) == chunkPos.x && (z >> 4) == chunkPos.z)
            {
                return biomeContainer.getNoiseBiome(x >> 2, 0, z >> 2);
            }
            return localBiomeCache.get(x >> 2, z >> 2);
        };

        final RockData rockData = chunkDataProvider.get(chunkPos).getRockDataOrThrow();

        // Set a reference to the surface height map, which the helper will modify later
        // Since we need surface height to query rock -> each block, it's set before iterating the column in the helper
        rockData.setSurfaceHeight(surfaceHeightMap);

        final Object2DoubleMap<Biome>[] biomeWeights = sampleBiomes(level, chunkPos, biomeAccessor);
        final FillFromNoiseHelper helper = new FillFromNoiseHelper(level, chunk, biomeWeights, surfaceHeightMap, localBiomes);

        @SuppressWarnings("ConstantConditions") // The aquifer never uses the sampler field, and we don't either.
        final Aquifer aquifer = Aquifer.create(chunkPos, aquiferBarrierNoise, aquiferWaterLevelNoise, aquiferLavaLevelNoise, noiseGeneratorSettings(), null, helper.minCellY * cellHeight, helper.noiseCellCountY * cellHeight);
        final RockLayerStoneSource stoneSource = new RockLayerStoneSource(chunkPos, rockData);

        helper.fillFromNoise(aquifer, stoneSource);

        // Fill in additional derivative sampling points
        for (int i = 0; i < EXTERIOR_POINTS_COUNT; i++)
        {
            int x = EXTERIOR_POINTS[i << 1];
            int z = EXTERIOR_POINTS[(i << 1) | 1];

            int x0 = chunkX + ((x - 1) << 2);
            int z0 = chunkZ + ((z - 1) << 2);

            helper.setupColumn(x0, z0);
            sampledHeightMap[x + 7 * z] = helper.sampleColumnHeightAndBiome(biomeWeights[x + z * 7], false);
        }

        final double[] slopeMap = buildSlopeMap(sampledHeightMap);

        // buildSurfaceWithContext(level, chunk, localBiomes, slopeMap, random);
        if (Debug.ENABLE_SLOPE_VISUALIZATION)
        {
            Debug.slopeVisualization(chunk, slopeMap, chunkX, chunkZ, this::sampleSlope);
        }

        // todo: multithreading?
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getSeaLevel()
    {
        return SEA_LEVEL_Y;
    }

    @Override
    public int getMinY()
    {
        return noiseGeneratorSettings().noiseSettings().minY();
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level)
    {
        return SEA_LEVEL_Y;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level)
    {
        return new NoiseColumn(0, new BlockState[0]);
    }

    /**
     * Samples the 'slope' value for a given coordinate within the chunk
     * Expected values are in [0, 13] but are practically unbounded above
     */
    @SuppressWarnings("PointlessArithmeticExpression")
    protected double sampleSlope(double[] slopeMap, int x, int z)
    {
        // compute slope contribution from lerp of corners
        final int offsetX = x + 2, offsetZ = z + 2;
        final int cellX = offsetX >> 2, cellZ = offsetZ >> 2;
        final double deltaX = ((double) offsetX - (cellX << 2)) * 0.25, deltaZ = ((double) offsetZ - (cellZ << 2)) * 0.25;

        double slope = 0;
        slope += slopeMap[(cellX + 0) + 6 * (cellZ + 0)] * (1 - deltaX) * (1 - deltaZ);
        slope += slopeMap[(cellX + 1) + 6 * (cellZ + 0)] * (deltaX) * (1 - deltaZ);
        slope += slopeMap[(cellX + 0) + 6 * (cellZ + 1)] * (1 - deltaX) * (deltaZ);
        slope += slopeMap[(cellX + 1) + 6 * (cellZ + 1)] * (deltaX) * (deltaZ);

        slope *= 0.8f;
        return slope;
    }

    protected Object2DoubleMap<Biome>[] sampleBiomes(LevelAccessor world, ChunkPos pos, Sampler<Biome> biomeSampler)
    {
        // First, sample biomes at chunk distance, in a 4x4 grid centered on the target chunk.
        // These are used to build the large-scale biome blending radius
        Object2DoubleMap<Biome>[] biomeWeights16 = newWeightArray(4 * 4);

        int chunkX = pos.getMinBlockX(), chunkZ = pos.getMinBlockZ();
        for (int x = 0; x < 4; x++)
        {
            for (int z = 0; z < 4; z++)
            {
                Object2DoubleMap<Biome> map = newWeightMap();

                for (int dx = 0; dx < 9; dx++)
                {
                    for (int dz = 0; dz < 9; dz++)
                    {
                        double weight = KERNEL_9x9[dx + dz * 9];
                        int x0 = chunkX + ((x + dx - 4) << 4);
                        int z0 = chunkZ + ((z + dz - 4) << 4);
                        Biome biome = biomeSampler.get(x0, z0);
                        map.mergeDouble(biome, weight, Double::sum);
                    }
                }

                biomeWeights16[x | (z << 2)] = map;
            }
        }

        Object2DoubleMap<Biome>[] biomeWeights1 = newWeightArray(7 * 7);
        Object2DoubleMap<Biome> biomeWeight16 = newWeightMap(), biomeWeight4 = newWeightMap();

        for (int x = 0; x < 7; x++)
        {
            for (int z = 0; z < 7; z++)
            {
                biomeWeight4.clear();

                for (int dx = 0; dx < 9; dx++)
                {
                    for (int dz = 0; dz < 9; dz++)
                    {
                        double weight = KERNEL_9x9[dx + dz * 9];
                        int x0 = chunkX + ((x + dx - 1) << 2);
                        int z0 = chunkZ + ((z + dz - 1) << 2);
                        Biome biome = biomeSampler.get(x0, z0);
                        biomeWeight4.mergeDouble(biome, weight, Double::sum);
                    }
                }

                biomeWeight16.clear();

                // Contribution from four corners
                int x1 = chunkX + ((x - 1) << 2);
                int z1 = chunkZ + ((z - 1) << 2);

                int coordX = x1 >> 4;
                int coordZ = z1 >> 4;

                double lerpX = (x1 - (coordX << 4)) * (1 / 16d);
                double lerpZ = (z1 - (coordZ << 4)) * (1 / 16d);

                int index16X = ((x1 - chunkX) >> 4) + 1;
                int index16Z = ((z1 - chunkZ) >> 4) + 1;

                sampleBiomesCornerContribution(biomeWeight16, biomeWeights16[index16X | (index16Z << 2)], (1 - lerpX) * (1 - lerpZ));
                sampleBiomesCornerContribution(biomeWeight16, biomeWeights16[(index16X + 1) | (index16Z << 2)], lerpX * (1 - lerpZ));
                sampleBiomesCornerContribution(biomeWeight16, biomeWeights16[index16X | ((index16Z + 1) << 2)], (1 - lerpX) * lerpZ);
                sampleBiomesCornerContribution(biomeWeight16, biomeWeights16[(index16X + 1) | ((index16Z + 1) << 2)], lerpX * lerpZ);

                composeSampleWeights(biomeWeight4, biomeWeight16, biome -> {
                    BiomeVariants.Group group = TFCBiomes.getExtensionOrThrow(world, biome).getVariants().getGroup();
                    return group.ordinal();
                }, BiomeVariants.Group.SIZE);

                Object2DoubleMap<Biome> biomeWeight1 = newWeightMap();

                for (int dx = 0; dx < 5; dx++)
                {
                    for (int dz = 0; dz < 5; dz++)
                    {
                        double weight = KERNEL_5x5[dx + dz * 5];
                        int x0 = chunkX + ((x + dx - 1) << 2);
                        int z0 = chunkZ + ((z + dz - 1) << 2);
                        Biome biome = biomeSampler.get(x0, z0);
                        biomeWeight1.mergeDouble(biome, weight, Double::sum);
                    }
                }

                composeSampleWeights(biomeWeight1, biomeWeight4, biome -> {
                    BiomeVariants.Group group = TFCBiomes.getExtensionOrThrow(world, biome).getVariants().getGroup();
                    return group == BiomeVariants.Group.RIVER ? 1 : 0;
                }, 2);

                biomeWeights1[x + 7 * z] = biomeWeight1;
            }
        }

        return biomeWeights1;
    }

    /**
     * Builds the surface, with a couple modifications from vanilla
     * - Passes additional context to the surface builder (if desired), such as world, and slope data
     * - Picks the biome from the accurate biomes computed in the noise step, rather than the chunk biome magnifier.
     */
    private void buildSurfaceWithContext(LevelAccessor world, ProtoChunk chunk, Biome[] accurateChunkBiomes, double[] slopeMap, Random random)
    {
        final ChunkPos chunkPos = chunk.getPos();
        final ChunkData chunkData = chunkDataProvider.get(chunkPos);
        final SurfaceBuilderContext context = new SurfaceBuilderContext(world, chunk, chunkData, random, seed, noiseGeneratorSettings(), getSeaLevel());
        for (int x = 0; x < 16; ++x)
        {
            for (int z = 0; z < 16; ++z)
            {
                final int posX = chunkPos.getMinBlockX() + x;
                final int posZ = chunkPos.getMinBlockZ() + z;
                final int startHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) + 1;
                final double noise = surfaceDepthNoise.getSurfaceNoiseValue(posX * 0.0625, posZ * 0.0625, 0.0625, 0.0625) * 15;
                final double slope = sampleSlope(slopeMap, x, z);
                final Biome biome = accurateChunkBiomes[x + 16 * z];
                final ConfiguredSurfaceBuilder<?> surfaceBuilder = biome.getGenerationSettings().getSurfaceBuilder().get();

                context.apply(surfaceBuilder, biome, posX, posZ, startHeight, noise, slope);
            }
        }
    }

    /**
     * Builds either a single flat layer of bedrock, or natural vanilla bedrock
     * Writes directly to the bottom chunk section for better efficiency
     */
    private void makeBedrock(ProtoChunk chunk, Random random)
    {
        final LevelChunkSection bottomSection = chunk.getOrCreateSection(0);
        final BlockState bedrock = Blocks.BEDROCK.defaultBlockState();

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                if (flatBedrock)
                {
                    bottomSection.setBlockState(x, 0, z, bedrock, false);
                }
                else
                {
                    for (int y = 0; y < 6; y++)
                    {
                        if (random.nextInt(6) < 6 - y)
                        {
                            bottomSection.setBlockState(x, y, z, bedrock, false);
                        }
                    }
                }
            }
        }
    }

    private void sampleBiomesCornerContribution(Object2DoubleMap<Biome> accumulator, Object2DoubleMap<Biome> corner, double t)
    {
        if (t > 0)
        {
            for (Object2DoubleMap.Entry<Biome> entry : corner.object2DoubleEntrySet())
            {
                accumulator.mergeDouble(entry.getKey(), entry.getDoubleValue() * t, Double::sum);
            }
        }
    }

    /**
     * Builds a 6x6, 4x4 resolution slope map for a chunk
     * This is enough to do basic linear interpolation for every point within the chunk.
     *
     * @param sampledHeightMap A 7x7, 4x4 resolution map of the heights in the chunk, offset by (-1, -1)
     * @return A measure of how slope-y the chunk is. Values roughly in [0, 13), although technically can be >13
     */
    @SuppressWarnings("PointlessArithmeticExpression")
    private double[] buildSlopeMap(double[] sampledHeightMap)
    {
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

    private <T> Object2DoubleMap<T> newWeightMap()
    {
        return new Object2DoubleOpenHashMap<>(); // This is just for my own curiosity, it can be inlined later.
    }

    @SuppressWarnings("unchecked")
    private <T> Object2DoubleMap<T>[] newWeightArray(int size)
    {
        return (Object2DoubleMap<T>[]) new Object2DoubleMap[size]; // Avoid generic array warnings / errors
    }

    interface Sampler<T>
    {
        T get(int x, int z);
    }

    class FillFromNoiseHelper
    {
        // Initialized from the chunk
        private final LevelAccessor world;
        private final ProtoChunk chunk;
        private final int chunkX, chunkZ;
        private final Object2DoubleMap<BiomeNoiseSampler> biomeSamplers;
        private final Heightmap oceanFloor, worldSurface;

        // Noise caves / interpolators, setup initially for the chunk
        private final List<NoiseInterpolator> interpolators;
        private final CavifierSampler cavifierSampler;
        private final NoodleCaveSampler noodleCaveSampler;
        private final int minY, maxY, minCellY, noiseCellCountY;

        // Externally sampled biome weight arrays, and a weight map for local sampling
        private final Object2DoubleMap<Biome>[] sampledBiomeWeights;
        private final Object2DoubleMap<Biome> biomeWeights1;

        // Current local position / context
        private int x, z; // Absolute x/z positions
        private int localX, localZ; // Chunk-local x/z
        private double cellDeltaX, cellDeltaZ; // Delta within a noise cell
        private int lastCellZ; // Last cell Z, needed due to a quick in noise interpolator

        // Result arrays, set for each x/z in the chunk
        private final int[] surfaceHeight;
        private final Biome[] localBiomes;

        FillFromNoiseHelper(LevelAccessor world, ProtoChunk chunk, Object2DoubleMap<Biome>[] sampledBiomeWeights, int[] surfaceHeight, Biome[] localBiomes)
        {
            this.world = world;
            this.chunk = chunk;
            this.chunkX = chunk.getPos().getMinBlockX();
            this.chunkZ = chunk.getPos().getMinBlockZ();
            this.biomeSamplers = new Object2DoubleOpenHashMap<>();

            final NoiseSettings noiseSettings = noiseGeneratorSettings().noiseSettings();
            this.minY = Math.max(noiseSettings.minY(), chunk.getMinBuildHeight());
            this.maxY = Math.min(noiseSettings.minY() + noiseSettings.height(), chunk.getMaxBuildHeight());

            this.minCellY = Mth.intFloorDiv(minY, cellHeight);
            this.noiseCellCountY = Mth.intFloorDiv(maxY - minY, cellHeight);

            this.interpolators = new ArrayList<>();

            this.cavifierSampler = new CavifierSampler(cavifier, chunk.getPos(), cellWidth, cellHeight, cellCountX, cellCountY, cellCountZ, minCellY);
            this.noodleCaveSampler = new NoodleCaveSampler(noodleCavifier, chunk.getPos(), cellCountX, cellCountY, cellCountZ, minCellY);

            this.oceanFloor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
            this.worldSurface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

            this.sampledBiomeWeights = sampledBiomeWeights;
            this.biomeWeights1 = new Object2DoubleOpenHashMap<>();

            this.surfaceHeight = surfaceHeight;
            this.localBiomes = localBiomes;

            cavifierSampler.addInterpolators(interpolators);
            noodleCaveSampler.addInterpolators(interpolators);
            interpolators.forEach(NoiseInterpolator::initializeForFirstCellX);
        }

        /**
         * Fills the entire chunk
         */
        void fillFromNoise(Aquifer aquifer, RockLayerStoneSource stoneSource)
        {
            final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            for (int cellX = 0; cellX < cellCountX; cellX++)
            {
                final int finalCellX = cellX;
                interpolators.forEach(interpolator -> interpolator.advanceCellX(finalCellX));

                for (int cellZ = 0; cellZ < cellCountZ; cellZ++)
                {
                    // skip cell Y

                    for (int localCellX = 0; localCellX < cellWidth; localCellX++)
                    {
                        x = chunkX + cellX * cellWidth + localCellX;
                        localX = x & 15;
                        cellDeltaX = (double) localCellX / cellWidth;

                        // cannot update for x here because we first need to update for yz. So we do all three each time per cell
                        for (int localCellZ = 0; localCellZ < cellWidth; localCellZ++)
                        {
                            z = chunkZ + cellZ * cellWidth + localCellZ;
                            lastCellZ = cellZ; // needed for the noise interpolator
                            localZ = z & 15;
                            cellDeltaZ = (double) localCellZ / cellWidth;

                            fillColumn(aquifer, stoneSource, mutablePos);
                        }
                    }
                }

                interpolators.forEach(NoiseInterpolator::swapSlices);
            }
        }

        /**
         * Fills a single column
         */
        void fillColumn(Aquifer aquifer, RockLayerStoneSource stoneSource, BlockPos.MutableBlockPos mutablePos)
        {
            // todo: optimization, start at the top height value rather than iterating all the way
            prepareColumnBiomeWeights(); // Before iterating y, setup x/z biome sampling
            double heightNoiseValue = sampleColumnHeightAndBiome(biomeWeights1, true); // sample height, using the just-computed biome weights

            LevelChunkSection section = chunk.getOrCreateSection(chunk.getSectionsCount() - 1);
            for (int cellY = cellCountY - 1; cellY >= 0; --cellY)
            {
                final int finalCellZ = lastCellZ;
                final int finalCellY = cellY;
                interpolators.forEach(interpolator -> interpolator.selectCellYZ(finalCellY, finalCellZ));

                for (int localCellY = cellHeight - 1; localCellY >= 0; --localCellY)
                {
                    int y = (minCellY + cellY) * cellHeight + localCellY;
                    int localY = y & 15;
                    int lastSectionIndex = chunk.getSectionIndex(y);
                    if (chunk.getSectionIndex(section.bottomBlockY()) != lastSectionIndex)
                    {
                        section = chunk.getOrCreateSection(lastSectionIndex);
                    }

                    double cellDeltaY = (double) localCellY / cellHeight;
                    interpolators.forEach(noiseInterpolator -> {
                        noiseInterpolator.updateForY(cellDeltaY);
                        noiseInterpolator.updateForX(cellDeltaX);
                    });

                    double noise = calculateNoiseAtHeight(y, heightNoiseValue);
                    BlockState state = getStateAt(aquifer, stoneSource, x, y, z, noise);
                    if (!state.isAir())
                    {
                        section.setBlockState(localX, localY, localZ, state, false);
                        oceanFloor.update(localX, y, localZ, state);
                        worldSurface.update(localX, y, localZ, state);
                        if (aquifer.shouldScheduleFluidUpdate() && !state.getFluidState().isEmpty())
                        {
                            mutablePos.set(x, y, z);
                            chunk.getLiquidTicks().scheduleTick(mutablePos, state.getFluidState().getType(), 0);
                        }
                    }
                }
            }
        }

        void prepareColumnBiomeWeights()
        {
            final int index4X = (localX >> 2) + 1;
            final int index4Z = (localZ >> 2) + 1;

            final double lerpX = (localX - ((localX >> 2) << 2)) * (1 / 4d);
            final double lerpZ = (localZ - ((localZ >> 2) << 2)) * (1 / 4d);

            biomeWeights1.clear();
            sampleBiomesCornerContribution(biomeWeights1, sampledBiomeWeights[index4X + index4Z * 7], (1 - lerpX) * (1 - lerpZ));
            sampleBiomesCornerContribution(biomeWeights1, sampledBiomeWeights[(index4X + 1) + index4Z * 7], lerpX * (1 - lerpZ));
            sampleBiomesCornerContribution(biomeWeights1, sampledBiomeWeights[index4X + (index4Z + 1) * 7], (1 - lerpX) * lerpZ);
            sampleBiomesCornerContribution(biomeWeights1, sampledBiomeWeights[(index4X + 1) + (index4Z + 1) * 7], lerpX * lerpZ);
        }

        double sampleColumnHeightAndBiome(Object2DoubleMap<Biome> biomeWeights, boolean updateArrays)
        {
            biomeSamplers.clear();

            // Requires the column to be initialized (just x/z)
            double totalHeight = 0, riverHeight = 0, shoreHeight = 0;
            double riverWeight = 0, shoreWeight = 0;
            Biome biomeAt = null, normalBiomeAt = null, riverBiomeAt = null, shoreBiomeAt = null;
            double maxNormalWeight = 0, maxRiverWeight = 0, maxShoreWeight = 0;

            for (Object2DoubleMap.Entry<Biome> entry : biomeWeights.object2DoubleEntrySet())
            {
                double weight = entry.getDoubleValue();
                BiomeVariants variants = TFCBiomes.getExtensionOrThrow(world, entry.getKey()).getVariants();
                BiomeNoiseSampler sampler = biomeNoiseSamplers.get(variants);

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
                if (variants == TFCBiomes.RIVER)
                {
                    riverHeight += height;
                    riverWeight += weight;
                    if (maxRiverWeight < weight)
                    {
                        riverBiomeAt = entry.getKey();
                        maxRiverWeight = weight;
                    }
                }
                else if (variants == TFCBiomes.SHORE)
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

            double actualHeight = totalHeight;
            if (riverWeight > 0.6 && riverBiomeAt != null)
            {
                // River bottom / shore
                double aboveWaterDelta = actualHeight - riverHeight / riverWeight;
                if (aboveWaterDelta > 0)
                {
                    if (aboveWaterDelta > 20)
                    {
                        aboveWaterDelta = 20;
                    }
                    double adjustedAboveWaterDelta = 0.02 * aboveWaterDelta * (40 - aboveWaterDelta) - 0.48;
                    actualHeight = riverHeight / riverWeight + adjustedAboveWaterDelta;
                }
                biomeAt = riverBiomeAt; // Use river surface for the bottom of the river + small shore beneath cliffs
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

            return actualHeight;
        }

        double calculateNoiseAtHeight(int y, double heightNoiseValue)
        {
            double noise = 0;
            for (Object2DoubleMap.Entry<BiomeNoiseSampler> entry : biomeSamplers.object2DoubleEntrySet())
            {
                noise += entry.getKey().noise(y) * entry.getDoubleValue();
            }

            noise = 0.4f - noise; // > 0 is solid
            if (y > heightNoiseValue)
            {
                noise -= (y - heightNoiseValue) * 0.2f; // Quickly drop noise down to zero if we're above the expected height
            }

            return Mth.clamp(noise, -1, 1);
        }

        BlockState getStateAt(Aquifer aquifer, BaseStoneSource stoneSource, int x, int y, int z, double noise)
        {
            noise = noodleCaveSampler.sample(noise, x, y, z, minY, cellDeltaZ);
            noise = Math.min(noise, cavifierSampler.sample(cellDeltaZ) * 2f);
            return aquifer.computeState(stoneSource, x, y, z, noise);
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
}