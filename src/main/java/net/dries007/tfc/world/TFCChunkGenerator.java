/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
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
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraftforge.registries.DeferredRegister;

import net.dries007.tfc.mixin.accessor.ChunkAccessAccessor;
import net.dries007.tfc.mixin.accessor.ChunkGeneratorAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.biome.BiomeBlendType;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.BiomeSourceExtension;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataGenerator;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.chunkdata.RegionChunkDataGenerator;
import net.dries007.tfc.world.chunkdata.RockData;
import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.layer.framework.AreaFactory;
import net.dries007.tfc.world.layer.framework.ConcurrentArea;
import net.dries007.tfc.world.noise.ChunkNoiseSamplingSettings;
import net.dries007.tfc.world.noise.Kernel;
import net.dries007.tfc.world.noise.NoiseSampler;
import net.dries007.tfc.world.region.RegionGenerator;
import net.dries007.tfc.world.river.RiverBlendType;
import net.dries007.tfc.world.river.RiverNoiseSampler;
import net.dries007.tfc.world.settings.Settings;
import net.dries007.tfc.world.surface.SurfaceManager;

import static net.dries007.tfc.TerraFirmaCraft.*;

@SuppressWarnings("NotNullFieldNotInitialized") // Since we do a separate `init` step
public class TFCChunkGenerator extends ChunkGenerator implements ChunkGeneratorExtension
{
    public static final Codec<TFCChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BiomeSource.CODEC.comapFlatMap(TFCChunkGenerator::guardBiomeSource, BiomeSourceExtension::self).fieldOf("biome_source").forGetter(c -> c.customBiomeSource),
        NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(c -> c.noiseSettings),
        Settings.CODEC.fieldOf("tfc_settings").forGetter(c -> c.settings)
    ).apply(instance, TFCChunkGenerator::new));

    public static final DeferredRegister<Codec<? extends ChunkGenerator>> CHUNK_GENERATOR = DeferredRegister.create(Registries.CHUNK_GENERATOR, MOD_ID);
    public static final int DECORATION_STEPS = GenerationStep.Decoration.values().length;
    public static final int SEA_LEVEL_Y = 63; // Matches vanilla
    public static final Kernel KERNEL_9x9 = Kernel.create((x, z) -> 0.0211640211641D * (1 - 0.03125D * (z * z + x * x)), 4);

    static
    {
        CHUNK_GENERATOR.register("overworld", () -> CODEC);
    }

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

    private static DataResult<BiomeSourceExtension> guardBiomeSource(BiomeSource source)
    {
        return source instanceof BiomeSourceExtension s ? DataResult.success(s) : DataResult.error(() -> "Must be a " + BiomeSourceExtension.class.getSimpleName());
    }

    /**
     * @param pos           The target chunk pos.
     * @param biomeSampler  A sampler for biomes, in block coordinates.
     * @param groupFunction A function to access a {@link BiomeBlendType} from a {@link Biome}.
     * @return A 7x7 array of sampled biome weights, at quart pos resolution, where the (0, 0) index aligns to the (-1, -1) quart position relative to the target chunk.
     */
    private static <T> Object2DoubleMap<T>[] sampleBiomes(ChunkPos pos, Sampler<T> biomeSampler, Function<T, BiomeBlendType> groupFunction)
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
        final Object2DoubleMap<T> chunkBiomeWeight = new Object2DoubleOpenHashMap<>();

        for (int x = 0; x < 7; x++)
        {
            for (int z = 0; z < 7; z++)
            {
                // Reset
                final Object2DoubleMap<T> quartBiomeWeight = new Object2DoubleOpenHashMap<>();
                chunkBiomeWeight.clear();

                sampleBiomesAtPositionWithKernel(quartBiomeWeight, biomeSampler, KERNEL_9x9, 2, chunkX, chunkZ, x - 1, z - 1);

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
                composeSampleWeights(quartBiomeWeight, chunkBiomeWeight, biome -> {
                    final BiomeBlendType group = groupFunction.apply(biome);
                    return group.ordinal();
                }, BiomeBlendType.SIZE);

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
    private final BiomeSourceExtension customBiomeSource; // narrowed type from superclass
    private final Holder<NoiseGeneratorSettings> noiseSettings; // Supplier is resolved in constructor
    private Settings settings;

    private final NoiseBasedChunkGenerator stupidMojangChunkGenerator; // Mojang fix your god awful deprecated carver nonsense
    private final FastConcurrentCache<TFCAquifer> aquiferCache;

    private ChunkDataProvider chunkDataProvider;
    private long noiseSamplerSeed;
    private SurfaceManager surfaceManager;
    private NoiseSampler noiseSampler;

    public TFCChunkGenerator(BiomeSourceExtension biomeSource, Holder<NoiseGeneratorSettings> noiseSettings, Settings settings)
    {
        super(biomeSource.self());

        this.noiseSettings = noiseSettings;
        this.customBiomeSource = biomeSource;
        this.settings = settings;

        this.stupidMojangChunkGenerator = new NoiseBasedChunkGenerator(biomeSource.self(), noiseSettings);
        this.aquiferCache = new FastConcurrentCache<>(256);
    }

    @Override
    public Settings settings()
    {
        return settings;
    }

    @Override
    public void applySettings(UnaryOperator<Settings> settings)
    {
        this.settings = settings.apply(this.settings);
    }

    @Override
    public ChunkDataProvider chunkDataProvider()
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

    /**
     * Vanilla initializes this as the {@link RandomState} as part of {@link net.minecraft.server.level.ChunkMap}. For various reasons that is terribly
     * difficult for mods to edit, but we need data that is only accessible at the initialization of {@link RandomState} (like world seed). So, I think
     * the best solution is to intercept that constructor, but initialize data just as part of the chunk generator.
     * <p>
     * This works for TFC as we have an entirely custom chunk generator, and have no need to share {@link RandomState}-like data with any other generator.
     */
    @Override
    public void initRandomState(ServerLevel level)
    {
        final long seed = level.getSeed();
        final RandomSource random = new XoroshiroRandomSource(seed);

        final RegionGenerator regionGenerator = new RegionGenerator(settings, random);
        final ChunkDataGenerator chunkDataGenerator = RegionChunkDataGenerator.create(random.nextLong(), settings.rockLayerSettings(), regionGenerator);
        final AreaFactory factory = TFCLayers.createRegionBiomeLayer(regionGenerator, random.nextLong());
        final ConcurrentArea<BiomeExtension> biomeLayer = new ConcurrentArea<>(factory, TFCLayers::getFromLayerId);

        this.noiseSamplerSeed = seed;
        this.noiseSampler = new NoiseSampler(noiseSettings.get().noiseSettings(), random.nextLong(), level.registryAccess().lookupOrThrow(Registries.NOISE));
        this.chunkDataProvider = new ChunkDataProvider(chunkDataGenerator);
        this.surfaceManager = new SurfaceManager(seed);

        this.customBiomeSource.initRandomState(regionGenerator, biomeLayer);
    }

    public ChunkHeightFiller createHeightFillerForChunk(ChunkPos pos)
    {
        final Object2DoubleMap<BiomeExtension>[] biomeWeights = sampleBiomes(pos, this::sampleBiomeNoRiver, BiomeExtension::biomeBlendType);
        return new ChunkHeightFiller(createBiomeSamplersForChunk(), biomeWeights);
    }

    @Override
    protected Codec<TFCChunkGenerator> codec()
    {
        return CODEC;
    }

    @Override
    public CompletableFuture<ChunkAccess> createBiomes(Executor executor, RandomState state, Blender legacyTerrainBlender, StructureManager structureFeatureManager, ChunkAccess chunk)
    {
        return CompletableFuture.supplyAsync(() -> {
            chunkDataProvider.get(chunk);
            chunk.fillBiomesFromNoise((quartX, quartY, quartZ, sampler) -> customBiomeSource.getBiome(quartX, quartZ), NoopClimateSampler.INSTANCE);
            return chunk;
        }, Util.backgroundExecutor());
    }

    @Override
    public void applyCarvers(WorldGenRegion level, long seed, RandomState state, BiomeManager biomeManager, StructureManager structureFeatureManager, ChunkAccess chunk, GenerationStep.Carving step)
    {
        // Skip water carving, only do air carving, since we use aquifers
        if (step != GenerationStep.Carving.AIR)
        {
            return;
        }

        final BiomeManager customBiomeManager = biomeManager.withDifferentSource((x, y, z) -> customBiomeSource.getBiome(x, z));
        final PositionalRandomFactory fork = new XoroshiroRandomSource(seed).forkPositional();
        final WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
        final ChunkPos chunkPos = chunk.getPos();

        final ChunkNoiseSamplingSettings settings = createNoiseSamplingSettingsForChunk(chunk);
        final ChunkBaseBlockSource baseBlockSource = createBaseBlockSourceForChunk(chunk);
        final TFCAquifer aquifer = getOrCreateAquifer(chunk, settings, baseBlockSource);

        @SuppressWarnings("ConstantConditions")
        final CarvingContext context = new CarvingContext(stupidMojangChunkGenerator, null, chunk.getHeightAccessorForGeneration(), null, state, this.noiseSettings.value().surfaceRule());
        final CarvingMask carvingMask = ((ProtoChunk) chunk).getOrCreateCarvingMask(step);

        for (int offsetX = -8; offsetX <= 8; ++offsetX)
        {
            for (int offsetZ = -8; offsetZ <= 8; ++offsetZ)
            {
                final ChunkPos offsetChunkPos = new ChunkPos(chunkPos.x + offsetX, chunkPos.z + offsetZ);
                final ChunkAccess offsetChunk = level.getChunk(offsetChunkPos.x, offsetChunkPos.z);

                @SuppressWarnings("deprecation")
                final Iterable<Holder<ConfiguredWorldCarver<?>>> iterable = offsetChunk
                    .carverBiome(() -> customBiomeSource.getBiome(QuartPos.fromBlock(offsetChunkPos.getMinBlockX()), QuartPos.fromBlock(offsetChunkPos.getMinBlockZ())).value().getGenerationSettings())
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
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureFeatureManager)
    {
        final ChunkPos chunkPos = chunk.getPos();
        final SectionPos sectionPos = SectionPos.of(chunkPos, level.getMinSection());
        final BlockPos originPos = sectionPos.origin();

        final Registry<Structure> structureFeatures = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        final Registry<PlacedFeature> placedFeatures = level.registryAccess().registryOrThrow(Registries.PLACED_FEATURE);

        final Map<Integer, List<Structure>> structureFeaturesByStep = structureFeatures.stream()
            .collect(Collectors.groupingBy(feature -> feature.step().ordinal()));

        final List<FeatureSorter.StepFeatureData> orderedFeatures = ((ChunkGeneratorAccessor) this).accessor$getFeaturesPerStep().get();
        final WorldgenRandom random = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.generateUniqueSeed()));
        final long baseSeed = Helpers.hash(128739412341L, originPos);

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
            if (structureFeatureManager.shouldGenerateStructures())
            {
                int featureIndex = 0;
                for (Structure feature : structureFeaturesByStep.getOrDefault(decorationIndex, Collections.emptyList()))
                {
                    Helpers.seedLargeFeatures(random, baseSeed, featureIndex, decorationIndex);

                    final Supplier<String> featureName = () -> structureFeatures.getResourceKey(feature).map(Object::toString).orElseGet(feature::toString);

                    try
                    {
                        level.setCurrentlyGenerating(featureName);
                        structureFeatureManager.startsForStructure(sectionPos, feature).forEach(start -> start.placeInChunk(level, structureFeatureManager, this, random, getBoundingBoxForStructure(chunk), chunkPos));
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
                        final FeatureSorter.StepFeatureData stepIndex = orderedFeatures.get(decorationIndex);
                        for (Holder<PlacedFeature> holder : featuresPerBiomeAtStep)
                        {
                            featureIndices.add(stepIndex.indexMapping().applyAsInt(holder.value()));
                        }
                    }
                }

                final int[] sortedIndices = featureIndices.toIntArray();
                final FeatureSorter.StepFeatureData step = orderedFeatures.get(decorationIndex);

                Arrays.sort(sortedIndices);
                for (int featureIndex : sortedIndices)
                {
                    final PlacedFeature feature = step.features().get(featureIndex);
                    Helpers.seedLargeFeatures(random, baseSeed, featureIndex, decorationIndex);
                    final Supplier<String> featureName = () -> placedFeatures.getResourceKey(feature).map(Object::toString).orElseGet(feature::toString);
                    try
                    {
                        level.setCurrentlyGenerating(featureName);
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
    public void buildSurface(WorldGenRegion level, StructureManager structureFeatureManager, RandomState state, ChunkAccess chunk)
    {
        makeBedrock(chunk);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void spawnOriginalMobs(WorldGenRegion level)
    {
        if (!this.noiseSettings.value().disableMobGeneration())
        {
            final ChunkPos pos = level.getCenter();
            final Holder<Biome> biome = level.getBiome(pos.getWorldPosition().atY(level.getMaxBuildHeight() - 1));
            final WorldgenRandom random = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.generateUniqueSeed()));
            random.setDecorationSeed(level.getSeed(), pos.getMinBlockX(), pos.getMinBlockZ());

            NaturalSpawner.spawnMobsForChunkGeneration(level, biome, pos, random);
        }
    }

    @Override
    public BiomeSource getBiomeSource()
    {
        return customBiomeSource.self();
    }

    @Override
    public int getGenDepth()
    {
        return this.noiseSettings.value().noiseSettings().height();
    }

    @Override
    public void createStructures(RegistryAccess dynamicRegistry, ChunkGeneratorStructureState structureState, StructureManager structureFeatureManager, ChunkAccess chunk, StructureTemplateManager templateManager)
    {
        chunkDataProvider.get(chunk); // populate chunk data before references to enable placements
        super.createStructures(dynamicRegistry, structureState, structureFeatureManager, chunk, templateManager);
    }

    @Override
    public void createReferences(WorldGenLevel level, StructureManager structureFeatureManager, ChunkAccess chunk)
    {
        super.createReferences(level, structureFeatureManager, chunk);
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor mainExecutor, Blender oldTerrainBlender, RandomState rawState, StructureManager structureFeatureManager, ChunkAccess chunk)
    {
        // Initialization
        final ChunkNoiseSamplingSettings settings = createNoiseSamplingSettingsForChunk(chunk);
        final LevelAccessor actualLevel = (LevelAccessor) ((ChunkAccessAccessor) chunk).accessor$getLevelHeightAccessor();
        final ChunkPos chunkPos = chunk.getPos();
        final RandomSource random = new XoroshiroRandomSource(chunkPos.x * 1842639486192314L, chunkPos.z * 579238196380231L);
        final ChunkData chunkData = chunkDataProvider.get(chunk);

        // Lock sections
        final Set<LevelChunkSection> sections = new HashSet<>();
        for (LevelChunkSection section : chunk.getSections())
        {
            section.acquire();
            sections.add(section);
        }

        final Object2DoubleMap<BiomeExtension>[] biomeWeights = sampleBiomes(chunkPos, this::sampleBiomeNoRiver, BiomeExtension::biomeBlendType);
        final ChunkBaseBlockSource baseBlockSource = createBaseBlockSourceForChunk(chunk);
        final ChunkNoiseFiller filler = new ChunkNoiseFiller((ProtoChunk) chunk, biomeWeights, customBiomeSource, createBiomeSamplersForChunk(), createRiverSamplersForChunk(), noiseSampler, baseBlockSource, settings, getSeaLevel(), Beardifier.forStructuresInChunk(structureFeatureManager, chunkPos));

        return CompletableFuture.supplyAsync(() -> {
            filler.sampleAquiferSurfaceHeight(this::sampleBiomeNoRiver);
            chunkData.generateFull(filler.surfaceHeight(), filler.aquifer().surfaceHeights());
            chunkData.getRockData().useCache(chunkPos);
            filler.fillFromNoise();

            aquiferCache.set(chunkPos.x, chunkPos.z, filler.aquifer());

            return chunk;
        }, Util.backgroundExecutor()).whenCompleteAsync((ret, error) -> {
            // Unlock before surfaces are built, as they use locks directly
            sections.forEach(LevelChunkSection::release);

            surfaceManager.buildSurface(actualLevel, chunk, rockLayerSettings(), chunkData, filler.localBiomes(), filler.localBiomesNoRivers(), filler.localBiomeWeights(), filler.createSlopeMap(), random, getSeaLevel(), settings.minY());
        }, mainExecutor);
    }

    @Override
    public int getSeaLevel()
    {
        return SEA_LEVEL_Y;
    }

    @Override
    public int getMinY()
    {
        return noiseSettings.value().noiseSettings().minY();
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState state)
    {
        final ChunkPos pos = new ChunkPos(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
        return (int) createHeightFillerForChunk(pos).sampleHeight(x, z);
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState state)
    {
        return new NoiseColumn(0, new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(List<String> list, RandomState state, BlockPos pos) {}

    /**
     * Builds either a single flat layer of bedrock, or natural vanilla bedrock
     * Writes directly to the bottom chunk section for better efficiency
     */
    private void makeBedrock(ChunkAccess chunk)
    {
        final ChunkPos chunkPos = chunk.getPos();
        final RandomSource random = new XoroshiroRandomSource(chunkPos.x * 2369412341L, chunkPos.z * 8192836412341L);
        final LevelChunkSection bottomSection = chunk.getSection(0);
        final BlockState bedrock = Blocks.BEDROCK.defaultBlockState();

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                if (settings.flatBedrock())
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

    private BiomeExtension sampleBiomeNoRiver(int blockX, int blockZ)
    {
        return customBiomeSource.getBiomeExtensionNoRiver(QuartPos.fromBlock(blockX), QuartPos.fromBlock(blockZ));
    }

    private ChunkBaseBlockSource createBaseBlockSourceForChunk(ChunkAccess chunk)
    {
        final RockData rockData = chunkDataProvider.get(chunk).getRockData();
        return new ChunkBaseBlockSource(rockData, this::sampleBiomeNoRiver);
    }

    private ChunkNoiseSamplingSettings createNoiseSamplingSettingsForChunk(ChunkAccess chunk)
    {
        return createNoiseSamplingSettingsForChunk(chunk.getPos(), chunk.getHeightAccessorForGeneration());
    }

    private ChunkNoiseSamplingSettings createNoiseSamplingSettingsForChunk(ChunkPos pos, LevelHeightAccessor level)
    {
        final NoiseSettings noiseSettings = this.noiseSettings.value().noiseSettings();

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

    private Map<BiomeExtension, BiomeNoiseSampler> createBiomeSamplersForChunk()
    {
        final ImmutableMap.Builder<BiomeExtension, BiomeNoiseSampler> builder = ImmutableMap.builder();
        for (BiomeExtension extension : TFCBiomes.getExtensions())
        {
            final BiomeNoiseSampler sampler = extension.createNoiseSampler(noiseSamplerSeed);
            if (sampler != null)
            {
                builder.put(extension, sampler);
            }
        }
        return builder.build();
    }

    private Map<RiverBlendType, RiverNoiseSampler> createRiverSamplersForChunk()
    {
        final EnumMap<RiverBlendType, RiverNoiseSampler> builder = new EnumMap<>(RiverBlendType.class);
        for (RiverBlendType blendType : RiverBlendType.ALL)
        {
            builder.put(blendType, blendType.createNoiseSampler(noiseSamplerSeed));
        }
        return builder;
    }
}