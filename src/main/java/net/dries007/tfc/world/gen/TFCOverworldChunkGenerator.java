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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.dries007.tfc.world.biome.TFCBiome;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.biome.provider.TFCBiomeProvider;
import net.dries007.tfc.world.gen.carver.WorleyCaveCarver;
import net.dries007.tfc.world.gen.rock.provider.RockProvider;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;
import net.dries007.tfc.world.noise.SinNoise;

@ParametersAreNonnullByDefault
public class TFCOverworldChunkGenerator extends ChunkGenerator<TFCGenerationSettings>
{
    public static final int SEA_LEVEL = 96;

    public static final BlockState STONE = Blocks.STONE.getDefaultState();
    public static final BlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
    public static final BlockState WATER = Blocks.WATER.getDefaultState();
    public static final BlockState AIR = Blocks.AIR.getDefaultState();

    private static final double[] PARABOLIC_FIELD = new double[9 * 9];

    static
    {
        for (int x = 0; x < 9; x++)
        {
            for (int z = 0; z < 9; z++)
            {
                PARABOLIC_FIELD[x + 9 * z] = 0.0211640211641D * (1 - 0.03125D * ((z - 4) * (z - 4) + (x - 4) * (x - 4)));
            }
        }
    }

    private final INoise2D temperatureNoise, rainfallNoise, surfaceNoise;
    private final Map<TFCBiome, INoise2D> biomeNoiseMap;
    private final TFCBiomeProvider biomeProvider;

    private final WorleyCaveCarver worleyCaveCarver;

    private final RockProvider rockProvider;

    public TFCOverworldChunkGenerator(IWorld world, BiomeProvider biomeProvider, TFCGenerationSettings settings)
    {
        super(world, biomeProvider, settings);

        Random seedGenerator = new Random(world.getSeed());

        // Initial Climate Layers
        this.temperatureNoise = new SinNoise(20, 0, (float) Math.PI * 0.000025f, 0).extendX().add(new SimplexNoise2D(seedGenerator.nextLong()).octaves(4).spread(0.0008f).scaled(-5, 5));
        this.rainfallNoise = new SinNoise(250, 250, (float) Math.PI * 0.000025f, 0).extendY().add(new SimplexNoise2D(seedGenerator.nextLong()).octaves(4).spread(0.0008f).scaled(-25, 25));

        this.biomeNoiseMap = new HashMap<>();
        final long biomeNoiseSeed = seedGenerator.nextLong();
        TFCBiomes.getBiomes().forEach(biome -> biomeNoiseMap.put(biome, biome.createNoiseLayer(biomeNoiseSeed)));

        this.surfaceNoise = new SimplexNoise2D(seedGenerator.nextLong()).octaves(4).spread(0.07f);

        if (!(biomeProvider instanceof TFCBiomeProvider))
        {
            throw new IllegalArgumentException("biome provider must extend TFCBiomeProvider");
        }
        this.biomeProvider = (TFCBiomeProvider) biomeProvider;

        // Custom cave carver
        this.worleyCaveCarver = new WorleyCaveCarver(seedGenerator);

        // Rock Layer Provider
        this.rockProvider = new RockProvider(world, settings);
    }

    @Nonnull
    public RockProvider getRockProvider()
    {
        return rockProvider;
    }

    @Override
    public void carve(IChunk chunkIn, GenerationStage.Carving stage)
    {
        if (stage == GenerationStage.Carving.AIR)
        {
            // First, run worley cave carver
            worleyCaveCarver.carve(chunkIn, chunkIn.getPos().x << 4, chunkIn.getPos().z << 4);
        }

        //super.carve(chunkIn, stage);
    }

    @Override
    public void generateSurface(IChunk chunk)
    {
        ChunkPos chunkPos = chunk.getPos();
        SharedSeedRandom random = new SharedSeedRandom();
        random.setBaseChunkSeed(chunkPos.x, chunkPos.z);

        Biome[] biomes = chunk.getBiomes();

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                // surface builder
                biomes[z * 16 + x].buildSurface(random, chunk, chunkPos.getXStart() + x, chunkPos.getZStart() + z, chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, x, z) + 1, 0, getSettings().getDefaultBlock(), getSettings().getDefaultFluid(), getSeaLevel(), world.getSeed());

                // rock replacement

            }
        }

        makeBedrock(chunk, random);
    }

    @Override
    public int getGroundHeight()
    {
        return 0;
    }

    @Override
    public void makeBase(IWorld world, IChunk chunk)
    {
        // Initialization
        ChunkPos chunkPos = chunk.getPos();
        SharedSeedRandom random = new SharedSeedRandom();
        int chunkX = chunkPos.getXStart(), chunkZ = chunkPos.getZStart();
        random.setBaseChunkSeed(chunkPos.x, chunkPos.z);

        // The spread biomes (for calculating terrain smoothing), and the 16x16 biome grid (for chunk biomes)
        TFCBiome[] spreadBiomes = biomeProvider.getBiomes(chunkX - 4, chunkZ - 4, 24, 24, false);
        TFCBiome[] chunkBiomes = new TFCBiome[16 * 16];

        double[] baseHeight = new double[16 * 16];

        Object2DoubleMap<TFCBiome> heightBiomeMap = new Object2DoubleOpenHashMap<>(4);
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                // At each position, apply the parabolic field to a 9x9 square area around the start position
                heightBiomeMap.clear();
                double totalHeight = 0;
                for (int xOffset = 0; xOffset < 9; xOffset++)
                {
                    for (int zOffset = 0; zOffset < 9; zOffset++)
                    {
                        // Get the biome at the position and add it to the height biome map
                        TFCBiome biomeAt = spreadBiomes[(x + xOffset) + 24 * (z + zOffset)];
                        heightBiomeMap.mergeDouble(biomeAt, PARABOLIC_FIELD[xOffset + 9 * zOffset], (a, b) -> a + b);
                    }
                }

                // Populate the chunk biomes, instead of asking the biome provider to generate them again
                chunkBiomes[x + 16 * z] = spreadBiomes[(x + 4) + 24 * (z + 4)];

                // calculate the total height based on the biome noise map, using a custom Noise2D for each biome
                for (Object2DoubleMap.Entry<TFCBiome> entry : heightBiomeMap.object2DoubleEntrySet())
                {
                    // This line breaks for some reason?
                    totalHeight += entry.getDoubleValue() * biomeNoiseMap.get(entry.getKey()).noise(chunkX + x, chunkZ + z);
                }

                baseHeight[x + 16 * z] = totalHeight;
            }
        }

        // Height
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                double totalHeight = baseHeight[x + 16 * z];
                for (int y = 0; y <= (int) totalHeight; y++)
                {
                    // todo: can this just be x, y, z since it all gets cut off anyway?
                    pos.setPos(chunkX + x, y, chunkZ + z);
                    chunk.setBlockState(pos, settings.getDefaultBlock(), false);
                }

                for (int y = (int) totalHeight + 1; y <= SEA_LEVEL; y++)
                {
                    pos.setPos(chunkX + x, y, chunkZ + z);
                    chunk.setBlockState(pos, settings.getDefaultFluid(), false);
                }
            }
        }

        // Biomes
        chunk.setBiomes(chunkBiomes);

        // Height maps
        chunk.func_217303_b(Heightmap.Type.OCEAN_FLOOR_WG);
        chunk.func_217303_b(Heightmap.Type.WORLD_SURFACE_WG);
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
        BlockPos.MutableBlockPos posAt = new BlockPos.MutableBlockPos();
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
