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
import net.dries007.tfc.world.biome.TFCBiome;
import net.dries007.tfc.world.biome.TFCBiomeProvider;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.carver.WorleyCaveCarver;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.noise.INoise2D;

public class TFCOverworldChunkGenerator extends ChunkGenerator<TFCGenerationSettings>
{
    public static final BlockState BEDROCK = Blocks.BEDROCK.getDefaultState();

    // Parabolic field with total summed area equal to 1
    private static final double[] PARABOLIC_FIELD = Util.make(new double[9 * 9], array ->
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

    // Generators / Providers
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
        surfaceDepthNoise = new PerlinNoiseGenerator(seedGenerator, 3, 0);

        if (!(biomeProvider instanceof TFCBiomeProvider))
        {
            throw new IllegalArgumentException("biome provider must extend TFCBiomeProvider");
        }
        this.biomeProvider = (TFCBiomeProvider) biomeProvider; // Custom biome provider class
        this.worleyCaveCarver = new WorleyCaveCarver(seedGenerator); // Worley cave carver, separate from vanilla ones
        this.chunkDataProvider = new ChunkDataProvider(world, settings, seedGenerator); // Chunk data
        this.blockReplacer = new ChunkBlockReplacer(); // Replaces default world gen blocks with TFC variants, after surface generation
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
    public void func_225550_a_(BiomeManager biomeManager, IChunk chunkIn, GenerationStage.Carving stage)
    {
        if (stage == GenerationStage.Carving.AIR)
        {
            // First, run worley cave carver
            worleyCaveCarver.carve(chunkIn, chunkIn.getPos().x << 4, chunkIn.getPos().z << 4, chunkIn.getCarvingMask(stage));
        }
        // Fire other world carvers
        super.func_225550_a_(biomeManager, chunkIn, stage);
    }

    /**
     * See {@link net.minecraft.world.chunk.ChunkStatus#SURFACE}
     * Since we build surface in {@link TFCOverworldChunkGenerator#makeBase(IWorld, IChunk)}, we just have to make bedrock and replace surface with TFC blocks here
     */
    @Override
    public void func_225551_a_(WorldGenRegion worldGenRegion, IChunk chunk)
    {

        ChunkPos chunkPos = chunk.getPos();
        SharedSeedRandom random = new SharedSeedRandom();
        random.setBaseChunkSeed(chunkPos.x, chunkPos.z);
        BlockPos.Mutable pos = new BlockPos.Mutable();

        makeBedrock(chunk, random);

        ChunkData chunkData = chunkDataProvider.getOrCreate(chunkPos);
        float temperature = chunkData.getAvgTemp();
        float rainfall = chunkData.getRainfall();
        blockReplacer.replace(worldGenRegion, chunk, random, chunkData.getRockData(), rainfall, temperature);
    }

    @Override
    public int getGroundHeight()
    {
        return getSeaLevel();
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

        // The spread biomes (for calculating terrain smoothing), and the 16x16 biome grid (for height map creation)
        TFCBiome[] spreadBiomes = new TFCBiome[24 * 24];
        TFCBiome[] localBiomes = new TFCBiome[16 * 16];
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int i = 0; i < 24; i++)
        {
            for (int j = 0; j < 24; j++)
            {
                pos.setPos(chunkX - 4 + i, 0, chunkZ - 4 + j);
                spreadBiomes[i + 24 * j] = (TFCBiome) world.getBiome(pos);
            }
        }

        // Build the base height map, and also assign surface types (different from biomes because we need more control)
        double[] baseHeight = new double[16 * 16];
        Object2DoubleMap<TFCBiome> heightBiomeMap = new Object2DoubleOpenHashMap<>(4);
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                // At each position, apply the parabolic field to a 9x9 square area around the start position
                heightBiomeMap.clear();
                double totalHeight = 0, riverHeight = 0, shoreHeight = 0;
                double riverWeight = 0, shoreWeight = 0;
                for (int xOffset = 0; xOffset < 9; xOffset++)
                {
                    for (int zOffset = 0; zOffset < 9; zOffset++)
                    {
                        // Get the biome at the position and add it to the height biome map
                        TFCBiome biomeAt = spreadBiomes[(x + xOffset) + 24 * (z + zOffset)];
                        heightBiomeMap.mergeDouble(biomeAt, PARABOLIC_FIELD[xOffset + 9 * zOffset], Double::sum);
                    }
                }

                // The biome to reference when building the initial surface
                TFCBiome biomeAt = spreadBiomes[(x + 4) + 24 * (z + 4)];
                TFCBiome shoreBiomeAt = biomeAt, standardBiomeAt = biomeAt;
                double maxShoreWeight = 0, maxStandardBiomeWeight = 0;

                // calculate the total height based on the biome noise map, using a custom Noise2D for each biome
                for (Object2DoubleMap.Entry<TFCBiome> entry : heightBiomeMap.object2DoubleEntrySet())
                {
                    double weight = entry.getDoubleValue();
                    double height = weight * biomeNoiseMap.get(entry.getKey()).noise(chunkX + x, chunkZ + z);
                    totalHeight += height;
                    if (entry.getKey() == TFCBiomes.RIVER.get())
                    {
                        riverHeight += height;
                        riverWeight += weight;
                    }
                    else if (entry.getKey() == TFCBiomes.SHORE.get() || entry.getKey() == TFCBiomes.STONE_SHORE.get())
                    {
                        shoreHeight += height;
                        shoreWeight += weight;

                        if (maxShoreWeight < weight)
                        {
                            shoreBiomeAt = entry.getKey();
                            maxShoreWeight = weight;
                        }
                    }
                    else if (maxStandardBiomeWeight < weight)
                    {
                        standardBiomeAt = entry.getKey();
                        maxStandardBiomeWeight = weight;
                    }
                }

                // Create river valleys - carve cliffs around river biomes, and smooth out the edges
                double actualHeight = totalHeight;
                if (riverWeight > 0.6)
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
                    biomeAt = TFCBiomes.RIVER.get(); // Use river surface for the bottom of the river + small shore beneath cliffs
                }
                else if (riverWeight > 0)
                {
                    double adjustedRiverWeight = 0.6 * riverWeight;
                    actualHeight = (totalHeight - riverHeight) * ((1 - adjustedRiverWeight) / (1 - riverWeight)) + riverHeight * (adjustedRiverWeight / riverWeight);

                    if (biomeAt == TFCBiomes.RIVER.get())
                    {
                        biomeAt = standardBiomeAt;
                    }
                }

                // Flatten shores, and create cliffs on the edges
                if (shoreWeight > 0.4)
                {
                    if (actualHeight > getSeaLevel() + 1)
                    {
                        actualHeight = getSeaLevel() + 1;
                    }
                    biomeAt = shoreBiomeAt;
                }
                else if (shoreWeight > 0)
                {
                    double adjustedShoreWeight = 0.4 * shoreWeight;
                    actualHeight = (actualHeight - shoreHeight) * ((1 - adjustedShoreWeight) / (1 - shoreWeight)) + shoreHeight * (adjustedShoreWeight / shoreWeight);

                    if (biomeAt == shoreBiomeAt)
                    {
                        biomeAt = standardBiomeAt;
                    }
                }

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
        return TFCConfig.COMMON.seaLevel.get();
    }

    @Override
    public int func_222529_a(int p_222529_1_, int p_222529_2_, Heightmap.Type p_222529_3_)
    {
        return 0;
    }

    /* func_222555_a in NoiseChunkGenerator */
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
