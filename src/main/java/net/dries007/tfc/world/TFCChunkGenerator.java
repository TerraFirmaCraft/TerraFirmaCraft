/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.*;
import java.util.function.Supplier;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.EmptyBlockReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.dries007.tfc.mixin.world.gen.ChunkGeneratorAccessor;
import net.dries007.tfc.world.biome.*;
import net.dries007.tfc.world.carver.CarverHelpers;
import net.dries007.tfc.world.chunkdata.*;
import net.dries007.tfc.world.surfacebuilder.SurfaceBuilderContext;

public class TFCChunkGenerator extends ChunkGenerator implements ITFCChunkGenerator
{
    public static final Codec<TFCChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BiomeProvider.CODEC.fieldOf("biome_source").forGetter(c -> c.biomeSource),
        DimensionSettings.CODEC.fieldOf("settings").forGetter(c -> () -> c.settings),
        Codec.BOOL.fieldOf("flat_bedrock").forGetter(c -> c.flatBedrock),
        Codec.LONG.fieldOf("seed").forGetter(c -> c.seed)
    ).apply(instance, TFCChunkGenerator::new));

    public static final int SEA_LEVEL = 96;
    public static final int SPAWN_HEIGHT = SEA_LEVEL + 16;

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
                if (value < 0)
                {
                    throw new IllegalArgumentException("Invalid kernel value: " + value + " for x = " + x + ", z = " + z);
                }
                array[x + z * size] = value;
                sum += value;
            }
        }
        if (sum < 0.99 || sum > 1.01)
        {
            throw new IllegalArgumentException("Invalid kernel sum: " + sum + " is not ~= 1.00");
        }
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
    public static TFCChunkGenerator createDefaultPreset(Supplier<DimensionSettings> dimensionSettings, Registry<Biome> biomeRegistry, long seed)
    {
        return new TFCChunkGenerator(new TFCBiomeProvider(seed, 8_000, 0, 0, new TFCBiomeProvider.LayerSettings(), new TFCBiomeProvider.ClimateSettings(), biomeRegistry), dimensionSettings, false, seed);
    }

    // Noise
    private final Map<BiomeVariants, IBiomeNoiseSampler> biomeHeightNoise;
    private final INoiseGenerator surfaceDepthNoise;
    private final ChunkDataProvider chunkDataProvider;

    // Properties set from codec
    private final TFCBiomeProvider biomeProvider;
    private final DimensionSettings settings;
    private final boolean flatBedrock;
    private final long seed;

    private final ThreadLocal<BiomeCache> biomeCache;

    public TFCChunkGenerator(BiomeProvider biomeProvider, Supplier<DimensionSettings> settings, boolean flatBedrock, long seed)
    {
        super(biomeProvider, settings.get().structureSettings());

        if (!(biomeProvider instanceof TFCBiomeProvider))
        {
            throw new IllegalArgumentException("biome provider must extend TFCBiomeProvider");
        }
        this.biomeProvider = (TFCBiomeProvider) biomeProvider;
        this.settings = settings.get();
        this.flatBedrock = flatBedrock;
        this.seed = seed;

        this.biomeHeightNoise = new HashMap<>();

        final SharedSeedRandom seedGenerator = new SharedSeedRandom(seed);
        for (BiomeVariants variant : TFCBiomes.getVariants())
        {
            biomeHeightNoise.put(variant, variant.createNoiseSampler(seed));
        }
        surfaceDepthNoise = new PerlinNoiseGenerator(seedGenerator, IntStream.rangeClosed(-3, 0)); // From vanilla

        // Generators / Providers
        this.chunkDataProvider = new ChunkDataProvider(new ChunkDataGenerator(seed, seedGenerator, this.biomeProvider.getLayerSettings())); // Chunk data
        this.biomeProvider.setChunkDataProvider(chunkDataProvider); // Allow biomes to use the chunk data temperature / rainfall variation
        this.biomeCache = ThreadLocal.withInitial(() -> new BiomeCache(8192, biomeProvider));
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
        return new TFCChunkGenerator(biomeProvider, () -> settings, flatBedrock, seedIn);
    }

    @Override
    public void createBiomes(Registry<Biome> biomeIdRegistry, IChunk chunkIn)
    {
        ((ChunkPrimer) chunkIn).setBiomes(new ColumnBiomeContainer(biomeIdRegistry, chunkIn.getPos(), biomeProvider));
    }

    /**
     * Noop - carvers are done at the beginning of feature stage, so the carver is free to check adjacent chunks for information
     */
    @Override
    public void applyCarvers(long worldSeed, BiomeManager biomeManagerIn, IChunk chunkIn, GenerationStage.Carving stage)
    {
        final ChunkPrimer chunk = (ChunkPrimer) chunkIn;
        final BiomeGenerationSettings settings = biomeSource.getNoiseBiome(chunk.getPos().x << 2, 0, chunk.getPos().z << 2).getGenerationSettings();
        final BiomeManager biomeManager = biomeManagerIn.withDifferentSource(this.biomeSource);
        final SharedSeedRandom random = new SharedSeedRandom();

        final BitSet liquidCarvingMask = chunk.getOrCreateCarvingMask(GenerationStage.Carving.LIQUID);
        final BitSet airCarvingMask = chunk.getOrCreateCarvingMask(GenerationStage.Carving.AIR);
        final RockData rockData = chunkDataProvider.get(chunk.getPos()).getRockDataOrThrow();

        if (stage == GenerationStage.Carving.AIR)
        {
            // In vanilla, air carvers fire first. We do water carvers first instead, to catch them with the water adjacency mask later
            // Pass in a null adjacency mask as liquid carvers do not need it
            CarverHelpers.runCarversWithContext(worldSeed, chunk, biomeManager, settings, random, GenerationStage.Carving.LIQUID, airCarvingMask, liquidCarvingMask, rockData, null, getSeaLevel());
        }
        else
        {
            // During liquid carvers, we run air carvers instead.
            // Compute the adjacency mask here
            final BitSet waterAdjacencyMask = CarverHelpers.createWaterAdjacencyMask(chunk, getSeaLevel());
            CarverHelpers.runCarversWithContext(worldSeed, chunk, biomeManager, settings, random, GenerationStage.Carving.AIR, airCarvingMask, liquidCarvingMask, rockData, waterAdjacencyMask, getSeaLevel());
        }
    }

    /**
     * Surface is done in make base, bedrock is added here
     */
    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion world, IChunk chunkIn)
    {
        final ChunkPrimer chunk = (ChunkPrimer) chunkIn;
        final ChunkPos chunkPos = chunk.getPos();
        final SharedSeedRandom random = new SharedSeedRandom();

        random.setBaseChunkSeed(chunkPos.x, chunkPos.z);
        makeBedrock(chunk, random);
    }

    @Override
    public int getSpawnHeight()
    {
        return SPAWN_HEIGHT;
    }

    @Override
    public TFCBiomeProvider getBiomeSource()
    {
        return biomeProvider;
    }

    /**
     * This override just ignores strongholds conditionally as by default TFC does not generate them, but  {@link ChunkGenerator} hard codes them to generate.
     */
    @Override
    public void createStructures(DynamicRegistries dynamicRegistry, StructureManager structureManager, IChunk chunk, TemplateManager templateManager, long seed)
    {
        final ChunkPos chunkPos = chunk.getPos();
        final Biome biome = this.biomeProvider.getNoiseBiome((chunkPos.x << 2) + 2, 0, (chunkPos.z << 2) + 2);
        for (Supplier<StructureFeature<?, ?>> supplier : biome.getGenerationSettings().structures())
        {
            ((ChunkGeneratorAccessor) this).invoke$createStructure(supplier.get(), dynamicRegistry, structureManager, chunk, templateManager, seed, chunkPos, biome);
        }
    }

    /**
     * This runs after biome generation. In order to do accurate surface placement, we build surfaces here as we can get better resolution than the default biome container based surface.
     */
    @Override
    public void fillFromNoise(IWorld world, StructureManager structureManager, IChunk chunkIn)
    {
        // Initialization
        final ChunkPrimer chunk = (ChunkPrimer) chunkIn;
        final ChunkPos chunkPos = chunk.getPos();
        final SharedSeedRandom random = new SharedSeedRandom();
        final int chunkX = chunkPos.getMinBlockX(), chunkZ = chunkPos.getMinBlockZ();

        random.setBaseChunkSeed(chunkPos.x, chunkPos.z);

        final Biome[] localBiomes = new Biome[16 * 16];
        final double[] sampledHeightMap = new double[7 * 7];
        final int[] surfaceHeightMap = new int[16 * 16];

        final BiomeCache localBiomeCache = biomeCache.get();
        final BiomeContainer biomeContainer = Objects.requireNonNull(chunk.getBiomes(), "Chunk has no biomes?");
        final Sampler<Biome> biomeAccessor = (x, z) -> {
            // First check the local chunk, if not then fallback to the cache
            if ((x >> 4) == chunkPos.x && (z >> 4) == chunkPos.z)
            {
                return biomeContainer.getNoiseBiome(x >> 2, 0, z >> 2);
            }
            return localBiomeCache.get(x >> 2, z >> 2);
        };
        final Mutable<Biome> biome = new MutableObject<>();

        final BitSet airCarvingMask = chunk.getOrCreateCarvingMask(GenerationStage.Carving.AIR);
        final BitSet liquidCarvingMask = chunk.getOrCreateCarvingMask(GenerationStage.Carving.LIQUID);

        final Heightmap oceanFloor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Type.OCEAN_FLOOR_WG);
        final Heightmap worldSurface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Type.WORLD_SURFACE_WG);

        final Object2DoubleMap<Biome>[] biomeWeights = sampleBiomes(world, chunkPos, biomeAccessor);
        final Object2DoubleMap<Biome> biomeWeight1 = new Object2DoubleOpenHashMap<>();

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                final int x0 = chunkX + x;
                final int z0 = chunkZ + z;

                final int index4X = (x >> 2) + 1;
                final int index4Z = (z >> 2) + 1;

                final double lerpX = (x0 - ((x0 >> 2) << 2)) * (1 / 4d);
                final double lerpZ = (z0 - ((z0 >> 2) << 2)) * (1 / 4d);

                biomeWeight1.clear();
                sampleBiomesCornerContribution(biomeWeight1, biomeWeights[index4X + index4Z * 7], (1 - lerpX) * (1 - lerpZ));
                sampleBiomesCornerContribution(biomeWeight1, biomeWeights[(index4X + 1) + index4Z * 7], lerpX * (1 - lerpZ));
                sampleBiomesCornerContribution(biomeWeight1, biomeWeights[index4X + (index4Z + 1) * 7], (1 - lerpX) * lerpZ);
                sampleBiomesCornerContribution(biomeWeight1, biomeWeights[(index4X + 1) + (index4Z + 1) * 7], lerpX * lerpZ);

                biome.setValue(null);

                final double actualHeight = fillColumn(world, chunk, biomeWeight1, x0, z0, worldSurface, oceanFloor, biome, airCarvingMask, liquidCarvingMask);

                // Record the local (accurate) biome.
                localBiomes[x + 16 * z] = biome.getValue();

                // Record height for derivative sampling, if we're at an appropriate corner position
                if ((x & 0b11) == 0 && (z & 0b11) == 0)
                {
                    final int sampleX = x >> 2, sampleZ = z >> 2;
                    sampledHeightMap[(sampleX + 1) + 7 * (sampleZ + 1)] = actualHeight;
                }

                surfaceHeightMap[x + 16 * z] = (int) actualHeight;
            }
        }

        // Surface height is built now, this is required for surface builders
        chunkDataProvider.get(chunkPos).getRockDataOrThrow().setSurfaceHeight(surfaceHeightMap);

        // Fill in additional derivative sampling points
        for (int i = 0; i < EXTERIOR_POINTS_COUNT; i++)
        {
            int x = EXTERIOR_POINTS[i << 1];
            int z = EXTERIOR_POINTS[(i << 1) | 1];

            int x0 = chunkX + ((x - 1) << 2);
            int z0 = chunkZ + ((z - 1) << 2);

            sampledHeightMap[x + 7 * z] = sampleHeightOnly(world, biomeWeights[x + z * 7], x0, z0);
        }

        final double[] slopeMap = buildSlopeMap(sampledHeightMap);

        buildSurfaceWithContext(world, chunk, localBiomes, slopeMap, random);
        if (Debug.ENABLE_SLOPE_VISUALIZATION)
        {
            Debug.slopeVisualization(chunk, slopeMap, chunkX, chunkZ, this::sampleSlope);
        }
    }

    @Override
    public int getSeaLevel()
    {
        return SEA_LEVEL;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Type heightMapType)
    {
        return SEA_LEVEL;
    }

    /**
     * Vanilla only uses this for nether fossils and ruined portals. We can spare both of those I think.
     */
    @Override
    public IBlockReader getBaseColumn(int x, int z)
    {
        return EmptyBlockReader.INSTANCE;
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

    protected Object2DoubleMap<Biome>[] sampleBiomes(IWorld world, ChunkPos pos, Sampler<Biome> biomeSampler)
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
     * Fills a column of blocks.
     * 1. Applies river and shore transformations, using the provided biome weights, to the initial height map
     * 2. Builds a map of {@link IBiomeNoiseSampler}s for each biome, accumulating their weights
     * 3. Identifies the highest weight biome at this location.
     * 4. Fills the rest of the column, using influences from the 3D noise samplers
     * 5. Marks carving masks and heightmaps appropriately
     *
     * For a lightweight version of this function, see {@link TFCChunkGenerator#sampleHeightOnly(IWorld, Object2DoubleMap, int, int)}
     *
     * @param world         The world, used to query {@link BiomeVariants}
     * @param chunk         The chunk
     * @param biomeWeights  A map of the weights of biomes at this location
     * @param x             Absolute x position
     * @param z             Absolute z position
     * @param worldSurface  The {@link Heightmap.Type#WORLD_SURFACE_WG} heightmap
     * @param oceanFloor    The {@link Heightmap.Type#OCEAN_FLOOR_WG} heightmap
     * @param biome         A box for the biome to be returned in. This will be set to a non-null value by the end of this method
     * @param airCarving    The air carving mask
     * @param liquidCarving The liquid carving mask
     * @return The actual height of the location, before noise sampling is taken into account.
     */
    private double fillColumn(IWorld world, ChunkPrimer chunk, Object2DoubleMap<Biome> biomeWeights, int x, int z, Heightmap worldSurface, Heightmap oceanFloor, Mutable<Biome> biome, BitSet airCarving, BitSet liquidCarving)
    {
        double totalHeight = 0, riverHeight = 0, shoreHeight = 0;
        double riverWeight = 0, shoreWeight = 0;
        Biome biomeAt = null, normalBiomeAt = null, riverBiomeAt = null, shoreBiomeAt = null;
        double maxNormalWeight = 0, maxRiverWeight = 0, maxShoreWeight = 0;

        Object2DoubleMap<IBiomeNoiseSampler> biomeSamplers = newWeightMap();
        for (Object2DoubleMap.Entry<Biome> entry : biomeWeights.object2DoubleEntrySet())
        {
            double weight = entry.getDoubleValue();
            BiomeVariants variants = TFCBiomes.getExtensionOrThrow(world, entry.getKey()).getVariants();
            IBiomeNoiseSampler sampler = biomeHeightNoise.get(variants);

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

        final BlockState fillerBlock = settings.getDefaultBlock();
        final BlockState fillerFluid = settings.getDefaultFluid();

        final int localX = x & 15;
        final int localZ = z & 15;

        int maxActualHeight = Math.max((int) actualHeight, 1 + SEA_LEVEL);
        int maxSection = maxActualHeight >> 4;

        // Top down iteration
        // 1. We need to mark exposed air below the first solid ground as carving mask applicable.
        // 2. We need to record the highest height (be it water or solid) for height map creation
        boolean topBlockPlaced = false;
        boolean topSolidBlockPlaced = false;

        for (int sectionY = maxSection; sectionY >= 0; sectionY--)
        {
            ChunkSection section = chunk.getOrCreateSection(sectionY);
            for (int localY = 15; localY >= 0; localY--)
            {
                int y = (sectionY << 4) | localY;
                if (y >= maxActualHeight)
                {
                    continue; // For the first section, just skip down to the top height
                }

                // Compute noise for this y level
                double noise = 0;
                for (Object2DoubleMap.Entry<IBiomeNoiseSampler> entry : biomeSamplers.object2DoubleEntrySet())
                {
                    noise += entry.getKey().noise(y) * entry.getDoubleValue();
                }

                if (noise < 0.4 && y < actualHeight) // Solid
                {
                    section.setBlockState(localX, y & 15, localZ, fillerBlock, false);
                    if (!topBlockPlaced)
                    {
                        topBlockPlaced = true;
                        worldSurface.update(localX, y, localZ, fillerBlock);
                    }
                    if (!topSolidBlockPlaced)
                    {
                        topSolidBlockPlaced = true;
                        oceanFloor.update(localX, y, localZ, fillerFluid);
                    }
                }
                else if (y < SEA_LEVEL) // Non solid, but below sea level
                {
                    section.setBlockState(localX, y & 15, localZ, fillerFluid, false);
                    if (!topBlockPlaced)
                    {
                        topBlockPlaced = true;
                        worldSurface.update(localX, y, localZ, fillerBlock);
                    }
                    if (topSolidBlockPlaced)
                    {
                        // Water underneath solid blocks, so mark as carved
                        liquidCarving.set(CarverHelpers.maskIndex(localX, localY, localZ));
                    }
                }
                else // Air
                {
                    if (topSolidBlockPlaced)
                    {
                        // Air under solid blocks, so mark as carved
                        airCarving.set(CarverHelpers.maskIndex(localX, localY, localZ));
                    }
                }
            }
        }

        biome.setValue(Objects.requireNonNull(biomeAt));
        return actualHeight;
    }

    /**
     * This is a simplification of {@link TFCChunkGenerator#fillColumn(IWorld, ChunkPrimer, Object2DoubleMap, int, int, Heightmap, Heightmap, Mutable)} that is adapted to just sample the height, while ignoring any unnecessary computation.
     * It is used for derivative sampling outside of the target chunk, where only the height is desired.
     */
    private double sampleHeightOnly(IWorld world, Object2DoubleMap<Biome> biomeWeights, int x, int z)
    {
        double totalHeight = 0, riverHeight = 0, shoreHeight = 0;
        double riverWeight = 0, shoreWeight = 0;
        Biome normalBiomeAt = null, riverBiomeAt = null, shoreBiomeAt = null;
        double maxNormalWeight = 0, maxRiverWeight = 0, maxShoreWeight = 0;
        Object2DoubleMap<IBiomeNoiseSampler> biomeSamplers = newWeightMap();
        for (Object2DoubleMap.Entry<Biome> entry : biomeWeights.object2DoubleEntrySet())
        {
            double weight = entry.getDoubleValue();
            BiomeVariants variants = TFCBiomes.getExtensionOrThrow(world, entry.getKey()).getVariants();
            IBiomeNoiseSampler sampler = biomeHeightNoise.get(variants);

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
        }
        else if (riverWeight > 0 && normalBiomeAt != null)
        {
            double adjustedRiverWeight = 0.6 * riverWeight;
            actualHeight = (totalHeight - riverHeight) * ((1 - adjustedRiverWeight) / (1 - riverWeight)) + riverHeight * (adjustedRiverWeight / riverWeight);
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
        }
        return actualHeight;
    }

    /**
     * Builds the surface, with a couple modifications from vanilla
     * - Passes additional context to the surface builder (if desired), such as world, and slope data
     * - Picks the biome from the accurate biomes computed in the noise step, rather than the chunk biome magnifier.
     */
    private void buildSurfaceWithContext(IWorld world, ChunkPrimer chunk, Biome[] accurateChunkBiomes, double[] slopeMap, Random random)
    {
        final ChunkPos chunkPos = chunk.getPos();
        final ChunkData chunkData = chunkDataProvider.get(chunkPos);
        final SurfaceBuilderContext context = new SurfaceBuilderContext(world, chunk, chunkData, random, seed, settings, getSeaLevel());
        for (int x = 0; x < 16; ++x)
        {
            for (int z = 0; z < 16; ++z)
            {
                final int posX = chunkPos.getMinBlockX() + x;
                final int posZ = chunkPos.getMinBlockZ() + z;
                final int startHeight = chunk.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z) + 1;
                final double noise = surfaceDepthNoise.getSurfaceNoiseValue(posX * 0.0625, posZ * 0.0625, 0.0625, x * 0.0625) * 15;
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
    private void makeBedrock(ChunkPrimer chunk, Random random)
    {
        final ChunkSection bottomSection = chunk.getOrCreateSection(0);
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

    private interface Sampler<T>
    {
        T get(int x, int z);
    }
}