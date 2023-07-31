/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraftforge.registries.DeferredRegister;

import net.dries007.tfc.mixin.accessor.ChunkAccessAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.TFCBiomeSource;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.chunkdata.ChunkGeneratorExtension;
import net.dries007.tfc.world.chunkdata.RockData;
import net.dries007.tfc.world.noise.ChunkNoiseSamplingSettings;
import net.dries007.tfc.world.noise.Kernel;
import net.dries007.tfc.world.noise.NoiseSampler;
import net.dries007.tfc.world.surface.SurfaceManager;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class TFCChunkGenerator extends ChunkGenerator implements ChunkGeneratorExtension
{
    public static final Codec<TFCChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        RegistryOps.retrieveRegistry(Registry.STRUCTURE_SET_REGISTRY).forGetter(c -> c.structureSets),
        RegistryOps.retrieveRegistry(Registry.NOISE_REGISTRY).forGetter(c -> c.parameters),
        BiomeSource.CODEC.comapFlatMap(TFCChunkGenerator::guardBiomeSource, Function.identity()).fieldOf("biome_source").forGetter(c -> c.customBiomeSource),
        NoiseGeneratorSettings.CODEC.fieldOf("noise_settings").forGetter(c -> c.settings),
        Codec.BOOL.fieldOf("flat_bedrock").forGetter(c -> c.flatBedrock),
        Codec.LONG.optionalFieldOf("seed", 0L).forGetter(c -> c.seed)
    ).apply(instance, TFCChunkGenerator::new));

    public static final DeferredRegister<Codec<? extends ChunkGenerator>> CHUNK_GENERATOR = DeferredRegister.create(Registry.CHUNK_GENERATOR_REGISTRY, MOD_ID);
    public static final int DECORATION_STEPS = GenerationStep.Decoration.values().length;

    static
    {
        CHUNK_GENERATOR.register("overworld", () -> CODEC);
    }

    public static final int SEA_LEVEL_Y = 63; // Matches vanilla

    public static final Kernel KERNEL_9x9 = Kernel.create((x, z) -> 0.0211640211641D * (1 - 0.03125D * (z * z + x * x)), 4);
    public static final Kernel KERNEL_5x5 = Kernel.create((x, z) -> 0.08D * (1 - 0.125D * (z * z + x * x)), 2);

    /**
     * Composes two levels of sampled weights. It takes two maps of two different resolutions, and re-weights the higher resolution one by replacing specific groups of samples with the respective weights from the lower resolution map.
     * Each element of the higher resolution map is replaced with a proportional average of the same group which is present in the lower resolution map.
     * This has the effect of blending specific groups at closer distances than others, allowing for both smooth and sharp biome transitions.
     * <p>
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
    public static TFCChunkGenerator defaultChunkGenerator(Registry<StructureSet> structures, Registry<NormalNoise.NoiseParameters> parameters, Holder<NoiseGeneratorSettings> noiseGeneratorSettings, Registry<Biome> biomeRegistry, long seed)
    {
        return new TFCChunkGenerator(structures, parameters, TFCBiomeSource.defaultBiomeSource(seed, biomeRegistry), noiseGeneratorSettings, false, seed);
    }

    public static <T> void sampleBiomesCornerContribution(Object2DoubleMap<T> accumulator, Object2DoubleMap<T> corner, double t)
    {
        if (t > 0)
        {
            for (Object2DoubleMap.Entry<T> entry : corner.object2DoubleEntrySet())
            {
                accumulator.mergeDouble(entry.getKey(), entry.getDoubleValue() * t, Double::sum);
            }
        }
    }

    private static Map<BiomeExtension, Supplier<BiomeNoiseSampler>> collectBiomeNoiseSamplers(long seed)
    {
        final ImmutableMap.Builder<BiomeExtension, Supplier<BiomeNoiseSampler>> builder = ImmutableMap.builder();
        for (BiomeExtension variant : TFCBiomes.getExtensions())
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
     * @param groupFunction A function to access a {@link BiomeExtension.Group} from a {@link Biome}.
     * @return A 7x7 array of sampled biome weights, at quart pos resolution, where the (0, 0) index aligns to the (-1, -1) quart position relative to the target chunk.
     */
    private static <T> Object2DoubleMap<T>[] sampleBiomes(ChunkPos pos, Sampler<T> biomeSampler, Function<T, BiomeExtension.Group> groupFunction)
    {
        // First, sample biomes at chunk distance, in a 4x4 grid centered on the target chunk.
        // These are used to build the large-scale biome blending radius
        final Object2DoubleMap<T>[] chunkBiomeWeightArray = newWeightArray(4 * 4);
        final int chunkX = pos.getMinBlockX(), chunkZ = pos.getMinBlockZ(); // Block coordinates
        for (int x = 0; x < 4; x++)
        {
            for (int z = 0; z < 4; z++)
            {
                // x, z = 0, 0 is the -1, -1 chunk relative to chunkX, chunkZ
                final Object2DoubleMap<T> chunkBiomeWeight = new Object2DoubleOpenHashMap<>();
                chunkBiomeWeightArray[x | (z << 2)] = chunkBiomeWeight;
                sampleBiomesAtPositionWithKernel(chunkBiomeWeight, biomeSampler, KERNEL_9x9, 4, chunkX, chunkZ, x - 1, z - 1);
            }
        }

        // A 7x7 grid, in quart positions relative to the target chunk, where (1, 1) is the target chunk origin.
        final Object2DoubleMap<T>[] quartBiomeWeightArray = newWeightArray(7 * 7);
        final Object2DoubleMap<T> chunkBiomeWeight = new Object2DoubleOpenHashMap<>(), wideQuartBiomeWeight = new Object2DoubleOpenHashMap<>();

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
                    final BiomeExtension.Group group = groupFunction.apply(biome);
                    return group.ordinal();
                }, BiomeExtension.Group.SIZE);

                // Same as wideQuartBiomeWeight, but only with a sample radius of 2, rather than 4
                final Object2DoubleMap<T> quartBiomeWeight = new Object2DoubleOpenHashMap<>();
                sampleBiomesAtPositionWithKernel(quartBiomeWeight, biomeSampler, KERNEL_5x5, 2, chunkX, chunkZ, x - 1, z - 1);

                composeSampleWeights(quartBiomeWeight, wideQuartBiomeWeight, biome -> {
                    final BiomeExtension.Group group = groupFunction.apply(biome);
                    return group == BiomeExtension.Group.RIVER ? 1 : 0;
                }, 2);

                quartBiomeWeightArray[x + 7 * z] = quartBiomeWeight;
            }
        }
        return quartBiomeWeightArray;
    }

    private static <T> void sampleBiomesAtPositionWithKernel(Object2DoubleMap<T> weights, Sampler<T> biomeSampler, Kernel kernel, int kernelBits, int chunkX, int chunkZ, int xOffsetInKernelBits, int zOffsetInKernelBits)
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
                final T biome = biomeSampler.get(blockX, blockZ);
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
    private final Registry<StructureSet> structures;
    private final Registry<NormalNoise.NoiseParameters> parameters;
    private final TFCBiomeSource customBiomeSource; // narrowed type from superclass
    private final Holder<NoiseGeneratorSettings> settings; // Supplier is resolved in constructor
    private final boolean flatBedrock;
    private final long seed;

    private final NoiseBasedChunkGenerator stupidMojangChunkGenerator; // Mojang fix your god awful deprecated carver nonsense
    private final FastConcurrentCache<TFCAquifer> aquiferCache;

    private final Map<BiomeExtension, Supplier<BiomeNoiseSampler>> biomeNoiseSamplers;
    private final ChunkDataProvider chunkDataProvider;
    private final SurfaceManager surfaceManager;
    private final NoiseSampler noiseSampler;
    private final boolean hasStructures;

    public TFCChunkGenerator(Registry<StructureSet> structures, Registry<NormalNoise.NoiseParameters> parameters, TFCBiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings, boolean flatBedrock, long seed)
    {
        super(structures, Optional.empty(), biomeSource);
        this.structures = structures;
        this.parameters = parameters;
        this.settings = settings;
        this.customBiomeSource = biomeSource;
        this.flatBedrock = flatBedrock;
        this.seed = seed;

        this.stupidMojangChunkGenerator = new NoiseBasedChunkGenerator(structures, parameters, biomeSource, seed, settings);
        this.aquiferCache = new FastConcurrentCache<>(256);

        this.biomeNoiseSamplers = collectBiomeNoiseSamplers(seed);
        this.chunkDataProvider = customBiomeSource.getChunkDataProvider();
        this.surfaceManager = new SurfaceManager(seed);
        this.noiseSampler = new NoiseSampler(this.settings.value().noiseSettings(), seed, parameters);
        this.hasStructures = structures.size() > 0;
    }

    @Override
    public ChunkDataProvider getChunkDataProvider()
    {
        return chunkDataProvider;
    }

    @Override
    public Aquifer getOrCreateAquifer(ChunkAccess chunk)
    {
        final ChunkNoiseSamplingSettings settings = createNoiseSamplingSettingsForChunk(chunk);
        final ChunkBaseBlockSource baseBlockSource = createBaseBlockSourceForChunk(chunk);
        return getOrCreateAquifer(chunk, settings, baseBlockSource);
    }

    @Override
    protected Codec<TFCChunkGenerator> codec()
    {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed)
    {
        return new TFCChunkGenerator(structures, parameters, customBiomeSource.withSeed(seed), settings, flatBedrock, seed);
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
        // Skip water carving, only do air carving, since we use aquifers
        if (step != GenerationStep.Carving.AIR)
        {
            return;
        }

        // N.B. because this ends up sampling biomes way outside the target chunk range, we cannot guarantee that chunk data will exist for the chunk yet
        // Since that's not the case, when we query the biome source with climate, it may or may not know what climate of biome to return
        // Instead of allowing that unreliability, we assume all biomes carvers are identical to the normal/normal one, and like in base noise generation, only query biomes without climate.
        // This may have strange side effects if people try and mutate carvers on a per-biome basis.
        final BiomeManager customBiomeManager = biomeManager.withDifferentSource((x, y, z) -> customBiomeSource.getNoiseBiome(x, z));
        final PositionalRandomFactory fork = new XoroshiroRandomSource(seed).forkPositional();
        final Random random = new Random();
        final ChunkPos chunkPos = chunk.getPos();

        final ChunkNoiseSamplingSettings settings = createNoiseSamplingSettingsForChunk(chunk);
        final ChunkBaseBlockSource baseBlockSource = createBaseBlockSourceForChunk(chunk);
        final TFCAquifer aquifer = getOrCreateAquifer(chunk, settings, baseBlockSource);

        @SuppressWarnings("ConstantConditions")
        final CarvingContext context = new CarvingContext(stupidMojangChunkGenerator, null, chunk.getHeightAccessorForGeneration(), null);
        final CarvingMask carvingMask = ((ProtoChunk) chunk).getOrCreateCarvingMask(step);

        for (int offsetX = -8; offsetX <= 8; ++offsetX)
        {
            for (int offsetZ = -8; offsetZ <= 8; ++offsetZ)
            {
                final ChunkPos offsetChunkPos = new ChunkPos(chunkPos.x + offsetX, chunkPos.z + offsetZ);
                final ChunkAccess offsetChunk = level.getChunk(offsetChunkPos.x, offsetChunkPos.z);

                @SuppressWarnings("deprecation")
                final Iterable<Holder<ConfiguredWorldCarver<?>>> iterable = offsetChunk
                    .carverBiome(() -> customBiomeSource.getNoiseBiome(QuartPos.fromBlock(offsetChunkPos.getMinBlockX()), QuartPos.fromBlock(offsetChunkPos.getMinBlockZ())))
                    .value()
                    .getGenerationSettings()
                    .getCarvers(step);

                int i = 1;
                for (Holder<ConfiguredWorldCarver<?>> holder : iterable)
                {
                    final long chunkSeed = fork.at(offsetChunkPos.x, i, offsetChunkPos.z).nextLong();

                    random.setSeed(chunkSeed);
                    final ConfiguredWorldCarver<?> carver = holder.value();
                    if (carver.isStartChunk(random))
                    {
                        carver.carve(context, chunk, customBiomeManager::getBiome, random, aquifer, offsetChunkPos, carvingMask);
                    }
                    i++;
                }
            }
        }
    }

    @Override
    public void buildSurface(WorldGenRegion level, StructureFeatureManager structureFeatureManager, ChunkAccess chunk)
    {
        makeBedrock(chunk);
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion level)
    {
        if (!this.settings.value().disableMobGeneration())
        {
            ChunkPos pos = level.getCenter();
            Holder<Biome> biome = level.getBiome(pos.getWorldPosition().atY(level.getMaxBuildHeight() - 1));
            WorldgenRandom random = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.seedUniquifier()));
            random.setDecorationSeed(level.getSeed(), pos.getMinBlockX(), pos.getMinBlockZ());

            NaturalSpawner.spawnMobsForChunkGeneration(level, biome, pos, random);
        }
    }

    @Override
    public TFCBiomeSource getBiomeSource()
    {
        return customBiomeSource;
    }

    @Override
    public int getGenDepth()
    {
        return this.settings.value().noiseSettings().height();
    }

    @Override
    public void createStructures(RegistryAccess dynamicRegistry, StructureFeatureManager structureFeatureManager, ChunkAccess chunk, StructureManager templateManager, long seed)
    {
        chunkDataProvider.get(chunk); // populate chunk data before references to enable placements
        super.createStructures(dynamicRegistry, structureFeatureManager, chunk, templateManager, seed);
    }

    @Override
    public void createReferences(WorldGenLevel level, StructureFeatureManager structureFeatureManager, ChunkAccess chunk)
    {
        super.createReferences(level, structureFeatureManager, chunk);
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureFeatureManager structureFeatureManager)
    {
        final ChunkPos chunkPos = chunk.getPos();
        final SectionPos sectionPos = SectionPos.of(chunkPos, level.getMinSection());
        final BlockPos originPos = sectionPos.origin();

        final Registry<ConfiguredStructureFeature<?, ?>> structureFeatures = level.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
        final Registry<PlacedFeature> placedFeatures = level.registryAccess().registryOrThrow(Registry.PLACED_FEATURE_REGISTRY);

        final Map<Integer, List<ConfiguredStructureFeature<?, ?>>> structureFeaturesByStep = structureFeatures.stream()
            .collect(Collectors.groupingBy(feature -> feature.feature.step().ordinal()));

        final List<BiomeSource.StepFeatureData> orderedFeatures = customBiomeSource.featuresPerStep();
        final long baseSeed = Helpers.hash(128739412341L, originPos);
        final Random random = new WorldgenRandom(new XoroshiroRandomSource(baseSeed));

        final Set<Biome> allAdjacentBiomes = new ObjectArraySet<>();
        ChunkPos.rangeClosed(sectionPos.chunk(), 1).forEach((chunkPos1_) -> {
            final ChunkAccess adjChunk = level.getChunk(chunkPos1_.x, chunkPos1_.z);
            for (LevelChunkSection adjSection : adjChunk.getSections())
            {
                adjSection.getBiomes().getAll(biome -> allAdjacentBiomes.add(biome.value()));
            }
        });

        for (int decorationIndex = 0; decorationIndex < Math.max(DECORATION_STEPS, orderedFeatures.size()); ++decorationIndex)
        {
            if (structureFeatureManager.shouldGenerateFeatures())
            {
                int featureIndex = 0;
                for (ConfiguredStructureFeature<?, ?> feature : structureFeaturesByStep.getOrDefault(decorationIndex, Collections.emptyList()))
                {
                    Helpers.seedLargeFeatures(random, baseSeed, featureIndex, decorationIndex);

                    final Supplier<String> featureName = () -> structureFeatures.getResourceKey(feature).map(Object::toString).orElseGet(feature::toString);

                    try
                    {
                        structureFeatureManager.startsForFeature(sectionPos, feature).forEach(start -> start.placeInChunk(level, structureFeatureManager, this, random, getBoundingBoxForStructure(chunk), chunkPos));
                    }
                    catch (Exception e)
                    {
                        final CrashReport crash = CrashReport.forThrowable(e, "Feature placement");
                        crash.addCategory("Feature").setDetail("Description", featureName::get);
                        throw new ReportedException(crash);
                    }

                    featureIndex++;
                }
            }

            if (decorationIndex < orderedFeatures.size())
            {
                final IntSet featureIndices = new IntArraySet();
                for (Biome biome : allAdjacentBiomes)
                {
                    List<HolderSet<PlacedFeature>> featuresPerBiome = TFCBiomes.getExtensionOrThrow(level, biome).getFlattenedFeatures(biome);
                    if (decorationIndex < featuresPerBiome.size())
                    {
                        final HolderSet<PlacedFeature> featuresPerBiomeAtStep = featuresPerBiome.get(decorationIndex);
                        final BiomeSource.StepFeatureData stepIndex = orderedFeatures.get(decorationIndex);
                        for (Holder<PlacedFeature> holder : featuresPerBiomeAtStep)
                        {
                            featureIndices.add(stepIndex.indexMapping().applyAsInt(holder.value()));
                        }
                    }
                }

                final int[] sortedIndices = featureIndices.toIntArray();
                final BiomeSource.StepFeatureData step = orderedFeatures.get(decorationIndex);

                Arrays.sort(sortedIndices);
                for (int featureIndex : sortedIndices)
                {
                    final PlacedFeature feature = step.features().get(featureIndex);
                    Helpers.seedLargeFeatures(random, baseSeed, featureIndex, decorationIndex);
                    final Supplier<String> featureName = () -> placedFeatures.getResourceKey(feature).map(Object::toString).orElseGet(feature::toString);
                    try
                    {
                        feature.placeWithBiomeCheck(level, this, random, originPos);
                    }
                    catch (Exception e)
                    {
                        final CrashReport crash = CrashReport.forThrowable(e, "Feature placement");
                        crash.addCategory("Feature").setDetail("Description", featureName);
                        throw new ReportedException(crash);
                    }
                }
            }
        }

        level.setCurrentlyGenerating(null);
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor mainExecutor, Blender oldTerrainBlender, StructureFeatureManager structureFeatureManager, ChunkAccess chunk)
    {
        // Initialization
        final ChunkNoiseSamplingSettings settings = createNoiseSamplingSettingsForChunk(chunk);
        final LevelAccessor actualLevel = (LevelAccessor) ((ChunkAccessAccessor) chunk).accessor$getLevelHeightAccessor();
        final ChunkPos chunkPos = chunk.getPos();
        final RandomSource random = new XoroshiroRandomSource(chunkPos.x * 1842639486192314L, chunkPos.z * 579238196380231L);
        final ChunkData chunkData = chunkDataProvider.get(chunk);
        final RockData rockData = chunkData.getRockData();

        // Lock sections
        final Set<LevelChunkSection> sections = new HashSet<>();
        for (LevelChunkSection section : chunk.getSections())
        {
            section.acquire();
            sections.add(section);
        }

        final Object2DoubleMap<BiomeExtension>[] biomeWeights = sampleBiomes(chunkPos, this::sampleBiomeVariants, BiomeExtension::getGroup);
        final ChunkBaseBlockSource baseBlockSource = createBaseBlockSourceForChunk(chunk);
        final ChunkNoiseFiller filler = new ChunkNoiseFiller(actualLevel, (ProtoChunk) chunk, biomeWeights, customBiomeSource, createBiomeSamplersForChunk(), customBiomeSource::getBiome, noiseSampler, baseBlockSource, settings, getSeaLevel());

        filler.setupAquiferSurfaceHeight(this::sampleBiomeVariants);
        chunkData.setAquiferSurfaceHeight(filler.aquifer().getSurfaceHeights()); // Record this in the chunk data so caves can query it accurately
        rockData.setSurfaceHeight(filler.getSurfaceHeight()); // Need to set this in the rock data before we can fill the chunk proper
        filler.fillFromNoise();

        aquiferCache.set(chunkPos.x, chunkPos.z, filler.aquifer());

        // Unlock before surfaces are built, as they use locks directly
        sections.forEach(LevelChunkSection::release);

        surfaceManager.buildSurface(actualLevel, chunk, getRockLayerSettings(), chunkData, filler.getLocalBiomes(), filler.getLocalBiomeWeights(), filler.getSlopeMap(), random, getSeaLevel(), settings.minY());

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
        return settings.value().noiseSettings().minY();
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level)
    {
        final ChunkPos pos = new ChunkPos(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
        return (int) createHeightFillerForChunk(pos).sampleHeight(x, z);
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level)
    {
        return new NoiseColumn(0, new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(List<String> list, BlockPos pos) {}

    @Override
    public WeightedRandomList<MobSpawnSettings.SpawnerData> getMobsAt(Holder<Biome> biome, StructureFeatureManager structureManager, MobCategory category, BlockPos pos)
    {
        return hasStructures ? super.getMobsAt(biome, structureManager, category, pos) : biome.value().getMobSettings().getMobs(category);
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

    private BoundingBox getBoundingBoxForStructure(ChunkAccess chunk)
    {
        final ChunkPos pos = chunk.getPos();
        final int blockX = pos.getMinBlockX(), blockZ = pos.getMinBlockZ();
        final LevelHeightAccessor level = chunk.getHeightAccessorForGeneration();
        return new BoundingBox(blockX, level.getMinBuildHeight() + 1, blockZ, blockX + 15, level.getMaxBuildHeight() - 1, blockZ + 15);
    }

    private BiomeExtension sampleBiomeVariants(int blockX, int blockZ)
    {
        return customBiomeSource.getNoiseBiomeVariants(QuartPos.fromBlock(blockX), QuartPos.fromBlock(blockZ));
    }

    public ChunkHeightFiller createHeightFillerForChunk(ChunkPos pos)
    {
        final Object2DoubleMap<BiomeExtension>[] biomeWeights = sampleBiomes(pos, this::sampleBiomeVariants, BiomeExtension::getGroup);
        return new ChunkHeightFiller(createBiomeSamplersForChunk(), biomeWeights);
    }

    private ChunkBaseBlockSource createBaseBlockSourceForChunk(ChunkAccess chunk)
    {
        final RockData rockData = chunkDataProvider.get(chunk).getRockData();
        return new ChunkBaseBlockSource(rockData, this::sampleBiomeVariants);
    }

    private ChunkNoiseSamplingSettings createNoiseSamplingSettingsForChunk(ChunkAccess chunk)
    {
        return createNoiseSamplingSettingsForChunk(chunk.getPos(), chunk.getHeightAccessorForGeneration());
    }

    private ChunkNoiseSamplingSettings createNoiseSamplingSettingsForChunk(ChunkPos pos, LevelHeightAccessor level)
    {
        final NoiseSettings noiseSettings = settings.value().noiseSettings();

        final int cellWidth = noiseSettings.getCellWidth();
        final int cellHeight = noiseSettings.getCellHeight();

        final int minY = Math.max(noiseSettings.minY(), level.getMinBuildHeight());
        final int maxY = Math.min(noiseSettings.minY() + noiseSettings.height(), level.getMaxBuildHeight());

        final int cellCountY = Math.floorDiv(maxY - minY, noiseSettings.getCellHeight());

        final int firstCellX = Math.floorDiv(pos.getMinBlockX(), cellWidth);
        final int firstCellY = Math.floorDiv(minY, cellHeight);
        final int firstCellZ = Math.floorDiv(pos.getMinBlockZ(), cellWidth);

        return new ChunkNoiseSamplingSettings(minY, 16 / cellWidth, cellCountY, cellWidth, cellHeight, firstCellX, firstCellY, firstCellZ);
    }

    private Map<BiomeExtension, BiomeNoiseSampler> createBiomeSamplersForChunk()
    {
        final ImmutableMap.Builder<BiomeExtension, BiomeNoiseSampler> builder = ImmutableMap.builder();
        for (Map.Entry<BiomeExtension, Supplier<BiomeNoiseSampler>> entry : biomeNoiseSamplers.entrySet())
        {
            builder.put(entry.getKey(), entry.getValue().get());
        }
        return builder.build();
    }

    private TFCAquifer getOrCreateAquifer(ChunkAccess chunk, ChunkNoiseSamplingSettings settings, ChunkBaseBlockSource baseBlockSource)
    {
        final ChunkPos chunkPos = chunk.getPos();

        TFCAquifer aquifer = aquiferCache.getIfPresent(chunkPos.x, chunkPos.z);
        if (aquifer == null)
        {
            final ChunkData chunkData = chunkDataProvider.get(chunk);

            aquifer = new TFCAquifer(chunkPos, settings, baseBlockSource, getSeaLevel(), noiseSampler.positionalRandomFactory, noiseSampler.barrierNoise);
            aquifer.setSurfaceHeights(chunkData.getAquiferSurfaceHeight());

            aquiferCache.set(chunkPos.x, chunkPos.z, aquifer);
        }
        return aquifer;
    }
}