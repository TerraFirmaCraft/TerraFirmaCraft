/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.dries007.tfc.mixin.accessor.ChunkAccessAccessor;
import net.dries007.tfc.world.biome.*;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.chunkdata.ChunkGeneratorExtension;
import net.dries007.tfc.world.chunkdata.RockData;
import net.dries007.tfc.world.noise.Kernel;
import net.dries007.tfc.world.noise.NoiseSampler;
import net.dries007.tfc.world.noise.ChunkNoiseSamplingSettings;
import net.dries007.tfc.world.surface.SurfaceManager;

public class TFCChunkGenerator extends ChunkGenerator implements ChunkGeneratorExtension
{
    public static final Codec<TFCChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        RegistryLookupCodec.create(Registry.NOISE_REGISTRY).forGetter(c -> c.parameters),
        BiomeSource.CODEC.comapFlatMap(TFCChunkGenerator::guardBiomeSource, Function.identity()).fieldOf("biome_source").forGetter(c -> c.customBiomeSource),
        NoiseGeneratorSettings.CODEC.fieldOf("noise_settings").forGetter(c -> c.settings),
        Codec.BOOL.fieldOf("flat_bedrock").forGetter(c -> c.flatBedrock),
        Codec.LONG.fieldOf("seed").forGetter(c -> c.seed)
    ).apply(instance, TFCChunkGenerator::new));

    public static final int SEA_LEVEL_Y = 63; // Matches vanilla

    public static final Kernel KERNEL_9x9 = Kernel.create((x, z) -> 0.0211640211641D * (1 - 0.03125D * (z * z + x * x)), 4);
    public static final Kernel KERNEL_5x5 = Kernel.create((x, z) -> 0.08D * (1 - 0.125D * (z * z + x * x)), 2);

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
    public static TFCChunkGenerator defaultChunkGenerator(Registry<NormalNoise.NoiseParameters> parameters, Supplier<NoiseGeneratorSettings> noiseGeneratorSettings, Registry<Biome> biomeRegistry, long seed)
    {
        return new TFCChunkGenerator(parameters, TFCBiomeSource.defaultBiomeSource(seed, biomeRegistry), noiseGeneratorSettings, false, seed);
    }

    public static void sampleBiomesCornerContribution(Object2DoubleMap<Biome> accumulator, Object2DoubleMap<Biome> corner, double t)
    {
        if (t > 0)
        {
            for (Object2DoubleMap.Entry<Biome> entry : corner.object2DoubleEntrySet())
            {
                accumulator.mergeDouble(entry.getKey(), entry.getDoubleValue() * t, Double::sum);
            }
        }
    }

    private static Map<BiomeVariants, Supplier<BiomeNoiseSampler>> collectBiomeNoiseSamplers(long seed)
    {
        final ImmutableMap.Builder<BiomeVariants, Supplier<BiomeNoiseSampler>> builder = ImmutableMap.builder();
        for (BiomeVariants variant : TFCBiomes.getVariants())
        {
            builder.put(variant, () -> variant.createNoiseSampler(seed));
        }
        return builder.build();
    }

    private static DataResult<TFCBiomeSource> guardBiomeSource(BiomeSource source)
    {
        return source instanceof TFCBiomeSource s ? DataResult.success(s) : DataResult.error("Must be a " + TFCBiomeSource.class.getSimpleName());
    }

    /**
     * @param pos           The target chunk pos.
     * @param biomeSampler  A sampler for biomes, in block coordinates.
     * @param groupFunction A function to access a {@link BiomeVariants.Group} from a {@link Biome}.
     * @return A 7x7 array of sampled biome weights, at quart pos resolution, where the (0, 0) index aligns to the (-1, -1) quart position relative to the target chunk.
     */
    private static Object2DoubleMap<Biome>[] sampleBiomes(ChunkPos pos, Sampler<Biome> biomeSampler, Function<Biome, BiomeVariants.Group> groupFunction)
    {
        // First, sample biomes at chunk distance, in a 4x4 grid centered on the target chunk.
        // These are used to build the large-scale biome blending radius
        final Object2DoubleMap<Biome>[] chunkBiomeWeightArray = newWeightArray(4 * 4);
        final int chunkX = pos.getMinBlockX(), chunkZ = pos.getMinBlockZ(); // Block coordinates
        for (int x = 0; x < 4; x++)
        {
            for (int z = 0; z < 4; z++)
            {
                // x, z = 0, 0 is the -1, -1 chunk relative to chunkX, chunkZ
                final Object2DoubleMap<Biome> chunkBiomeWeight = new Object2DoubleOpenHashMap<>();
                chunkBiomeWeightArray[x | (z << 2)] = chunkBiomeWeight;
                sampleBiomesAtPositionWithKernel(chunkBiomeWeight, biomeSampler, KERNEL_9x9, 4, chunkX, chunkZ, x - 1, z - 1);
            }
        }

        // A 7x7 grid, in quart positions relative to the target chunk, where (1, 1) is the target chunk origin.
        final Object2DoubleMap<Biome>[] quartBiomeWeightArray = newWeightArray(7 * 7);
        final Object2DoubleMap<Biome> chunkBiomeWeight = new Object2DoubleOpenHashMap<>(), wideQuartBiomeWeight = new Object2DoubleOpenHashMap<>();

        for (int x = 0; x < 7; x++)
        {
            for (int z = 0; z < 7; z++)
            {
                // Reset
                wideQuartBiomeWeight.clear();
                chunkBiomeWeight.clear();

                sampleBiomesAtPositionWithKernel(wideQuartBiomeWeight, biomeSampler, KERNEL_9x9, 2, chunkX, chunkZ, x - 1, z - 1);

                // Calculate contribution from the four corners of the 16x16 grid. First, calculate the current grid cell coordinates.
                final int x1 = chunkX + ((x - 1) << 2); // Block coordinates
                final int z1 = chunkZ + ((z - 1) << 2);

                final int coordX = x1 >> 4; // Chunk coordinates
                final int coordZ = z1 >> 4;

                final double lerpX = (x1 - (coordX << 4)) * (1 / 16d); // Deltas, in the range [0, 1)
                final double lerpZ = (z1 - (coordZ << 4)) * (1 / 16d);

                final int index16X = ((x1 - chunkX) >> 4) + 1; // Index into chunkBiomeWeightArray
                final int index16Z = ((z1 - chunkZ) >> 4) + 1;

                sampleBiomesCornerContribution(chunkBiomeWeight, chunkBiomeWeightArray[index16X | (index16Z << 2)], (1 - lerpX) * (1 - lerpZ));
                sampleBiomesCornerContribution(chunkBiomeWeight, chunkBiomeWeightArray[(index16X + 1) | (index16Z << 2)], lerpX * (1 - lerpZ));
                sampleBiomesCornerContribution(chunkBiomeWeight, chunkBiomeWeightArray[index16X | ((index16Z + 1) << 2)], (1 - lerpX) * lerpZ);
                sampleBiomesCornerContribution(chunkBiomeWeight, chunkBiomeWeightArray[(index16X + 1) | ((index16Z + 1) << 2)], lerpX * lerpZ);

                // Compose chunk weights -> wide quart weights.
                composeSampleWeights(wideQuartBiomeWeight, chunkBiomeWeight, biome -> {
                    final BiomeVariants.Group group = groupFunction.apply(biome);
                    return group.ordinal();
                }, BiomeVariants.Group.SIZE);

                // Same as wideQuartBiomeWeight, but only with a sample radius of 2, rather than 4
                final Object2DoubleMap<Biome> quartBiomeWeight = new Object2DoubleOpenHashMap<>();
                sampleBiomesAtPositionWithKernel(quartBiomeWeight, biomeSampler, KERNEL_5x5, 2, chunkX, chunkZ, x - 1, z - 1);

                composeSampleWeights(quartBiomeWeight, wideQuartBiomeWeight, biome -> {
                    final BiomeVariants.Group group = groupFunction.apply(biome);
                    return group == BiomeVariants.Group.RIVER ? 1 : 0;
                }, 2);

                quartBiomeWeightArray[x + 7 * z] = quartBiomeWeight;
            }
        }
        return quartBiomeWeightArray;
    }

    private static void sampleBiomesAtPositionWithKernel(Object2DoubleMap<Biome> weights, Sampler<Biome> biomeSampler, Kernel kernel, int kernelBits, int chunkX, int chunkZ, int xOffsetInKernelBits, int zOffsetInKernelBits)
    {
        final int kernelRadius = kernel.radius();
        final int kernelWidth = kernel.width();
        for (int dx = -kernelRadius; dx <= kernelRadius; dx++)
        {
            for (int dz = -kernelRadius; dz <= kernelRadius; dz++)
            {
                final double weight = kernel.values()[(dx + kernelRadius) + (dz + kernelRadius) * kernelWidth];
                final int blockX = chunkX + ((xOffsetInKernelBits + dx) << kernelBits); // Block positions
                final int blockZ = chunkZ + ((zOffsetInKernelBits + dz) << kernelBits);
                final Biome biome = biomeSampler.get(blockX, blockZ);
                weights.mergeDouble(biome, weight, Double::sum);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Object2DoubleMap<T>[] newWeightArray(int size)
    {
        return (Object2DoubleMap<T>[]) new Object2DoubleMap[size]; // Avoid generic array warnings / errors
    }

    // Properties set from codec
    private final Registry<NormalNoise.NoiseParameters> parameters;
    private final TFCBiomeSource customBiomeSource; // narrowed type from superclass
    private final Supplier<NoiseGeneratorSettings> settings;
    private final boolean flatBedrock;
    private final long seed;

    private final Map<BiomeVariants, Supplier<BiomeNoiseSampler>> biomeNoiseSamplers;
    private final ChunkDataProvider chunkDataProvider;
    private final SurfaceManager surfaceManager;
    private final NoiseSampler noiseSampler;
    @Nullable private NoiseGeneratorSettings cachedSettings;

    public TFCChunkGenerator(Registry<NormalNoise.NoiseParameters> parameters, TFCBiomeSource biomeSource, Supplier<NoiseGeneratorSettings> settings, boolean flatBedrock, long seed)
    {
        super(biomeSource, settings.get().structureSettings());

        this.parameters = parameters;
        this.settings = settings;
        this.cachedSettings = null;
        this.customBiomeSource = biomeSource;
        this.flatBedrock = flatBedrock;
        this.seed = seed;

        this.biomeNoiseSamplers = collectBiomeNoiseSamplers(seed);
        this.chunkDataProvider = customBiomeSource.getChunkDataProvider();
        this.surfaceManager = new SurfaceManager(seed);
        this.noiseSampler = new NoiseSampler(settings.get().noiseSettings(), seed, parameters);
    }

    @Override
    public ChunkDataProvider getChunkDataProvider()
    {
        return chunkDataProvider;
    }

    public NoiseGeneratorSettings getNoiseGeneratorSettings()
    {
        if (cachedSettings == null)
        {
            cachedSettings = settings.get();
        }
        return cachedSettings;
    }

    @Override
    protected Codec<TFCChunkGenerator> codec()
    {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed)
    {
        return new TFCChunkGenerator(parameters, customBiomeSource.withSeed(seed), settings, flatBedrock, seed);
    }

    @Override
    public CompletableFuture<ChunkAccess> createBiomes(Registry<Biome> biomeRegistry, Executor executor, Blender legacyTerrainBlender, StructureFeatureManager structureFeatureManager, ChunkAccess chunk)
    {
        // todo: async biome loading
        // This has caused some very weird issue that I don't quite understand
        // Somehow, if this is allowed to be async, in the same fashion as vanilla, this will actually load biomes incorrectly into the chunk, and/or cause the biome source to be inaccurate later. I have no idea how this happens and am at my limit for debugging this multithreading insanity.
        // The symptom of this will be chunks that appear to have generated at a different height or noise from surrounding ones.
        chunkDataProvider.get(chunk);
        chunk.fillBiomesFromNoise((quartX, quartY, quartZ, sampler) -> customBiomeSource.getNoiseBiome(quartX, quartZ), climateSampler());
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public Climate.Sampler climateSampler()
    {
        return NoopClimateSampler.INSTANCE;
    }

    @Override
    public void applyCarvers(WorldGenRegion level, long seed, BiomeManager biomeManager, StructureFeatureManager structureFeatureManager, ChunkAccess chunk, GenerationStep.Carving step)
    {
        // todo: all of this, figure out how to integrate aquifers from NoiseBasedChunkGenerator again
    }

    @Override
    public void buildSurface(WorldGenRegion level, StructureFeatureManager structureFeatureManager, ChunkAccess chunk)
    {
        makeBedrock(chunk);
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion level)
    {
        // todo: what on earth?
    }

    @Override
    public TFCBiomeSource getBiomeSource()
    {
        return customBiomeSource;
    }

    @Override
    public int getGenDepth()
    {
        return SEA_LEVEL_Y;
    }

    @Override
    public void createStructures(RegistryAccess dynamicRegistry, StructureFeatureManager structureFeatureManager, ChunkAccess chunk, StructureManager templateManager, long seed)
    {
        // todo: structures?
    }

    @Override
    public void createReferences(WorldGenLevel level, StructureFeatureManager structureFeatureManager, ChunkAccess chunk)
    {
        // todo: structures?
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureFeatureManager structureFeatureManager)
    {
        super.applyBiomeDecoration(level, chunk, structureFeatureManager);
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor mainExecutor, Blender oldTerrainBlender, StructureFeatureManager structureFeatureManager, ChunkAccess chunk)
    {
        final ChunkNoiseSamplingSettings settings = createNoiseSamplingSettingsForChunk(chunk);

        // Initialization
        final LevelAccessor actualLevel = (LevelAccessor) ((ChunkAccessAccessor) chunk).accessor$getLevelHeightAccessor();
        final ChunkPos chunkPos = chunk.getPos();
        final RandomSource random = new XoroshiroRandomSource(chunkPos.x * 1842639486192314L, chunkPos.z * 579238196380231L);

        final Biome[] localBiomes = new Biome[16 * 16];
        final int[] surfaceHeightMap = new int[16 * 16];

        final Sampler<Biome> biomeSampler = (x, z) -> customBiomeSource.getNoiseBiomeIgnoreClimate(QuartPos.fromBlock(x), QuartPos.fromBlock(z));

        final ChunkData chunkData = chunkDataProvider.get(chunk);
        final RockData rockData = chunkData.getRockData();

        // Set a reference to the surface height map, which the helper will modify later
        // Since we need surface height to query rock -> each block, it's set before iterating the column in the helper
        rockData.setSurfaceHeight(surfaceHeightMap);

        final Set<LevelChunkSection> sections = new HashSet<>();
        for (LevelChunkSection section : chunk.getSections())
        {
            section.acquire();
            sections.add(section);
        }

        final Object2DoubleMap<Biome>[] biomeWeights = sampleBiomes(chunkPos, biomeSampler, biome -> TFCBiomes.getExtensionOrThrow(actualLevel, biome).getVariants().getGroup());
        final ChunkBaseBlockSource baseBlockSource = new ChunkBaseBlockSource(actualLevel, rockData, biomeSampler);
        final ChunkNoiseFiller filler = new ChunkNoiseFiller(actualLevel, (ProtoChunk) chunk, biomeWeights, surfaceHeightMap, localBiomes, customBiomeSource, createBiomeSamplersForChunk(), noiseSampler, baseBlockSource, settings, getSeaLevel());

        filler.fillFromNoise();

        // Unlock before surfaces are built, as they use locks directly
        sections.forEach(LevelChunkSection::release);

        surfaceManager.buildSurface(actualLevel, chunk, getRockLayerSettings(), chunkData, localBiomes, filler.buildSlopeMap(), random, getSeaLevel(), settings.minY());

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
        return getNoiseGeneratorSettings().noiseSettings().minY();
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

    @Override
    public boolean hasStronghold(ChunkPos pos)
    {
        return false;
    }

    /**
     * Builds either a single flat layer of bedrock, or natural vanilla bedrock
     * Writes directly to the bottom chunk section for better efficiency
     */
    protected void makeBedrock(ChunkAccess chunk)
    {
        final ChunkPos chunkPos = chunk.getPos();
        final RandomSource random = new XoroshiroRandomSource(chunkPos.x * 2369412341L, chunkPos.z * 8192836412341L);
        final LevelChunkSection bottomSection = chunk.getSection(0);
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

    private ChunkNoiseSamplingSettings createNoiseSamplingSettingsForChunk(ChunkAccess chunk)
    {
        final NoiseSettings noiseSettings = getNoiseGeneratorSettings().noiseSettings();
        final LevelHeightAccessor level = chunk.getHeightAccessorForGeneration();

        final int cellWidth = noiseSettings.getCellWidth();
        final int cellHeight = noiseSettings.getCellHeight();

        final int minY = Math.max(noiseSettings.minY(), level.getMinBuildHeight());
        final int maxY = Math.min(noiseSettings.minY() + noiseSettings.height(), level.getMaxBuildHeight());

        final int cellCountY = Math.floorDiv(maxY - minY, noiseSettings.getCellHeight());

        final int firstCellX = Math.floorDiv(chunk.getPos().getMinBlockX(), cellWidth);
        final int firstCellY = Math.floorDiv(minY, cellHeight);
        final int firstCellZ = Math.floorDiv(chunk.getPos().getMinBlockZ(), cellWidth);

        return new ChunkNoiseSamplingSettings(minY, 16 / cellWidth, cellCountY, cellWidth, cellHeight, firstCellX, firstCellY, firstCellZ);
    }

    private Map<BiomeVariants, BiomeNoiseSampler> createBiomeSamplersForChunk()
    {
        final ImmutableMap.Builder<BiomeVariants, BiomeNoiseSampler> builder = ImmutableMap.builder();
        for (Map.Entry<BiomeVariants, Supplier<BiomeNoiseSampler>> entry : biomeNoiseSamplers.entrySet())
        {
            builder.put(entry.getKey(), entry.getValue().get());
        }
        return builder.build();
    }
}