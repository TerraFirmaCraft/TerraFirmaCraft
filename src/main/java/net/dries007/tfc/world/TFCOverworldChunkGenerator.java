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
import net.dries007.tfc.api.world.ITFCChunkGenerator;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.ChunkArraySampler;
import net.dries007.tfc.world.biome.*;
import net.dries007.tfc.world.carver.WorleyCaveCarver;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.noise.INoise2D;

public class TFCOverworldChunkGenerator extends ChunkGenerator<TFCGenerationSettings> implements ITFCChunkGenerator
{
    public static final BlockState BEDROCK = Blocks.BEDROCK.getDefaultState();

    // Noise
    private final Map<TFCBiome, INoise2D> biomeNoiseMap;
    private final INoiseGenerator surfaceDepthNoise;

    private final TFCBiomeProvider shadowBiomeProvider;
    private final WorleyCaveCarver worleyCaveCarver;
    private final ChunkDataProvider chunkDataProvider;
    private final ChunkBlockReplacer blockReplacer;

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
        this.blockReplacer = new ChunkBlockReplacer(); // Replaces default world gen blocks with TFC variants, after surface generation
        this.shadowBiomeProvider.setChunkDataProvider(chunkDataProvider); // Allow biomes to use the chunk data temperature / rainfall variation
    }

    @Override
    public ChunkDataProvider getChunkDataProvider()
    {
        return chunkDataProvider;
    }

    @Override
    public ChunkBlockReplacer getBlockReplacer()
    {
        return blockReplacer;
    }

    @Override
    public void generateBiomes(IChunk chunkIn)
    {
        // Saves 98% of vanilla biome generation calls
        ((ChunkPrimer) chunkIn).setBiomes(new ColumnBiomeContainer(chunkIn.getPos(), shadowBiomeProvider));
    }

    @Override
    public void generateCarvers(BiomeManager biomeManager, IChunk chunkIn, GenerationStage.Carving stage)
    {
        ChunkPos chunkPos = chunkIn.getPos();
        if (stage == GenerationStage.Carving.AIR)
        {
            // First, run worley cave carver
            // todo: this should use a vanilla world carver for ease of use (despite the worse efficiency)
            worleyCaveCarver.carve(chunkIn, chunkPos.getXStart(), chunkPos.getZStart(), chunkIn.getCarvingMask(stage));
        }

        // Fire other world carvers
        super.generateCarvers(biomeManager, chunkIn, stage);
    }

    /**
     * Since we build surface in {@link TFCOverworldChunkGenerator#makeBase(IWorld, IChunk)}, we just have to make bedrock and replace surface with TFC blocks here.
     */
    @Override
    public void generateSurface(WorldGenRegion worldGenRegion, IChunk chunk)
    {
        ChunkPos chunkPos = chunk.getPos();
        SharedSeedRandom random = new SharedSeedRandom();
        random.setBaseChunkSeed(chunkPos.x, chunkPos.z);

        makeBedrock(chunk, random);

        ChunkData chunkData = chunkDataProvider.get(chunkPos, ChunkData.Status.ROCKS);
        blockReplacer.replace(worldGenRegion, chunk, random, chunkData);
    }

    @Override
    public int getGroundHeight()
    {
        return getSeaLevel() + 1;
    }

    /**
     * This runs after biome generation. In order to do accurate surface placement, we don't use the already generated biome container, as the biome magnifier really sucks for definition on cliffs
     */
    @Override
    public void makeBase(IWorld world, IChunk chunk)
    {
        // Initialization
        ChunkPos chunkPos = chunk.getPos();
        SharedSeedRandom random = new SharedSeedRandom();
        int chunkX = chunkPos.getXStart(), chunkZ = chunkPos.getZStart();
        random.setBaseChunkSeed(chunkPos.x, chunkPos.z);
        BlockPos.Mutable pos = new BlockPos.Mutable();

        TFCBiome[] localBiomes = new TFCBiome[16 * 16];
        double[] baseHeight = new double[16 * 16];
        Object2DoubleMap<TFCBiome> weightMap16 = new Object2DoubleOpenHashMap<>(4), weightMap4 = new Object2DoubleOpenHashMap<>(4), weightMap1 = new Object2DoubleOpenHashMap<>();

        ChunkArraySampler.CoordinateAccessor<TFCBiome> biomeAccessor = (x, z) -> (TFCBiome) SmoothColumnBiomeMagnifier.SMOOTH.getBiome(world.getSeed(), chunkX + x, 0, chunkZ + z, world);

        TFCBiome[] sampledBiomes16 = ChunkArraySampler.fillSampledArray(new TFCBiome[10 * 10], biomeAccessor, 4);
        TFCBiome[] sampledBiomes4 = ChunkArraySampler.fillSampledArray(new TFCBiome[13 * 13], biomeAccessor, 2);
        TFCBiome[] sampledBiomes1 = ChunkArraySampler.fillSampledArray(new TFCBiome[24 * 24], biomeAccessor);

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                pos.setPos(chunkX + x, 0, chunkZ + z);

                ChunkArraySampler.fillSampledWeightMap(sampledBiomes16, weightMap16, 4, x, z);
                ChunkArraySampler.fillSampledWeightMap(sampledBiomes4, weightMap4, 2, x, z);
                ChunkArraySampler.fillSampledWeightMap(sampledBiomes1, weightMap1, x, z);

                ChunkArraySampler.reduceGroupedWeightMap(weightMap4, weightMap16, ITFCBiome::getLargeGroup, ITFCBiome.LargeGroup.SIZE);
                ChunkArraySampler.reduceGroupedWeightMap(weightMap1, weightMap4, ITFCBiome::getMediumGroup, ITFCBiome.SmallGroup.SIZE);

                // Simple average across the final weights
                double totalHeight = 0, riverHeight = 0;
                double riverWeight = 0;
                TFCBiome biomeAt = null, normalBiomeAt = null, riverBiomeAt = null;
                double maxNormalWeight = 0, maxRiverWeight = 0;
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

                Objects.requireNonNull(biomeAt, "Biome should not be null!");
                baseHeight[x + 16 * z] = actualHeight;
                localBiomes[x + 16 * z] = biomeAt;
            }
        }

        // Build Rough Terrain
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                double totalHeight = baseHeight[x + 16 * z];
                for (int y = 0; y <= (int) totalHeight; y++)
                {
                    pos.setPos(chunkX + x, y, chunkZ + z);
                    chunk.setBlockState(pos, settings.getDefaultBlock(), false);
                }

                for (int y = (int) totalHeight + 1; y < getSeaLevel(); y++)
                {
                    pos.setPos(chunkX + x, y, chunkZ + z);
                    chunk.setBlockState(pos, settings.getDefaultFluid(), false);
                }
            }
        }

        // Build vanilla surface
        // We do this here because we want to have more accuracy with biome / surface placement
        for (int x = 0; x < 16; ++x)
        {
            for (int z = 0; z < 16; ++z)
            {
                int posX = chunkPos.getXStart() + x;
                int posZ = chunkPos.getZStart() + z;
                int posY = chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, x, z) + 1;
                double noise = surfaceDepthNoise.noiseAt(posX * 0.0625, posZ * 0.0625, 0.0625, x * 0.0625) * 15;
                localBiomes[x + 16 * z].buildSurface(random, chunk, posX, posZ, posY, noise, getSettings().getDefaultBlock(), getSettings().getDefaultFluid(), getSeaLevel(), world.getSeed());
            }
        }
    }

    @Override
    public int getSeaLevel()
    {
        return TFCConfig.COMMON.seaLevel.get() + 1;
    }

    /* getHeight */
    @Override
    public int func_222529_a(int x, int z, Heightmap.Type heightMapType)
    {
        return getSeaLevel();
    }

    private void makeBedrock(IChunk chunk, Random random)
    {
        boolean flatBedrock = getSettings().isFlatBedrock();
        BlockPos.Mutable posAt = new BlockPos.Mutable();
        for (BlockPos pos : BlockPos.getAllInBoxMutable(chunk.getPos().getXStart(), 0, chunk.getPos().getZStart(), chunk.getPos().getXStart() + 15, 0, chunk.getPos().getZStart() + 15))
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
                    chunk.setBlockState(posAt.setPos(pos.getX(), y, pos.getZ()), BEDROCK, false);
                }
            }
        }
    }
}
