/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.EmptyBlockReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.structure.StructureManager;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.dries007.tfc.mixin.world.gen.HeightmapAccessor;
import net.dries007.tfc.mixin.world.gen.carver.ConfiguredCarverAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.biome.*;
import net.dries007.tfc.world.carver.IContextCarver;
import net.dries007.tfc.world.chunk.FastChunkPrimer;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataGenerator;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.chunkdata.ITFCChunkGenerator;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.surfacebuilder.IContextSurfaceBuilder;

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

    /**
     * This is the default instance used in the TFC preset, both on client and server
     */
    public static TFCChunkGenerator createDefaultPreset(Supplier<DimensionSettings> dimensionSettings, Registry<Biome> biomeRegistry, long seed)
    {
        return new TFCChunkGenerator(new TFCBiomeProvider(seed, 8_000, new TFCBiomeProvider.LayerSettings(), new TFCBiomeProvider.ClimateSettings(), biomeRegistry), dimensionSettings, false, seed);
    }

    // Noise
    private final Map<BiomeVariants, INoise2D> biomeHeightNoise;
    private final Map<BiomeVariants, INoise2D> biomeCarvingCenterNoise;
    private final Map<BiomeVariants, INoise2D> biomeCarvingHeightNoise;
    private final INoiseGenerator surfaceDepthNoise;

    private final ChunkDataProvider chunkDataProvider;
    private final ChunkBlockReplacer blockReplacer;

    // Properties set from codec
    private final TFCBiomeProvider biomeProvider;
    private final DimensionSettings settings;
    private final boolean flatBedrock;
    private final long seed;

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
        final long biomeNoiseSeed = seedGenerator.nextLong();

        TFCBiomes.getVariants().forEach(variant -> {
            biomeHeightNoise.put(variant, variant.createNoiseLayer(biomeNoiseSeed));
            if (variant instanceof CarvingBiomeVariants)
            {
                Pair<INoise2D, INoise2D> carvingNoise = ((CarvingBiomeVariants) variant).createCarvingNoiseLayer(biomeNoiseSeed);
                biomeCarvingCenterNoise.put(variant, carvingNoise.getFirst());
                biomeCarvingHeightNoise.put(variant, carvingNoise.getSecond());
            }
        });
        surfaceDepthNoise = new PerlinNoiseGenerator(seedGenerator, IntStream.rangeClosed(-3, 0)); // From vanilla

        // Generators / Providers
        this.chunkDataProvider = new ChunkDataProvider(new ChunkDataGenerator(seedGenerator, this.biomeProvider.getLayerSettings())); // Chunk data
        this.blockReplacer = new ChunkBlockReplacer(seedGenerator.nextLong()); // Replaces default world gen blocks with TFC variants, after surface generation
        this.biomeProvider.setChunkDataProvider(chunkDataProvider); // Allow biomes to use the chunk data temperature / rainfall variation
    }

    @Override
    public ChunkDataProvider getChunkDataProvider()
    {
        return chunkDataProvider;
    }

    @Override
    public TFCBiomeProvider getBiomeSource()
    {
        return biomeProvider;
    }

    public ChunkBlockReplacer getBlockReplacer()
    {
        return blockReplacer;
    }

    @Override
    public void createBiomes(Registry<Biome> biomeIdRegistry, IChunk chunkIn)
    {
        // Saves 98% of vanilla biome generation calls
        ((ChunkPrimer) chunkIn).setBiomes(new ColumnBiomeContainer(biomeIdRegistry, chunkIn.getPos(), biomeProvider));
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
    public void applyCarvers(long worldSeed, BiomeManager biomeManager, IChunk chunkIn, GenerationStage.Carving stage)
    {
        // Pull the ole' switcheroo
        stage = stage == GenerationStage.Carving.AIR ? GenerationStage.Carving.LIQUID : GenerationStage.Carving.AIR;

        final ChunkPos chunkPos = chunkIn.getPos();
        final BiomeGenerationSettings biomeGenerationSettings = biomeSource.getNoiseBiome(chunkPos.x << 2, 0, chunkPos.z << 2).getGenerationSettings();
        final BiomeManager delegateBiomeManager = biomeManager.withDifferentSource(this.biomeSource);
        final SharedSeedRandom random = new SharedSeedRandom();
        final List<Supplier<ConfiguredCarver<?>>> carvers = biomeGenerationSettings.getCarvers(stage);
        final BitSet liquidCarvingMask = ((ChunkPrimer) chunkIn).getOrCreateCarvingMask(GenerationStage.Carving.LIQUID);
        final BitSet currentCarvingMask = ((ChunkPrimer) chunkIn).getOrCreateCarvingMask(stage);
        final int chunkX = chunkPos.x;
        final int chunkZ = chunkPos.z;

        for (Supplier<ConfiguredCarver<?>> lazyCarver : biomeGenerationSettings.getCarvers(stage))
        {
            final WorldCarver<?> carver = ((ConfiguredCarverAccessor) lazyCarver.get()).accessor$getWorldCarver();
            if (carver instanceof IContextCarver)
            {
                ((IContextCarver) carver).setContext(worldSeed, stage, liquidCarvingMask);
            }
        }

        for (int x = chunkX - 8; x <= chunkX + 8; ++x)
        {
            for (int z = chunkZ - 8; z <= chunkZ + 8; ++z)
            {
                int index = 0;
                for (Supplier<ConfiguredCarver<?>> lazyCarver : carvers)
                {
                    final ConfiguredCarver<?> carver = lazyCarver.get();

                    random.setLargeFeatureSeed(worldSeed + index, x, z);
                    if (carver.isStartChunk(random, x, z))
                    {
                        carver.carve(chunkIn, delegateBiomeManager::getBiome, random, getSeaLevel(), x, z, chunkX, chunkZ, currentCarvingMask);
                    }
                    index++;
                }
            }
        }
    }

    /**
     * Since we build surface in {@link TFCChunkGenerator#makeBase(IWorld, IChunk)}, we just have to make bedrock and replace surface with TFC blocks here.
     */
    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion worldGenRegion, IChunk chunk)
    {
        final ChunkPos chunkPos = chunk.getPos();
        final SharedSeedRandom random = new SharedSeedRandom();
        final IChunk fastChunk = FastChunkPrimer.deslowificate(chunk);

        random.setBaseChunkSeed(chunkPos.x, chunkPos.z);
        makeBedrock(fastChunk, random);

        final ChunkData chunkData = chunkDataProvider.get(chunkPos, ChunkData.Status.ROCKS);
        blockReplacer.replace(worldGenRegion, fastChunk, chunkData);
        FastChunkPrimer.updateChunkHeightMaps(fastChunk);
    }

    @Override
    public int getSpawnHeight()
    {
        return SPAWN_HEIGHT;
    }

    /**
     * This runs after biome generation. In order to do accurate surface placement, we don't use the already generated biome container, as the biome magnifier really sucks for definition on cliffs
     */
    @Override
    public void fillFromNoise(IWorld world, StructureManager structureManager, IChunk chunk)
    {
        // Initialization
        final FastChunkPrimer fastChunk = FastChunkPrimer.deslowificate(chunk);
        final ChunkPos chunkPos = chunk.getPos();
        final SharedSeedRandom random = new SharedSeedRandom();
        final int chunkX = chunkPos.getMinBlockX(), chunkZ = chunkPos.getMinBlockZ();
        final BlockPos.Mutable pos = new BlockPos.Mutable();

        random.setBaseChunkSeed(chunkPos.x, chunkPos.z);

        // The accurate version of biomes which we use for surface building
        // These are calculated during height generation in order to generate cliffs with harsh borders between biomes
        final Biome[] localBiomes = new Biome[16 * 16];

        // The biome weights at different distance intervals
        final Object2DoubleMap<Biome> weightMap16 = new Object2DoubleOpenHashMap<>(4), weightMap4 = new Object2DoubleOpenHashMap<>(4), weightMap1 = new Object2DoubleOpenHashMap<>(4), carvingWeightMap1 = new Object2DoubleOpenHashMap<>(4);

        // Faster than vanilla (only does 2d interpolation) and uses the already generated biomes by the chunk where possible
        final ChunkArraySampler.CoordinateAccessor<Biome> biomeAccessor = (x, z) -> (Biome) SmoothColumnBiomeMagnifier.SMOOTH.getBiome(seed, chunkX + x, 0, chunkZ + z, world);
        final Function<Biome, BiomeVariants> variantAccessor = biome -> TFCBiomes.getExtensionOrThrow(world, biome).getVariants();

        final Biome[] sampledBiomes16 = ChunkArraySampler.fillSampledArray(new Biome[10 * 10], biomeAccessor, 4);
        final Biome[] sampledBiomes4 = ChunkArraySampler.fillSampledArray(new Biome[13 * 13], biomeAccessor, 2);
        final Biome[] sampledBiomes1 = ChunkArraySampler.fillSampledArray(new Biome[24 * 24], biomeAccessor);

        final Mutable<Biome> mutableBiome = new MutableObject<>();

        final BitSet airCarvingMask = fastChunk.getDelegate().getOrCreateCarvingMask(GenerationStage.Carving.AIR);
        final BitSet liquidCarvingMask = fastChunk.getDelegate().getOrCreateCarvingMask(GenerationStage.Carving.LIQUID);

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                pos.set(chunkX + x, 0, chunkZ + z);

                // Sample biome weights at different distances
                ChunkArraySampler.fillSampledWeightMap(sampledBiomes16, weightMap16, 4, x, z);
                ChunkArraySampler.fillSampledWeightMap(sampledBiomes4, weightMap4, 2, x, z);
                ChunkArraySampler.fillSampledWeightMap(sampledBiomes1, weightMap1, x, z);

                // Group biomes at different distances. This has the effect of making some biome transitions happen over larger distances than others.
                // This is used to make most land biomes blend at maximum distance, while allowing biomes such as rivers to blend at short distances, creating better cliffs as river biomes are smaller width than other biomes.
                ChunkArraySampler.reduceGroupedWeightMap(weightMap4, weightMap16, variantAccessor.andThen(BiomeVariants::getLargeGroup), BiomeVariants.LargeGroup.SIZE);
                ChunkArraySampler.reduceGroupedWeightMap(weightMap1, weightMap4, variantAccessor.andThen(BiomeVariants::getSmallGroup), BiomeVariants.SmallGroup.SIZE);

                // First, always calculate the center height, by ignoring any possibility of carving biomes
                // The variant accessor is called for each possible biome - if we detect a carving variant, then mark it as found
                MutableBoolean hasCarvingBiomes = new MutableBoolean();
                double actualHeight = calculateNoiseColumn(weightMap1, variantAccessor, v -> {
                    if (v instanceof CarvingBiomeVariants)
                    {
                        hasCarvingBiomes.setTrue();
                        return ((CarvingBiomeVariants) v).getParent();
                    }
                    return v;
                }, chunkX + x, chunkZ + z, mutableBiome);

                double carvingCenter = 0, carvingHeight = 0, carvingWeight = 0;
                if (hasCarvingBiomes.booleanValue())
                {
                    // Calculate the carving weight map, only using local influences from carving biomes
                    ChunkArraySampler.fillSampledWeightMap(sampledBiomes1, carvingWeightMap1, x, z);

                    // Calculate the weighted carving height and center, using the modified weight map
                    for (Object2DoubleMap.Entry<Biome> entry : carvingWeightMap1.object2DoubleEntrySet())
                    {
                        final BiomeVariants variants = variantAccessor.apply(entry.getKey());
                        if (variants instanceof CarvingBiomeVariants)
                        {
                            final double weight = entry.getDoubleValue();
                            carvingWeight += weight;
                            carvingCenter += weight * biomeCarvingCenterNoise.get(variants).noise(chunkX + x, chunkZ + z);
                            carvingHeight += weight * biomeCarvingHeightNoise.get(variants).noise(chunkX + x, chunkZ + z);
                        }
                    }
                }

                // Adjust carving center towards sea level, to fill out the total weight (height defaults weight to zero so it does not need to change
                carvingCenter += SEA_LEVEL * (1 - carvingWeight);

                // Record the local (accurate) biome.
                localBiomes[x + 16 * z] = mutableBiome.getValue();

                // Set base terrain
                final int landHeight = (int) actualHeight;
                final int landOrSeaHeight = Math.max(landHeight, SEA_LEVEL);
                for (int y = 0; y <= landHeight; y++)
                {
                    pos.set(chunkX + x, y, chunkZ + z);
                    fastChunk.setBlockState(pos, settings.getDefaultBlock(), false);
                }

                for (int y = landHeight + 1; y <= SEA_LEVEL; y++)
                {
                    pos.set(chunkX + x, y, chunkZ + z);
                    fastChunk.setBlockState(pos, settings.getDefaultFluid(), false);
                }

                if (hasCarvingBiomes.booleanValue() && carvingHeight > 2f)
                {
                    // Apply carving
                    final int bottomHeight = (int) (carvingCenter - carvingHeight * 0.5f);
                    final int topHeight = (int) (carvingCenter + carvingHeight * 0.5f);
                    for (int y = bottomHeight; y <= topHeight; y++)
                    {
                        pos.set(chunkX + x, y, chunkZ + z);
                        int carvingMaskIndex = Helpers.getCarvingMaskIndex(pos);
                        if (y <= SEA_LEVEL)
                        {
                            fastChunk.setBlockState(pos, settings.getDefaultFluid(), false);
                            liquidCarvingMask.set(carvingMaskIndex, true);
                        }
                        else
                        {
                            fastChunk.setBlockState(pos, Blocks.CAVE_AIR.defaultBlockState(), false);
                            airCarvingMask.set(carvingMaskIndex, true);
                        }
                    }
                }

                ((HeightmapAccessor) fastChunk.getOrCreateHeightmapUnprimed(Heightmap.Type.OCEAN_FLOOR_WG)).call$setHeight(x, z, landHeight + 1);
                ((HeightmapAccessor) fastChunk.getOrCreateHeightmapUnprimed(Heightmap.Type.WORLD_SURFACE_WG)).call$setHeight(x, z, landOrSeaHeight + 1);
            }
        }

        // Build surface here as we need access to localBiomes for better placement accuracy
        final ChunkData chunkData = chunkDataProvider.get(chunkPos, ChunkData.Status.EMPTY);
        for (int x = 0; x < 16; ++x)
        {
            for (int z = 0; z < 16; ++z)
            {
                final int posX = chunkPos.getMinBlockX() + x;
                final int posZ = chunkPos.getMinBlockZ() + z;
                final int posY = fastChunk.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z) + 1;
                final double noise = surfaceDepthNoise.getSurfaceNoiseValue(posX * 0.0625, posZ * 0.0625, 0.0625, x * 0.0625) * 15;

                final Biome biome = localBiomes[x + 16 * z];
                IContextSurfaceBuilder.applyIfPresent(biome.getGenerationSettings().getSurfaceBuilder().get(), random, chunkData, fastChunk, biome, posX, posZ, posY, noise, seed, settings.getDefaultBlock(), settings.getDefaultFluid(), getSeaLevel());
            }
        }
    }

    @Override
    public int getSeaLevel()
    {
        return TFCChunkGenerator.SEA_LEVEL;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Type heightMapType)
    {
        return getSeaLevel();
    }

    @Override
    public IBlockReader getBaseColumn(int x, int z)
    {
        return EmptyBlockReader.INSTANCE; // todo: is this important?
    }

    private void makeBedrock(IChunk chunk, Random random)
    {
        final BlockPos.Mutable posAt = new BlockPos.Mutable();
        final BlockState bedrock = Blocks.BEDROCK.defaultBlockState();
        for (BlockPos pos : BlockPos.betweenClosed(chunk.getPos().getMinBlockX(), 0, chunk.getPos().getMinBlockZ(), chunk.getPos().getMinBlockX() + 15, 0, chunk.getPos().getMinBlockZ() + 15))
        {
            if (flatBedrock)
            {
                chunk.setBlockState(pos, bedrock, false);
            }
            else
            {
                int yMax = random.nextInt(5);
                for (int y = 0; y <= yMax; y++)
                {
                    chunk.setBlockState(posAt.set(pos.getX(), y, pos.getZ()), bedrock, false);
                }
            }
        }
    }

    private double calculateNoiseColumn(Object2DoubleMap<Biome> weightMap, Function<Biome, BiomeVariants> variantsAccessor, Function<BiomeVariants, BiomeVariants> variantsFilter, int x, int z, Mutable<Biome> mutableBiome)
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
}