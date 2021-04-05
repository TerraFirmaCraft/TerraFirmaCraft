/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.EmptyBlockReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.*;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.dries007.tfc.mixin.world.gen.ChunkGeneratorAccessor;
import net.dries007.tfc.world.biome.*;
import net.dries007.tfc.world.carver.CarverHelpers;
import net.dries007.tfc.world.chunkdata.*;
import net.dries007.tfc.world.noise.INoise2D;
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

    public static final int KERNEL_RADIUS = 4;
    public static final int KERNEL_SIZE = 2 * KERNEL_RADIUS + 1; // 9
    public static final double[] KERNEL = Util.make(new double[9 * 9], array ->
    {
        // Parabolic field with total summed area equal to 1
        for (int x = 0; x < 9; x++)
        {
            for (int z = 0; z < 9; z++)
            {
                array[x + 9 * z] = 0.0211640211641D * (1 - 0.03125D * ((z - 4) * (z - 4) + (x - 4) * (x - 4)));
            }
        }
    });

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

    /**
     * For a coordinate pair (x, y), accumulates samples in a 9x9 square centered on (x, y), at integer values
     */
    public static <T> void sampleLocal(Sampler<T> sampler, Object2DoubleMap<T> weightMap, int x, int z)
    {
        weightMap.clear();
        for (int i = 0; i < KERNEL_SIZE; i++)
        {
            for (int j = 0; j < KERNEL_SIZE; j++)
            {
                final double weight = KERNEL[i + KERNEL_SIZE * j];
                weightMap.mergeDouble(sampler.get(i + x - KERNEL_RADIUS, j + z - KERNEL_RADIUS), weight, Double::sum);
            }
        }
    }

    /**
     * For a coordinate pair (x, y),
     * - It finds a grid square which encompasses the point (x, y).
     * - Then, it samples the four corners of this square in the same manner as sampleLocal()
     * - In order to reduce square artifacts, it weights based on the position of (x, y) inside the grid square.
     * - This is merged into one loop over a 1 + KERNEL_SIZE area to reduce repeated samples.
     *
     * This uses the combination of grid square, and linear interpolation to reduce the total number of samples needed for far away biomes.
     */
    public static <T> void sampleDiscrete(Sampler<T> sampler, Object2DoubleMap<T> weightMap, int x, int z, int resolutionBits)
    {
        if (resolutionBits == 0)
        {
            throw new IllegalArgumentException("Use sampleLocal() for resolutionBits = 0");
        }
        final int gridX = (x >> resolutionBits) << resolutionBits, gridZ = (z >> resolutionBits) << resolutionBits;
        final int deltaMax = 1 << resolutionBits;
        final float deltaX = ((float) x - gridX) / deltaMax, deltaZ = ((float) z - gridZ) / deltaMax;

        weightMap.clear();
        for (int i = 0; i < 1 + KERNEL_SIZE; i++)
        {
            for (int j = 0; j < 1 + KERNEL_SIZE; j++)
            {
                // Sample contributions from all four corners, where applicable
                // This saves repeated 224 mergeDouble() calls per iteration as opposed to iterating over the 9x9 area and summing contributions with offsets.
                // Profiling showed this method contributed ~3.4% of overall world gen time, benchmarking this improvement with JMH showed a consistent ~51% speed gain using this method.
                double value = 0;
                if (i < KERNEL_SIZE && j < KERNEL_SIZE)
                {
                    value += KERNEL[i + KERNEL_SIZE * j] * (1 - deltaX) * (1 - deltaZ);
                }
                if (i > 0 && j < KERNEL_SIZE)
                {
                    value += KERNEL[(i - 1) + KERNEL_SIZE * j] * deltaX * (1 - deltaZ);
                }
                if (i < KERNEL_SIZE && j > 0)
                {
                    value += KERNEL[i + KERNEL_SIZE * (j - 1)] * (1 - deltaX) * deltaZ;
                }
                if (i > 0 && j > 0)
                {
                    value += KERNEL[(i - 1) + KERNEL_SIZE * (j - 1)] * deltaX * deltaZ;
                }
                if (value > 0)
                {
                    weightMap.mergeDouble(sampler.get(((i - KERNEL_RADIUS) << resolutionBits) + gridX, ((j - KERNEL_RADIUS) << resolutionBits) + gridZ), value, Double::sum);
                }
            }
        }
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
    public static <T, G extends Enum<G>> void composeSampleWeights(Object2DoubleMap<T> weightMap, Object2DoubleMap<T> groupWeightMap, Function<T, G> groupFunction, int groups)
    {
        // First, we need to calculate the maximum weight per group
        double[] maxWeights = new double[groups];
        for (Object2DoubleMap.Entry<T> entry : groupWeightMap.object2DoubleEntrySet())
        {
            G group = groupFunction.apply(entry.getKey());
            if (group != null)
            {
                maxWeights[group.ordinal()] += entry.getDoubleValue();
            }
        }

        // Then, we iterate through the smaller weight map and identify the actual weight that needs to be replaced with each group
        double[] actualWeights = new double[groups];
        ObjectIterator<Object2DoubleMap.Entry<T>> iterator = weightMap.object2DoubleEntrySet().iterator();
        while (iterator.hasNext())
        {
            Object2DoubleMap.Entry<T> entry = iterator.next();
            G group = groupFunction.apply(entry.getKey());
            if (group != null)
            {
                actualWeights[group.ordinal()] += entry.getDoubleValue();
                iterator.remove();
            }
        }

        // Finally, insert the weights for each group as a portion of the actual weight
        for (Object2DoubleMap.Entry<T> entry : groupWeightMap.object2DoubleEntrySet())
        {
            G group = groupFunction.apply(entry.getKey());
            if (group != null && actualWeights[group.ordinal()] > 0 && maxWeights[group.ordinal()] > 0)
            {
                weightMap.put(entry.getKey(), entry.getDoubleValue() * actualWeights[group.ordinal()] / maxWeights[group.ordinal()]);
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
    private final Map<BiomeVariants, INoise2D> biomeHeightNoise;
    private final Map<BiomeVariants, INoise2D> biomeCarvingCenterNoise;
    private final Map<BiomeVariants, INoise2D> biomeCarvingHeightNoise;
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
        this.biomeCarvingCenterNoise = new HashMap<>();
        this.biomeCarvingHeightNoise = new HashMap<>();

        final SharedSeedRandom seedGenerator = new SharedSeedRandom(seed);
        TFCBiomes.getVariants().forEach(variant -> {
            biomeHeightNoise.put(variant, variant.createNoiseLayer(seed));
            if (variant instanceof CarvingBiomeVariants)
            {
                Pair<INoise2D, INoise2D> carvingNoise = ((CarvingBiomeVariants) variant).createCarvingNoiseLayer(seed);
                biomeCarvingCenterNoise.put(variant, carvingNoise.getFirst());
                biomeCarvingHeightNoise.put(variant, carvingNoise.getSecond());
            }
        });
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
        final RockData rockData = chunkDataProvider.get(chunk.getPos(), ChunkData.Status.ROCKS).getRockData();

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

    @Override
    public void applyBiomeDecoration(WorldGenRegion worldGenRegion_, StructureManager structureManager_)
    {
        // super.applyBiomeDecoration(worldGenRegion_, structureManager_);
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
        final Biome biome = this.biomeSource.getNoiseBiome((chunkPos.x << 2) + 2, 0, (chunkPos.z << 2) + 2);
        for (Supplier<StructureFeature<?, ?>> supplier : biome.getGenerationSettings().structures())
        {
            ((ChunkGeneratorAccessor) this).invoke$createStructure(supplier.get(), dynamicRegistry, structureManager, chunk, templateManager, seed, chunkPos, biome);
        }
    }

    /**
     * This runs after biome generation. In order to do accurate surface placement, we don't use the already generated biome container, as the biome magnifier really sucks for definition on cliffs.
     */
    @Override
    @SuppressWarnings("PointlessArithmeticExpression")
    public void fillFromNoise(IWorld world, StructureManager structureManager, IChunk chunkIn)
    {
        // Initialization
        final ChunkPrimer chunk = (ChunkPrimer) chunkIn;
        final ChunkPos chunkPos = chunk.getPos();
        final SharedSeedRandom random = new SharedSeedRandom();
        final int chunkX = chunkPos.getMinBlockX(), chunkZ = chunkPos.getMinBlockZ();

        random.setBaseChunkSeed(chunkPos.x, chunkPos.z);

        // The accurate version of biomes which we use for surface building
        // These are calculated during height generation in order to generate cliffs with harsh borders between biomes
        final Biome[] localBiomes = new Biome[16 * 16];

        // Height maps, computed initially for each position in the chunk
        final int[] surfaceHeightMap = new int[16 * 16];
        final double[] carvingCenterMap = new double[16 * 16];
        final double[] carvingHeightMap = new double[16 * 16];

        // Distributed height maps and derivative maps, used for surface slope calculations
        final double[] sampledHeightMap = new double[7 * 7];
        final double[] slopeMap = new double[6 * 6];

        // The biome weights at different distance intervals
        final Object2DoubleMap<Biome> weightMap16 = new Object2DoubleOpenHashMap<>(4), weightMap4 = new Object2DoubleOpenHashMap<>(4), weightMap1 = new Object2DoubleOpenHashMap<>(4), carvingWeightMap1 = new Object2DoubleOpenHashMap<>(4);

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
        final Function<Biome, BiomeVariants> variantAccessor = biome -> TFCBiomes.getExtensionOrThrow(world, biome).getVariants();
        final Mutable<Biome> mutableBiome = new MutableObject<>();
        final MutableBoolean hasCarvingBiomes = new MutableBoolean();

        final BitSet airCarvingMask = chunk.getOrCreateCarvingMask(GenerationStage.Carving.AIR);
        final BitSet liquidCarvingMask = chunk.getOrCreateCarvingMask(GenerationStage.Carving.LIQUID);

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                final int cx = chunkX + x, cz = chunkZ + z;

                mutableBiome.setValue(null);
                hasCarvingBiomes.setFalse();

                final double actualHeight = sampleHeight(biomeAccessor, weightMap16, weightMap4, weightMap1, variantAccessor, cx, cz, hasCarvingBiomes, mutableBiome);

                double carvingCenter = 0, carvingHeight = 0, carvingWeight = 0;
                if (hasCarvingBiomes.booleanValue())
                {
                    // Calculate the carving weight map, only using local influences from carving biomes
                    sampleLocal(biomeAccessor, carvingWeightMap1, cx, cz);

                    // Calculate the weighted carving height and center, using the modified weight map
                    for (Object2DoubleMap.Entry<Biome> entry : carvingWeightMap1.object2DoubleEntrySet())
                    {
                        final BiomeVariants variants = variantAccessor.apply(entry.getKey());
                        if (variants instanceof CarvingBiomeVariants)
                        {
                            final double weight = entry.getDoubleValue();
                            carvingWeight += weight;
                            carvingCenter += weight * biomeCarvingCenterNoise.get(variants).noise(cx, cz);
                            carvingHeight += weight * biomeCarvingHeightNoise.get(variants).noise(cx, cz);
                        }
                    }
                }

                // Adjust carving center towards sea level, to fill out the total weight (height defaults weight to zero so it does not need to change
                carvingCenter += SEA_LEVEL * (1 - carvingWeight);

                // Record the local (accurate) biome.
                localBiomes[x + 16 * z] = mutableBiome.getValue();

                // Record height maps
                surfaceHeightMap[x + 16 * z] = (int) actualHeight;
                carvingCenterMap[x + 16 * z] = (int) carvingCenter;
                carvingHeightMap[x + 16 * z] = (int) carvingHeight;

                // Record height for derivative sampling, if we're at an appropriate corner position
                if ((x & 0b11) == 0 && (z & 0b11) == 0)
                {
                    final int sampleX = x >> 2, sampleZ = z >> 2;
                    sampledHeightMap[(sampleX + 1) + 7 * (sampleZ + 1)] = actualHeight;
                }
            }
        }

        // Fill in additional derivative sampling points
        final MutableBoolean noopBoolean = new MutableBoolean();
        for (int i = 0; i < EXTERIOR_POINTS_COUNT; i++)
        {
            final int x = EXTERIOR_POINTS[i << 1], z = EXTERIOR_POINTS[(i << 1) | 1];
            sampledHeightMap[x + 7 * z] = (int) sampleHeight(biomeAccessor, weightMap16, weightMap4, weightMap1, variantAccessor, chunkX - 4 + (x << 2), chunkZ - 4 + (z << 2), noopBoolean, mutableBiome);
        }

        // Build the slope map at each quad
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

        fillInitialChunkBlocks(chunk, surfaceHeightMap);
        updateInitialChunkHeightmaps(chunk, surfaceHeightMap);
        carveInitialChunkBlocks(chunk, carvingCenterMap, carvingHeightMap, airCarvingMask, liquidCarvingMask);
        buildSurfaceWithContext(world, chunk, localBiomes, slopeMap, random);
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

    @Override
    public IBlockReader getBaseColumn(int x, int z)
    {
        return EmptyBlockReader.INSTANCE;
    }

    protected double sampleHeightAndBiome(Object2DoubleMap<Biome> weightMap, Function<Biome, BiomeVariants> variantsAccessor, Function<BiomeVariants, BiomeVariants> variantsFilter, int x, int z, Mutable<Biome> mutableBiome)
    {
        double totalHeight = 0, riverHeight = 0, shoreHeight = 0;
        double riverWeight = 0, shoreWeight = 0;
        Biome biomeAt = null, normalBiomeAt = null, riverBiomeAt = null, shoreBiomeAt = null;
        double maxNormalWeight = 0, maxRiverWeight = 0, maxShoreWeight = 0;
        for (Object2DoubleMap.Entry<Biome> entry : weightMap.object2DoubleEntrySet())
        {
            double weight = entry.getDoubleValue();
            BiomeVariants variants = variantsAccessor.apply(entry.getKey());
            double height = weight * biomeHeightNoise.get(variantsFilter.apply(variants)).noise(x, z);
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
        mutableBiome.setValue(Objects.requireNonNull(biomeAt, "Biome should not be null!"));
        return actualHeight;
    }

    /**
     * Fills the initial chunk based on the surface height noise
     * Batches block state modifications by chunk section
     */
    protected void fillInitialChunkBlocks(ChunkPrimer chunk, int[] surfaceHeightMap)
    {
        final BlockState fillerBlock = settings.getDefaultBlock();
        final BlockState fillerFluid = settings.getDefaultFluid();

        for (int sectionY = 0; sectionY < 16; sectionY++)
        {
            final ChunkSection section = chunk.getOrCreateSection(sectionY);
            for (int localY = 0; localY < 16; localY++)
            {
                final int y = (sectionY << 4) | localY;
                boolean filledAny = false;
                for (int x = 0; x < 16; x++)
                {
                    for (int z = 0; z < 16; z++)
                    {
                        if (y < surfaceHeightMap[x + 16 * z])
                        {
                            section.setBlockState(x, localY, z, fillerBlock, false);
                            filledAny = true;
                        }
                        else if (y < SEA_LEVEL)
                        {
                            section.setBlockState(x, localY, z, fillerFluid, false);
                            filledAny = true;
                        }
                    }
                }

                if (!filledAny)
                {
                    // Nothing was filled at this y level - exit early
                    return;
                }
            }
        }
    }

    /**
     * Updates chunk height maps based on the initial surface height.
     * This is split off of {@link TFCChunkGenerator#fillInitialChunkBlocks(ChunkPrimer, int[])} as that method exits early whenever it reaches the top layer.
     */
    protected void updateInitialChunkHeightmaps(ChunkPrimer chunk, int[] surfaceHeightMap)
    {
        final Heightmap oceanFloor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Type.OCEAN_FLOOR_WG);
        final Heightmap worldSurface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Type.WORLD_SURFACE_WG);

        final BlockState fillerBlock = settings.getDefaultBlock();
        final BlockState fillerFluid = settings.getDefaultFluid();

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                final int landHeight = surfaceHeightMap[x + 16 * z] - 1;
                if (landHeight >= SEA_LEVEL)
                {
                    worldSurface.update(x, landHeight, z, fillerBlock);
                    oceanFloor.update(x, landHeight, z, fillerBlock);
                }
                else
                {
                    worldSurface.update(x, SEA_LEVEL, z, fillerBlock);
                    oceanFloor.update(x, landHeight, z, fillerFluid);
                }
            }
        }
    }

    /**
     * Applies noise level carvers to the initial chunk blocks.
     */
    protected void carveInitialChunkBlocks(ChunkPrimer chunk, double[] carvingCenterMap, double[] carvingHeightMap, BitSet airCarvingMask, BitSet liquidCarvingMask)
    {
        final Heightmap oceanFloor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Type.OCEAN_FLOOR_WG);
        final Heightmap worldSurface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Type.WORLD_SURFACE_WG);

        final BlockState caveFluid = settings.getDefaultFluid();
        final BlockState caveAir = Blocks.CAVE_AIR.defaultBlockState();

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                final double carvingCenter = carvingCenterMap[x + 16 * z];
                final double carvingHeight = carvingHeightMap[x + 16 * z];
                if (carvingHeight > 2f)
                {
                    // Apply carving
                    final int bottomHeight = (int) (carvingCenter - carvingHeight * 0.5f);
                    final int topHeight = (int) (carvingCenter + carvingHeight * 0.5f);

                    ChunkSection section = chunk.getOrCreateSection(bottomHeight >> 4);
                    int sectionY = bottomHeight >> 4;

                    for (int y = bottomHeight; y <= topHeight; y++)
                    {
                        final int carvingMaskIndex = CarverHelpers.maskIndex(x, y, z);

                        BlockState stateAt;
                        if (y < SEA_LEVEL)
                        {
                            stateAt = caveFluid;
                            liquidCarvingMask.set(carvingMaskIndex, true);
                        }
                        else
                        {
                            stateAt = caveAir;
                            airCarvingMask.set(carvingMaskIndex, true);
                        }

                        // More optimizations for early chunk generation - directly access the chunk section's set block state and skip locks
                        final int currentSectionY = y >> 4;
                        if (currentSectionY != sectionY)
                        {
                            section = chunk.getOrCreateSection(currentSectionY);
                            sectionY = currentSectionY;
                        }
                        section.setBlockState(x, y & 15, z, stateAt, false);
                        worldSurface.update(x, y, z, stateAt);
                        oceanFloor.update(x, y, z, stateAt);
                    }
                }
            }
        }
    }

    /**
     * Builds the surface, with a couple modifications from vanilla
     * - Passes additional context to the surface builder (if desired), such as world, and slope data
     * - Picks the biome from the accurate biomes computed in the noise step, rather than the chunk biome magnifier.
     */
    protected void buildSurfaceWithContext(IWorld world, ChunkPrimer chunk, Biome[] accurateChunkBiomes, double[] slopeMap, Random random)
    {
        final ChunkPos chunkPos = chunk.getPos();
        final ChunkData chunkData = chunkDataProvider.get(chunkPos, ChunkData.Status.ROCKS);
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
    protected void makeBedrock(ChunkPrimer chunk, Random random)
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

    protected double sampleHeight(Sampler<Biome> biomeAccessor, Object2DoubleMap<Biome> weightMap16, Object2DoubleMap<Biome> weightMap4, Object2DoubleMap<Biome> weightMap1, Function<Biome, BiomeVariants> variantAccessor, int cx, int cz, MutableBoolean hasCarvingBiomes, Mutable<Biome> mutableBiome)
    {
        // Sample biome weights at different distances
        sampleDiscrete(biomeAccessor, weightMap16, cx, cz, 4);
        sampleDiscrete(biomeAccessor, weightMap4, cx, cz, 2);
        sampleLocal(biomeAccessor, weightMap1, cx, cz);

        // Group biomes at different distances. This has the effect of making some biome transitions happen over larger distances than others.
        // This is used to make most land biomes blend at maximum distance, while allowing biomes such as rivers to blend at short distances, creating better cliffs as river biomes are smaller width than other biomes.
        composeSampleWeights(weightMap4, weightMap16, variantAccessor.andThen(BiomeVariants::getLargeGroup), BiomeVariants.LargeGroup.SIZE);
        composeSampleWeights(weightMap1, weightMap4, variantAccessor.andThen(BiomeVariants::getSmallGroup), BiomeVariants.SmallGroup.SIZE);

        // First, always calculate the center height, by ignoring any possibility of carving biomes
        // The variant accessor is called for each possible biome - if we detect a carving variant, then mark it as found
        return sampleHeightAndBiome(weightMap1, variantAccessor, v -> {
            if (v instanceof CarvingBiomeVariants)
            {
                hasCarvingBiomes.setTrue();
                return ((CarvingBiomeVariants) v).getParent();
            }
            return v;
        }, cx, cz, mutableBiome);
    }

    /**
     * Samples the 'slope' value for a given coordinate within the chunk
     * Expected values are in [0, 13] but are practically unbounded above
     *
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

    protected interface Sampler<T>
    {
        T get(int x, int z);
    }
}