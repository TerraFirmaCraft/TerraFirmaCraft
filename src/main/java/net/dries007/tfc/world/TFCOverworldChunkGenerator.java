/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.world.biome.*;
import net.dries007.tfc.world.carver.WorleyCaveCarver;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.noise.INoise2D;

public class TFCOverworldChunkGenerator extends ChunkGenerator<TFCGenerationSettings>
{
    public static final BlockState BEDROCK = Blocks.BEDROCK.getDefaultState();

    // Parabolic field with total summed area equal to 1
    public static final double[] PARABOLIC_9x9 = Util.make(new double[9 * 9], array ->
    {
        for (int x = 0; x < 9; x++)
        {
            for (int z = 0; z < 9; z++)
            {
                array[x + 9 * z] = 0.0211640211641D * (1 - 0.03125D * ((z - 4) * (z - 4) + (x - 4) * (x - 4)));
            }
        }
    });

    // Noise
    private final Map<TFCBiome, INoise2D> biomeNoiseMap;
    private final INoiseGenerator surfaceDepthNoise;

    private final TFCBiomeProvider biomeProvider;
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
        this.biomeProvider = (TFCBiomeProvider) biomeProvider; // Custom biome provider class
        this.worleyCaveCarver = new WorleyCaveCarver(seedGenerator); // Worley cave carver, separate from vanilla ones
        this.chunkDataProvider = new ChunkDataProvider(world, settings, seedGenerator); // Chunk data
        this.blockReplacer = new ChunkBlockReplacer(); // Replaces default world gen blocks with TFC variants, after surface generation
        this.biomeProvider.setChunkDataProvider(chunkDataProvider); // Allow biomes to use the chunk data temperature / rainfall variation
    }

    public ChunkDataProvider getChunkDataProvider()
    {
        return chunkDataProvider;
    }

    public ChunkBlockReplacer getBlockReplacer()
    {
        return blockReplacer;
    }

    @Override
    public void generateCarvers(BiomeManager biomeManager, IChunk chunkIn, GenerationStage.Carving stage)
    {
        ChunkPos chunkPos = chunkIn.getPos();
        if (stage == GenerationStage.Carving.AIR)
        {
            // First, run worley cave carver
            worleyCaveCarver.carve(chunkIn, chunkIn.getPos().x << 4, chunkIn.getPos().z << 4, chunkIn.getCarvingMask(stage));
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

        ChunkData chunkData = ChunkData.get(worldGenRegion, chunkPos, ChunkData.Status.ROCKS, false);
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
        Object2DoubleMap<TFCBiome> biomeWeights = new Object2DoubleOpenHashMap<>(4);

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                pos.setPos(chunkX + x, 0, chunkZ + z);
                TFCBiome biomeAt = (TFCBiome) SmoothColumnBiomeMagnifier.SMOOTH.getBiome(world.getSeed(), chunkX + x, 0, chunkZ + z, world);

                // Edge type - sample an area around the target location dependent on the edge type
                BiomeEdgeType edgeType = biomeAt.getVariants().getEdgeType();
                biomeWeights.clear();
                edgeType.apply(biomeWeights, biomeProvider, biomeAt, world.getSeed(), chunkX + x, chunkZ + z);

                // Weight type - apply weight transformations here
                biomeAt.getVariants().getWeightType().apply(biomeWeights);

                // Simple average across the final weights
                double totalHeight = 0;
                double maxWeight = 0;
                TFCBiome maxWeightBiome = biomeAt;
                for (Object2DoubleMap.Entry<TFCBiome> entry : biomeWeights.object2DoubleEntrySet())
                {
                    double height = biomeNoiseMap.get(entry.getKey()).noise(chunkX + x, chunkZ + z);
                    double weight = entry.getDoubleValue();
                    totalHeight += weight * height;
                    if (weight > maxWeight)
                    {
                        maxWeight = weight;
                        maxWeightBiome = entry.getKey();
                    }
                }
                baseHeight[x + 16 * z] = totalHeight;
                localBiomes[x + 16 * z] = maxWeightBiome;
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
