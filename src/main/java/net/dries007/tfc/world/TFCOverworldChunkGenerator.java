/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.ChunkArraySampler;
import net.dries007.tfc.world.biome.*;
import net.dries007.tfc.world.carver.WorleyCaveCarver;
import net.dries007.tfc.world.chunk.FastChunkPrimer;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.chunkdata.IChunkDataProvidingChunkGenerator;
import net.dries007.tfc.world.noise.INoise2D;

public class TFCOverworldChunkGenerator extends ChunkGenerator<TFCGenerationSettings> implements IChunkDataProvidingChunkGenerator
{
    public static final BlockState BEDROCK = Blocks.BEDROCK.defaultBlockState();

    // Noise
    private final Map<TFCBiome, INoise2D> biomeNoiseMap;
    private final INoiseGenerator surfaceDepthNoise;

    private final TFCBiomeProvider shadowBiomeProvider;
    private final WorleyCaveCarver worleyCaveCarver;
    private final ChunkDataProvider chunkDataProvider;
    private final ChunkBlockReplacer blockReplacer;

    private final BlockState defaultBlock, defaultFluid;

    public TFCOverworldChunkGenerator(IWorld world, BiomeProvider biomeProvider, TFCGenerationSettings settings)
    {
        super(world, biomeProvider, settings);

        SharedSeedRandom seedGenerator = new SharedSeedRandom(world.getSeed());

        // Noise
        this.biomeNoiseMap = new HashMap<>();
        final long biomeNoiseSeed = seedGenerator.nextLong();
        TFCBiomes.getBiomes().forEach(biome -> biomeNoiseMap.put(biome, biome.createNoiseLayer(biomeNoiseSeed)));
        surfaceDepthNoise = new PerlinNoiseGenerator(seedGenerator, 3, 0); // From vanilla

        if (!(biomeProvider instanceof TFCBiomeProvider))
        {
            throw new IllegalArgumentException("biome provider must extend TFCBiomeProvider");
        }
        // Generators / Providers
        this.shadowBiomeProvider = (TFCBiomeProvider) biomeProvider; // Custom biome provider class
        this.worleyCaveCarver = new WorleyCaveCarver(seedGenerator); // Worley cave carver, separate from vanilla ones
        this.chunkDataProvider = new ChunkDataProvider(world, settings, seedGenerator); // Chunk data
        this.blockReplacer = new ChunkBlockReplacer(world.getSeed()); // Replaces default world gen blocks with TFC variants, after surface generation
        this.shadowBiomeProvider.setChunkDataProvider(chunkDataProvider); // Allow biomes to use the chunk data temperature / rainfall variation

        this.defaultBlock = settings.getDefaultBlock();
        this.defaultFluid = settings.getDefaultFluid();
    }

    @Override
    public ChunkDataProvider getChunkDataProvider()
    {
        return chunkDataProvider;
    }

    public ChunkBlockReplacer getBlockReplacer()
    {
        return blockReplacer;
    }

    @Override
    public void createBiomes(IChunk chunkIn)
    {
        // Saves 98% of vanilla biome generation calls
        ((ChunkPrimer) chunkIn).setBiomes(new ColumnBiomeContainer(chunkIn.getPos(), shadowBiomeProvider));
    }

    @Override
    public void applyCarvers(BiomeManager biomeManager, IChunk chunkIn, GenerationStage.Carving stage)
    {
        ChunkPos chunkPos = chunkIn.getPos();
        if (stage == GenerationStage.Carving.AIR)
        {
            // First, run worley cave carver
            // todo: this should use a vanilla world carver for ease of use (despite the worse efficiency)
            worleyCaveCarver.carve(chunkIn, chunkPos.getMinBlockX(), chunkPos.getMinBlockZ(), chunkIn.getCarvingMask(stage));
        }

        // Fire other world carvers
        super.applyCarvers(biomeManager, chunkIn, stage);
    }

    /**
     * Since we build surface in {@link TFCOverworldChunkGenerator#makeBase(IWorld, IChunk)}, we just have to make bedrock and replace surface with TFC blocks here.
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
        return getSeaLevel() + 1;
    }

    /**
     * This runs after biome generation. In order to do accurate surface placement, we don't use the already generated biome container, as the biome magnifier really sucks for definition on cliffs
     */
    @Override
    public void fillFromNoise(IWorld world, IChunk chunk)
    {
        // Initialization
        final IChunk fastChunk = FastChunkPrimer.deslowificate(chunk);
        final ChunkPos chunkPos = chunk.getPos();
        final SharedSeedRandom random = new SharedSeedRandom();
        final int chunkX = chunkPos.getMinBlockX(), chunkZ = chunkPos.getMinBlockZ();
        final BlockPos.Mutable pos = new BlockPos.Mutable();

        random.setBaseChunkSeed(chunkPos.x, chunkPos.z);

        // The accurate version of biomes which we use for surface building
        // These are calculated during height generation in order to generate cliffs with harsh borders between biomes
        final TFCBiome[] localBiomes = new TFCBiome[16 * 16];
        final double[] baseHeight = new double[16 * 16];

        // The biome weights at different distance intervals
        final Object2DoubleMap<TFCBiome> weightMap16 = new Object2DoubleOpenHashMap<>(4), weightMap4 = new Object2DoubleOpenHashMap<>(4), weightMap1 = new Object2DoubleOpenHashMap<>();

        // Faster than vanilla (only does 2d interpolation) and uses the already generated biomes by the chunk where possible
        final ChunkArraySampler.CoordinateAccessor<TFCBiome> biomeAccessor = (x, z) -> (TFCBiome) SmoothColumnBiomeMagnifier.SMOOTH.getBiome(world.getSeed(), chunkX + x, 0, chunkZ + z, world);

        final TFCBiome[] sampledBiomes16 = ChunkArraySampler.fillSampledArray(new TFCBiome[10 * 10], biomeAccessor, 4);
        final TFCBiome[] sampledBiomes4 = ChunkArraySampler.fillSampledArray(new TFCBiome[13 * 13], biomeAccessor, 2);
        final TFCBiome[] sampledBiomes1 = ChunkArraySampler.fillSampledArray(new TFCBiome[24 * 24], biomeAccessor);

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
                ChunkArraySampler.reduceGroupedWeightMap(weightMap4, weightMap16, ITFCBiome::getLargeGroup, ITFCBiome.LargeGroup.SIZE);
                ChunkArraySampler.reduceGroupedWeightMap(weightMap1, weightMap4, ITFCBiome::getMediumGroup, ITFCBiome.SmallGroup.SIZE);

                // Based on total weight of all biomes included, calculate heights of a couple important groups
                // Rivers and shores are seperated in order to force cliff generation
                double totalHeight = 0, riverHeight = 0, shoreHeight = 0;
                double riverWeight = 0, shoreWeight = 0;
                TFCBiome biomeAt = null, normalBiomeAt = null, riverBiomeAt = null, shoreBiomeAt = null;
                double maxNormalWeight = 0, maxRiverWeight = 0, maxShoreWeight = 0;
                for (Object2DoubleMap.Entry<TFCBiome> entry : weightMap1.object2DoubleEntrySet())
                {
                    double weight = entry.getDoubleValue();
                    double height = weight * biomeNoiseMap.get(entry.getKey()).noise(chunkX + x, chunkZ + z);
                    totalHeight += height;
                    if (entry.getKey().getVariants() == TFCBiomes.RIVER)
                    {
                        riverHeight += height;
                        riverWeight += weight;
                        if (maxRiverWeight < weight)
                        {
                            riverBiomeAt = entry.getKey();
                            maxRiverWeight = weight;
                        }
                    }
                    else if (entry.getKey().getVariants() == TFCBiomes.SHORE)
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

                Objects.requireNonNull(biomeAt, "Biome should not be null!");
                baseHeight[x + 16 * z] = actualHeight;
                localBiomes[x + 16 * z] = biomeAt;
            }
        }

        // Build terrain using height map, using just default block and fluid for now
        int seaLevel = getSeaLevel();
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                int landHeight = (int) baseHeight[x + 16 * z];
                int landOrSeaHeight = Math.max(landHeight, seaLevel);
                for (int y = 0; y <= landHeight; y++)
                {
                    pos.set(chunkX + x, y, chunkZ + z);
                    fastChunk.setBlockState(pos, defaultBlock, false);
                }

                for (int y = landHeight + 1; y <= seaLevel; y++)
                {
                    pos.set(chunkX + x, y, chunkZ + z);
                    fastChunk.setBlockState(pos, defaultFluid, false);
                }

                fastChunk.getOrCreateHeightmapUnprimed(Heightmap.Type.OCEAN_FLOOR_WG).setHeight(x, z, landHeight + 1);
                fastChunk.getOrCreateHeightmapUnprimed(Heightmap.Type.WORLD_SURFACE_WG).setHeight(x, z, landOrSeaHeight + 1);
            }
        }

        // Build vanilla surface
        // We do this here because we want to have more accuracy with biome / surface placement
        for (int x = 0; x < 16; ++x)
        {
            for (int z = 0; z < 16; ++z)
            {
                int posX = chunkPos.getMinBlockX() + x;
                int posZ = chunkPos.getMinBlockZ() + z;
                int posY = fastChunk.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z) + 1;
                double noise = surfaceDepthNoise.getSurfaceNoiseValue(posX * 0.0625, posZ * 0.0625, 0.0625, x * 0.0625) * 15;
                localBiomes[x + 16 * z].buildSurfaceAt(random, fastChunk, posX, posZ, posY, noise, getSettings().getDefaultBlock(), getSettings().getDefaultFluid(), getSeaLevel(), world.getSeed());
            }
        }
    }

    @Override
    public int getSeaLevel()
    {
        return TFCConfig.COMMON.seaLevel.get();
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Type heightMapType)
    {
        return getSeaLevel();
    }

    private void makeBedrock(IChunk chunk, Random random)
    {
        boolean flatBedrock = getSettings().isFlatBedrock();
        BlockPos.Mutable posAt = new BlockPos.Mutable();
        for (BlockPos pos : BlockPos.betweenClosed(chunk.getPos().getMinBlockX(), 0, chunk.getPos().getMinBlockZ(), chunk.getPos().getMinBlockX() + 15, 0, chunk.getPos().getMinBlockZ() + 15))
        {
            if (flatBedrock)
            {
                chunk.setBlockState(pos, BEDROCK, false);
            }
            else
            {
                int yMax = random.nextInt(5);
                for (int y = 0; y <= yMax; y++)
                {
                    chunk.setBlockState(posAt.set(pos.getX(), y, pos.getZ()), BEDROCK, false);
                }
            }
        }
    }
}