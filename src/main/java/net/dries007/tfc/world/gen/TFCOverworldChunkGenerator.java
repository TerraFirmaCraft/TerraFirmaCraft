/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.world.biome.TFCBiome;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.biome.provider.TFCBiomeProvider;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.gen.carver.WorleyCaveCarver;
import net.dries007.tfc.world.gen.rock.RockData;
import net.dries007.tfc.world.noise.INoise2D;

@ParametersAreNonnullByDefault
public class TFCOverworldChunkGenerator extends ChunkGenerator<TFCGenerationSettings>
{
    public static final int SEA_LEVEL = 96;

    public static final BlockState STONE = Blocks.STONE.getDefaultState();
    public static final BlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
    public static final BlockState WATER = Blocks.WATER.getDefaultState();
    public static final BlockState AIR = Blocks.AIR.getDefaultState();

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

    // Generators / Providers
    private final TFCBiomeProvider biomeProvider;
    private final WorleyCaveCarver worleyCaveCarver;
    private final ChunkDataProvider chunkDataProvider;

    public TFCOverworldChunkGenerator(IWorld world, BiomeProvider biomeProvider, TFCGenerationSettings settings)
    {
        super(world, biomeProvider, settings);

        Random seedGenerator = new Random(world.getSeed());

        this.biomeNoiseMap = new HashMap<>();
        final long biomeNoiseSeed = seedGenerator.nextLong();
        TFCBiomes.getBiomes().forEach(biome -> biomeNoiseMap.put(biome, biome.createNoiseLayer(biomeNoiseSeed)));

        if (!(biomeProvider instanceof TFCBiomeProvider))
        {
            throw new IllegalArgumentException("biome provider must extend TFCBiomeProvider");
        }
        this.biomeProvider = (TFCBiomeProvider) biomeProvider;

        // Custom cave carver
        this.worleyCaveCarver = new WorleyCaveCarver(seedGenerator);

        // Rock Layer Provider
        this.chunkDataProvider = new ChunkDataProvider(world, settings, seedGenerator);
    }

    @Nonnull
    public ChunkDataProvider getChunkDataProvider()
    {
        return chunkDataProvider;
    }

    @Override
    public void func_225550_a_(BiomeManager biomeManager, IChunk chunkIn, GenerationStage.Carving stage)
    {
        if (stage == GenerationStage.Carving.AIR)
        {
            // First, run worley cave carver
            worleyCaveCarver.carve(chunkIn, chunkIn.getPos().x << 4, chunkIn.getPos().z << 4);
        }

        // Fire other world carvers
        super.func_225550_a_(biomeManager, chunkIn, stage);
    }

    @Override
    public void func_225551_a_(WorldGenRegion worldGenRegion, IChunk chunk)
    {
        ChunkPos chunkPos = chunk.getPos();
        SharedSeedRandom random = new SharedSeedRandom();
        random.setBaseChunkSeed(chunkPos.x, chunkPos.z);

        BiomeContainer biomes = chunk.getBiomes();
        if (biomes == null)
        {
            throw new IllegalStateException("Biomes are missing from chunk during surface generation");
        }
        RockData rockData = chunkDataProvider.getOrCreate(chunkPos).getRockData();

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                // todo: make dependent on temp / rainfall layer
                float temperature = 5;
                float rainfall = 200;
                float noise = 0; // todo: use for noise surface builder
                int topYLevel = chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, x, z) + 1;
                ((TFCBiome) biomes.getNoiseBiome(x, 0, z)).getTFCSurfaceBuilder().buildSurface(random, chunk, rockData, chunkPos.getXStart() + x, chunkPos.getZStart() + z, topYLevel + 1, temperature, rainfall, noise);
            }
        }

        makeBedrock(chunk, random);
    }

    @Override
    public int getGroundHeight()
    {
        return SEA_LEVEL;
    }

    /**
     * This runs after biome generation. We skip biome generation there as we do it in tandem with noise generation
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
        TFCBiome[] spreadBiomes = biomeProvider.getBiomes(chunkX - 4, chunkZ - 4, 24, 24);

        // Build the base height map, and also assign surface types (different from biomes because we need more control)
        double[] baseHeight = new double[16 * 16];
        double[] baseRockHeight = new double[16 * 16];
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

                        // Sum the rock layer height
                        baseRockHeight[z + 16 * x] += PARABOLIC_FIELD[zOffset + 9 * xOffset] * biomeAt.getDefaultRockHeight();
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
                    if (entry.getKey() == TFCBiomes.RIVER)
                    {
                        riverHeight += height;
                        riverWeight += weight;
                    }
                    else if (entry.getKey() == TFCBiomes.SHORE || entry.getKey() == TFCBiomes.STONE_SHORE)
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
                    biomeAt = TFCBiomes.RIVER; // Use river surface for the bottom of the river + small shore beneath cliffs
                }
                else if (riverWeight > 0)
                {
                    double adjustedRiverWeight = 0.6 * riverWeight;
                    actualHeight = (totalHeight - riverHeight) * ((1 - adjustedRiverWeight) / (1 - riverWeight)) + riverHeight * (adjustedRiverWeight / riverWeight);

                    if (biomeAt == TFCBiomes.RIVER)
                    {
                        biomeAt = standardBiomeAt;
                    }
                }

                // Flatten shores, and create cliffs on the edges
                if (shoreWeight > 0.4)
                {
                    if (actualHeight > SEA_LEVEL + 1)
                    {
                        actualHeight = SEA_LEVEL + 1;
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
            }
        }

        //  todo: light stuff and height maps
        Heightmap oceanFloorHeightMap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap worldSurfaceHeightMap = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);

        // Build Rough Terrain
        RockData rockData = chunkDataProvider.getOrCreate(chunkPos).getRockData();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                double totalHeight = baseHeight[x + 16 * z];
                for (int y = 0; y <= (int) totalHeight; y++)
                {
                    // todo: can this just be x, y, z since it all gets cut off anyway?
                    pos.setPos(chunkX + x, y, chunkZ + z);
                    Rock rock;
                    if (y < baseRockHeight[x + 16 * z])
                    {
                        rock = rockData.getBottomRock(x, z);
                    }
                    else
                    {
                        rock = rockData.getTopRock(x, z);
                    }
                    BlockState rockState = rock.getBlock(Rock.BlockType.RAW).getDefaultState();
                    chunk.setBlockState(pos, rockState, false);
                }

                for (int y = (int) totalHeight + 1; y <= SEA_LEVEL; y++)
                {
                    pos.setPos(chunkX + x, y, chunkZ + z);
                    chunk.setBlockState(pos, settings.getDefaultFluid(), false);
                }
            }
        }
    }

    @Override
    public int getSeaLevel()
    {
        return SEA_LEVEL;
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
                for (int y = 4; y >= 0; y--)
                {
                    if (y <= yMax)
                    {
                        chunk.setBlockState(posAt.setPos(pos.getX(), y, pos.getZ()), BEDROCK, false);
                    }
                }
            }
        }
    }
}
