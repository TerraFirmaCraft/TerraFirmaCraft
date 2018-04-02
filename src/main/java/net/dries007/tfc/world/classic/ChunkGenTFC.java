package net.dries007.tfc.world.classic;

import net.dries007.tfc.objects.biomes.BiomesTFC;
import net.dries007.tfc.objects.blocks.BlockTFCVariant.Type;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;
import net.dries007.tfc.world.classic.genlayers.datalayers.drainage.GenDrainageLayer;
import net.dries007.tfc.world.classic.genlayers.datalayers.evt.GenEVTLayer;
import net.dries007.tfc.world.classic.genlayers.datalayers.ph.GenPHLayer;
import net.dries007.tfc.world.classic.genlayers.datalayers.rain.GenRainLayerTFC;
import net.dries007.tfc.world.classic.genlayers.datalayers.rock.GenRockLayer;
import net.dries007.tfc.world.classic.genlayers.datalayers.stability.GenStabilityLayer;
import net.dries007.tfc.world.classic.genlayers.datalayers.tree.GenTreeLayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.layer.IntCache;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static net.dries007.tfc.world.classic.DataLayer.*;

public class ChunkGenTFC implements IChunkGenerator
{
    private static final IBlockState STONE = Blocks.STONE.getDefaultState();
    private static final IBlockState AIR = Blocks.AIR.getDefaultState();
    private static final IBlockState WATER = Blocks.WATER.getDefaultState();

    private final World world;
    private final long seed;
    private final Random rand;
    public final WorldGenSettings settings;

    private final NoiseGeneratorOctaves noiseGen1;
    private final NoiseGeneratorOctaves noiseGen2;
    private final NoiseGeneratorOctaves noiseGen3;
    private final NoiseGeneratorOctaves noiseGen4;
    private final NoiseGeneratorOctaves noiseGen6;
    private final NoiseGeneratorOctaves mobSpawnerNoise;

    private final GenLayerTFC rocksGenLayer1;
    private final GenLayerTFC rocksGenLayer2;
    private final GenLayerTFC rocksGenLayer3;
    private final GenLayerTFC treesGenLayer1;
    private final GenLayerTFC treesGenLayer2;
    private final GenLayerTFC treesGenLayer3;
    private final GenLayerTFC evtGenLayer;
    private final GenLayerTFC rainfallGenLayer;
    private final GenLayerTFC stabilityGenLayer;
    private final GenLayerTFC phGenLayer;
    private final GenLayerTFC drainageGenLayer;


    private final double[] noise1 = new double[425];
    private final double[] noise2 = new double[425];
    private final double[] noise3 = new double[425];
    private final double[] noise4 = new double[425];
    private final double[] noise6 = new double[425];

    private final double[] heightMap = new double[425];
    private final Biome[] biomes = new Biome[324];

    private final DataLayer[] rockLayer1 = new DataLayer[256];
    private final DataLayer[] rockLayer2 = new DataLayer[256];
    private final DataLayer[] rockLayer3 = new DataLayer[256];
    private final DataLayer[] evtLayer = new DataLayer[256];
    private final DataLayer[] rainfallLayer = new DataLayer[256];
    private final DataLayer[] stabilityLayer = new DataLayer[256];
    private final DataLayer[] drainageLayer = new DataLayer[256];

    private final static float[] parabolicField = new float[25];

    static
    {
        for (int x = -2; x <= 2; ++x)
        {
            for (int y = -2; y <= 2; ++y)
            {
                parabolicField[x + 2 + (y + 2) * 5] = 10.0F / MathHelper.sqrt(x * x + y * y + 0.2F);
                // Results in the following plot: http://i.imgur.com/rxrui67.png
            }
        }
    }

    private final int[] seaLevelOffsetMap = new int[256];
    private final int[] chunkHeightMap = new int[256];

    /* todo
    private MapGenCavesTFC caveGen = new MapGenCavesTFC();
    private MapGenRavineTFC surfaceRavineGen = new MapGenRavineTFC(125, 30);//surface
    private MapGenRavineTFC ravineGen = new MapGenRavineTFC(20, 50);//deep
    private MapGenRiverRavine riverRavineGen = new MapGenRiverRavine();
    */

    public ChunkGenTFC(World w, String settingsString)
    {
        this.world = w;
        seed = world.getSeed();
        rand = new Random(seed);
        settings = WorldGenSettings.fromString(settingsString);

        noiseGen1 = new NoiseGeneratorOctaves(rand, 4);
        noiseGen2 = new NoiseGeneratorOctaves(rand, 16);
        noiseGen3 = new NoiseGeneratorOctaves(rand, 8);
        noiseGen4 = new NoiseGeneratorOctaves(rand, 4);
        noiseGen6 = new NoiseGeneratorOctaves(rand, 1);
        mobSpawnerNoise = new NoiseGeneratorOctaves(rand, 8);

        rocksGenLayer1 = GenRockLayer.initialize(seed+1, ROCK_LAYER_1);
        rocksGenLayer2 = GenRockLayer.initialize(seed+2, ROCK_LAYER_2);
        rocksGenLayer3 = GenRockLayer.initialize(seed+3, ROCK_LAYER_3);

        treesGenLayer1 = GenTreeLayer.initialize(seed+4, TREE_ARRAY);
        treesGenLayer2 = GenTreeLayer.initialize(seed+5, TREE_ARRAY);
        treesGenLayer3 = GenTreeLayer.initialize(seed+6, TREE_ARRAY);

        evtGenLayer = GenEVTLayer.initialize(seed+7);
        rainfallGenLayer = GenRainLayerTFC.initialize(seed+8);
        stabilityGenLayer = GenStabilityLayer.initialize(seed+9);
        phGenLayer = GenPHLayer.initialize(seed+10);
        drainageGenLayer = GenDrainageLayer.initialize(seed+11);
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ)
    {
        rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        ChunkPrimer chunkPrimerIn = new ChunkPrimer();
        setBlocksInChunk(chunkX, chunkZ, chunkPrimerIn);

        world.getBiomeProvider().getBiomes(biomes, chunkX * 16 - 1, chunkZ * 16 - 1, 18, 18);

        loadLayerGeneratorData(rocksGenLayer1, rockLayer1, chunkX * 16, chunkZ * 16, 16, 16);
        loadLayerGeneratorData(rocksGenLayer2, rockLayer2, chunkX * 16, chunkZ * 16, 16, 16);
        loadLayerGeneratorData(rocksGenLayer3, rockLayer3, chunkX * 16, chunkZ * 16, 16, 16);
        loadLayerGeneratorData(evtGenLayer, evtLayer, chunkX * 16, chunkZ * 16, 16, 16);
        loadLayerGeneratorData(rainfallGenLayer, rainfallLayer, chunkX * 16, chunkZ * 16, 16, 16);
        loadLayerGeneratorData(stabilityGenLayer, stabilityLayer, chunkX * 16, chunkZ * 16, 16, 16);
        loadLayerGeneratorData(drainageGenLayer, drainageLayer, chunkX * 16, chunkZ * 16, 16, 16);

        CustomChunkPrimer chunkPrimerOut = new CustomChunkPrimer();
        replaceBlocksForBiomeHigh(chunkX, chunkZ, chunkPrimerIn, chunkPrimerOut);
        // replaceBlocksForBiomeLow(chunkX, chunkZ, rand, idsBig, metaBig);

        /* todo
        caveGen.generate(this, world, chunkX, chunkZ, idsBig, metaBig);
        surfaceRavineGen.generate(this, world, chunkX, chunkZ, idsBig, metaBig);//surface
        ravineGen.generate(this, world, chunkX, chunkZ, idsBig, metaBig);//deep
        riverRavineGen.generate(this, world, chunkX, chunkZ, idsBig, metaBig);
        */

        Chunk chunk = new Chunk(world, chunkPrimerOut, chunkX, chunkZ);
//        Chunk chunk = new Chunk(world, chunkPrimerIn, chunkX, chunkZ);

        byte[] biomeIds = chunk.getBiomeArray();
        for (int x = 0; x < 16; ++x)
        {
            for (int z = 0; z < 16; ++z)
            {
                biomeIds[z << 4 | x] = (byte) Biome.getIdForBiome(getBiomeOffset(x, z));
//                chunk.setBlockState(new BlockPos(x, 255, z), Blocks.STAINED_GLASS.getStateFromMeta(biomeIds[z << 4 | x] & 15)); // todo: remove
            }
        }
        /*
        ChunkData data = new ChunkData(chunk).createNew(world, chunkX, chunkZ);
        data.heightmap = seaLevelOffsetMap;
        data.rainfallMap = rainfallLayer;
        TFC_Core.getCDM(world).addData(chunk, data);
        //chunk.heightMap = chunkHeightMap;
        */
        chunk.setHeightMap(chunkHeightMap);
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(int x, int z)
    {
        //todo
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z)
    {
        return false; //todo
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        return null; //todo
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored)
    {
        return null; //todo
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z)
    {
        //todo
    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos)
    {
        return false; //todo
    }

    private void loadLayerGeneratorData(GenLayerTFC gen, DataLayer[] layers, int x, int y, int width, int height)
    {
        IntCache.resetIntCache();
        int[] ints = gen.getInts(x, y, width, height);
        for (int i = 0; i < width * height; ++i)
        {
            layers[i] = DataLayer.LAYERS[ints[i]];
        }
    }

    private void setBlocksInChunk(int chunkX, int chunkZ, ChunkPrimer primer)
    {
        world.getBiomeProvider().getBiomesForGeneration(biomes, chunkX * 4 - 2, chunkZ * 4 - 2, 10, 10);
        generateHeightMap(chunkX * 4, chunkZ * 4);

        for (int x = 0; x < 4; ++x)
        {
            for (int z = 0; z < 4; ++z)
            {
                for (int y = 0; y < 16; ++y)
                {
                    double noiseDL = heightMap[((x + 0) * 5 + z + 0) * 17 + y];
                    double noiseUL = heightMap[((x + 0) * 5 + z + 1) * 17 + y];
                    double noiseDR = heightMap[((x + 1) * 5 + z + 0) * 17 + y];
                    double noiseUR = heightMap[((x + 1) * 5 + z + 1) * 17 + y];
                    final double noiseDLA = (heightMap[((x + 0) * 5 + z + 0) * 17 + y + 1] - noiseDL) * 0.125D;
                    final double noiseULA = (heightMap[((x + 0) * 5 + z + 1) * 17 + y + 1] - noiseUL) * 0.125D;
                    final double noiseDRA = (heightMap[((x + 1) * 5 + z + 0) * 17 + y + 1] - noiseDR) * 0.125D;
                    final double noiseURA = (heightMap[((x + 1) * 5 + z + 1) * 17 + y + 1] - noiseUR) * 0.125D;

                    for (int yy = 0; yy < 8; ++yy)
                    {
                        double var34 = noiseDL;
                        double var36 = noiseUL;
                        final double var38 = (noiseDR - noiseDL) * 0.25D;
                        final double var40 = (noiseUR - noiseUL) * 0.25D;

                        for (int xx = 0; xx < 4; ++xx)
                        {
                            final double var49 = (var36 - var34) * 0.25D;
                            double var47 = var34 - var49;

                            for (int zz = 0; zz < 4; ++zz)
                            {
                                if ((var47 += var49) > 0.0D)
                                {
                                    primer.setBlockState(x * 4 + xx, y * 8 + yy, z * 4 + zz, STONE);
                                }
                                else if (y * 8 + yy < 16)
                                {
                                    primer.setBlockState(x * 4 + xx, y * 8 + yy, z * 4 + zz, WATER);
                                }
                            }
                            var34 += var38;
                            var36 += var40;
                        }
                        noiseDL += noiseDLA;
                        noiseUL += noiseULA;
                        noiseDR += noiseDRA;
                        noiseUR += noiseURA;
                    }
                }
            }
        }
    }

    private void generateHeightMap(int xPos, int zPos)
    {
        noiseGen6.generateNoiseOctaves(noise6, xPos, zPos, 5, 5, 200.0D, 200.0D, 0.5D);
        noiseGen3.generateNoiseOctaves(noise3, xPos, 0, zPos, 5, 17, 5, 12.5, 6.25, 12.5);
        noiseGen1.generateNoiseOctaves(noise1, xPos, 0, zPos, 5, 17, 5, 1000D, 1000D, 1000D);
        noiseGen2.generateNoiseOctaves(noise2, xPos, 0, zPos, 5, 17, 5, 1000D, 1000D, 1000D);

        int i = 0;
        int j = 0;

        for (int x = 0; x < 5; ++x)
        {
            for (int z = 0; z < 5; ++z)
            {
                float variationBlended = 0.0F;
                float rootBlended = 0.0F;
                float totalBlendedHeight = 0.0F;
                Biome baseBiome = biomes[x + 2 + (z + 2) * 10];

                for (int xR = -2; xR <= 2; ++xR)
                {
                    for (int zR = -2; zR <= 2; ++zR)
                    {
                        Biome blendBiome = biomes[x + xR + 2 + (z + zR + 2) * 10];
                        float blendedHeight = parabolicField[xR + 2 + (zR + 2) * 5] / 2.0F;
                        if (blendBiome.getBaseHeight() > baseBiome.getBaseHeight())
                            blendedHeight *= 0.5F;

                        variationBlended += blendBiome.getHeightVariation() * blendedHeight;
                        rootBlended += blendBiome.getHeightVariation() * blendedHeight;
                        totalBlendedHeight += blendedHeight;
                    }
                }

                variationBlended /= totalBlendedHeight;
                rootBlended /= totalBlendedHeight;
                variationBlended = variationBlended * 0.9F + 0.1F;
                rootBlended = (rootBlended * 4.0F - 1.0F) / 8.0F;

                double scaledNoise6Value = noise6[j++] / 8000.0D;

                if (scaledNoise6Value < 0.0D)
                    scaledNoise6Value = -scaledNoise6Value * 0.3D; //If negative, make positive and shrink by a third?

                scaledNoise6Value = scaledNoise6Value * 3.0D - 2.0D;

                if (scaledNoise6Value < 0.0D) // Only true when noise6[index2] is between -17,777 and 0, scaledNoise6Value will be at maximum -2
                {
                    scaledNoise6Value /= 2.0D; // Results in values between 0 and -1
                    if (scaledNoise6Value < -1.0D) //Error Checking
                        scaledNoise6Value = -1.0D;
                    scaledNoise6Value /= 1.4D * 2.0D; // Results in values between 0 and -0.357143
                }
                else
                {
                    if (scaledNoise6Value > 1.0D)
                        scaledNoise6Value = 1.0D;
                    scaledNoise6Value /= 8.0D; // Results in values between 0 and 0.125
                }

                for (int y = 0; y < 17; ++y)
                {
                    double rootBlendedCopy = rootBlended;
                    rootBlendedCopy += scaledNoise6Value * 0.2D;
                    rootBlendedCopy = rootBlendedCopy * 17 / 16.0D;
                    double var28 = 17 / 2.0D + rootBlendedCopy * 4.0D;
                    double output;
                    double var32 = (y - var28) * 12.0D * 256.0D / 256.0D / (2.70 + variationBlended);

                    if (var32 < 0.0D)
                        var32 *= 4.0D;

                    double var34 = noise1[i] / 512.0D;
                    double var36 = noise2[i] / 512.0D;
                    double var38 = (noise3[i] / 10.0D + 1.0D) / 2.0D;

                    if (var38 < 0.0D)
                        output = var34;
                    else if (var38 > 1.0D)
                        output = var36;
                    else
                        output = var34 + (var36 - var34) * var38;

                    output -= var32;
                    if (y > 17 - 4)
                    {
                        double var40 = (y - (17 - 4)) / 3.0F;
                        output = output * (1.0D - var40) + -10.0D * var40;
                    }

                    heightMap[i++] = output;
                }
            }
        }
    }

    private Biome getBiomeOffset(int x, int z)
    {
        return biomes[z + 1 + (x + 1) * 18];
    }

    private void replaceBlocksForBiomeHigh(int chunkX, int chunkZ, ChunkPrimer inp, CustomChunkPrimer outp)
    {
        final int seaLevel = 16;
        int yOffset = 128;
        double var6 = 0.03125D;
        noiseGen4.generateNoiseOctaves(noise4, chunkX * 16, chunkZ * 16, 0, 16, 16, 1, var6 * 4.0D, var6 * 1.0D, var6 * 4.0D);
        boolean[] cliffMap = new boolean[256];
        for (int x = 0; x < 16; ++x)
        {
            for (int z = 0; z < 16; ++z)
            {
                int colIndex = x + z * 16;
                int colIndexDL = z + x * 16;
                Biome biome = getBiomeOffset(x, z);
                DataLayer rock1 = rockLayer1[colIndexDL];
                DataLayer rock2 = rockLayer2[colIndexDL];
                DataLayer rock3 = rockLayer3[colIndexDL];
//                DataLayer evt = evtLayer[colIndexDL];
                DataLayer rain = rainfallLayer[colIndexDL];
                DataLayer drainage = drainageLayer[colIndexDL];
                int noise = (int)(noise4[x + 1 + (z + 1) * 16] / 3.0D + 6.0D);//todo WTF? check what this fixes: it was x + 1 + z + 1 * 16
                int smooth = -1;

                IBlockState surfaceBlock = rock1.block.getVariant(rain.valueFloat >= 500 ? Type.GRASS : Type.DRY_GRASS).getDefaultState();
                IBlockState subSurfaceBlock = rock1.block.getVariant(Type.DIRT).getDefaultState();

                float bioTemp = 25;//todo: TFC_Climate.getBioTemperature(worldObj, chunkX * 16 + x, chunkZ * 16 + z);

                if (BiomesTFC.isBeachBiome(getBiomeOffset(x-1, z)) || BiomesTFC.isBeachBiome(getBiomeOffset(x+1, z)) || BiomesTFC.isBeachBiome(getBiomeOffset(x, z+1)) || BiomesTFC.isBeachBiome(getBiomeOffset(x, z-1)))
                {
                    if(!BiomesTFC.isBeachBiome(getBiomeOffset(x, z))) cliffMap[colIndex] = true;
                }

                int h = 0;
                for (int y = 127; y >= 0; --y)
                {
                    float temp = Climate.adjustHeightToTemp(y, bioTemp);
                    if (BiomesTFC.isBeachBiome(biome) && y > seaLevel + h && inp.getBlockState(x, y, z) == STONE)
                    {
                        inp.setBlockState(x, y, z, AIR);
                        if (h == 0) h = (y - 16) / 4;
                    }

                    if (outp.isEmpty(x, y + yOffset, z))
                    {
                        outp.setBlockState(x, y + yOffset, z, inp.getBlockState(x, y, z));
                        if (y + 1 < yOffset && outp.getBlockState(x, y + yOffset, z) == AIR/* no need to check again && BlocksTFC.isSoilOrGravel(outp.getBlockState(x, y + yOffset + 1, z))*/)
                        {
                            for(int upCount = 1; BlocksTFC.isSoilOrGravel(outp.getBlockState(x, y + yOffset + upCount, z)); upCount++)
                            {
                                outp.setBlockState(x, y + yOffset + upCount, z, AIR);
                            }
                        }
                    }

                    if (outp.getBlockState(x, y + yOffset, z) == STONE)
                    {
                        if(seaLevelOffsetMap[colIndex] == 0 && y-16 >= 0)
                            seaLevelOffsetMap[colIndex] = y-16;

                        if(chunkHeightMap[colIndex] == 0)
                            chunkHeightMap[colIndex] = y + yOffset;

                        if (y + yOffset <= 110 + seaLevelOffsetMap[colIndex])
                        {
                            outp.setBlockState(x, y + yOffset, z, rock3.block.getDefaultState());
                        }
                        else if(y + yOffset <= 110 + seaLevelOffsetMap[colIndex] && y + yOffset > 55 + seaLevelOffsetMap[colIndex])
                        {
                            outp.setBlockState(x, y + yOffset, z, rock2.block.getDefaultState());
                        }
                        else
                        {
                            outp.setBlockState(x, y + yOffset, z, rock1.block.getDefaultState());
                        }

                        //First we check to see if its a cold desert
                        if (rain.valueFloat < 125 && temp < 1.5f)
                        {
                            subSurfaceBlock = surfaceBlock = rock1.block.getVariant(Type.SAND).getDefaultState();
                        }
                        //Next we check for all other warm deserts
                        else if(rain.valueFloat < 125 && biome.getHeightVariation() < 0.5f && temp > 20f)
                        {
                            subSurfaceBlock = surfaceBlock = rock1.block.getVariant(Type.SAND).getDefaultState();
                        }

                        if (biome == BiomesTFC.BEACH || biome == BiomesTFC.OCEAN || biome == BiomesTFC.DEEP_OCEAN)
                        {
                            subSurfaceBlock = surfaceBlock = rock1.block.getVariant(Type.SAND).getDefaultState();
                        }
                        else if(biome == BiomesTFC.GRAVEL_BEACH)
                        {
                            subSurfaceBlock = surfaceBlock = rock1.block.getVariant(Type.GRAVEL).getDefaultState();
                        }

                        if (smooth == -1)
                        {
                            //The following makes dirt behave nicer and more smoothly, instead of forming sharp cliffs.
                            int arrayIndexx = x > 0 ? x - 1 + (z * 16) : -1;
                            int arrayIndexX = x < 15 ? x + 1 + (z * 16) : -1;
                            int arrayIndexz = z > 0? x + ((z-1) * 16):-1;
                            int arrayIndexZ = z < 15? x + ((z+1) * 16):-1;
                            for(int counter = 1; counter < noise / 3; counter++)
                            {
                                if (arrayIndexx >= 0 && seaLevelOffsetMap[colIndex]-(3*counter) > seaLevelOffsetMap[arrayIndexx] &&
                                        arrayIndexX >= 0 && seaLevelOffsetMap[colIndex]-(3*counter) > seaLevelOffsetMap[arrayIndexX] &&
                                        arrayIndexz >= 0 && seaLevelOffsetMap[colIndex]-(3*counter) > seaLevelOffsetMap[arrayIndexz] &&
                                        arrayIndexZ >= 0 && seaLevelOffsetMap[colIndex]-(3*counter) > seaLevelOffsetMap[arrayIndexZ])
                                {
                                    seaLevelOffsetMap[colIndex]--;
                                    noise--;
                                    y--;
                                }
                            }
                            smooth = (int) (noise * (1d - Math.max(Math.min((y - 16) / 80d, 1), 0)));

                            // Set soil below water
                            for(int c = 1; c < 3; c++)
                            {
                                if (yOffset + y + c > 256) continue;

                                IBlockState current = outp.getBlockState(x, yOffset + y + c, z);
                                if (current != surfaceBlock && current != subSurfaceBlock && !BlocksTFC.isWater(current))
                                {
                                    outp.setBlockState(x, yOffset + y + c, z, AIR);
                                    if (yOffset + y + c + 1 > 256) continue;
                                    if (outp.getBlockState(x, yOffset + y + c + 1, z) == WATER) // todo: TFCBlocks.saltWaterStationary
                                    {
                                        outp.setBlockState(x, yOffset + y + c, z, subSurfaceBlock);
                                    }
                                }
                            }

                            // Determine the soil depth based on world y
                            int dirtH = Math.max(8-((y + 96 - WorldTypeTFC.SEALEVEL) / 16), 0);

                            if (smooth > 0)
                            {
                                if (y >= seaLevel - 1 && y+1 < yOffset && inp.getBlockState(x, y + 1, z) != WATER && dirtH > 0)// todo: TFCBlocks.saltWaterStationary
                                {
                                    outp.setBlockState(x, y + yOffset, z, surfaceBlock);

                                    boolean mountains = BiomesTFC.isMountainBiome(biome) || biome == BiomesTFC.HIGH_HILLS || biome == BiomesTFC.HIGH_HILLS_EDGE;
                                    for (int c = 1; c < dirtH && !mountains && !cliffMap[colIndex]; c++)
                                    {
                                        outp.setBlockState(x, y - c + yOffset, z, subSurfaceBlock);
                                        if (c > 1 + (5-drainage.valueInt))
                                        {
                                            outp.setBlockState(x, y - c + yOffset, z, rock1.block.getVariant(Type.GRAVEL).getDefaultState());
                                        }
                                    }
                                }
                            }
                        }

//                        if (y > seaLevel - 2 && y < seaLevel && primer[index + 1] == TFCBlocks.saltWaterStationary || y < seaLevel && primer[index + 1] == TFCBlocks.saltWaterStationary)
                        if (y > seaLevel - 2 && y < seaLevel && inp.getBlockState(x, y+1, z) == WATER ||
                                y < seaLevel && inp.getBlockState(x, y+1, z) == WATER)
                        {
                            if (biome != BiomesTFC.SWAMPLAND) // Most areas have gravel and sand bottoms
                            {
                                if (outp.getBlockState(x, y + yOffset, z) != rock1.block.getVariant(Type.SAND).getDefaultState() && rand.nextInt(5) != 0)
                                {
                                    outp.setBlockState(x, y + yOffset, z, rock1.block.getVariant(Type.GRAVEL).getDefaultState());
                                }
                            }
                            else // Swamp biomes have bottoms that are mostly dirt
                            {
                                if (outp.getBlockState(x, y + yOffset, z) != rock1.block.getVariant(Type.SAND).getDefaultState())
                                {
                                    outp.setBlockState(x, y + yOffset, z, rock1.block.getVariant(Type.DIRT).getDefaultState());
                                }
                            }
                        }
                    }
                    else if (inp.getBlockState(x, y, z) == WATER && !(BiomesTFC.isOceanicBiome(biome) || BiomesTFC.isBeachBiome(biome))) //  && biome != BiomesTFC.OCEAN && biome != BiomesTFC.DEEP_OCEAN && biome != BiomesTFC.BEACH && biome != BiomesTFC.GRAVEL_BEACH
                    {
                        outp.setBlockState(x, y + yOffset, z, WATER); // todo fresh water
                    }
                }
            }
        }
    }
/*
    private void replaceBlocksForBiomeLow(int par1, int par2, Random rand, Block[] idsBig, byte[] metaBig)
    {
        for (int xCoord = 0; xCoord < 16; ++xCoord)
        {
            for (int zCoord = 0; zCoord < 16; ++zCoord)
            {
                int arrayIndex = xCoord + zCoord * 16;
                int arrayIndexDL = zCoord + xCoord * 16;
                DataLayer rock1 = rockLayer1[arrayIndexDL];
                DataLayer rock2 = rockLayer2[arrayIndexDL];
                DataLayer rock3 = rockLayer3[arrayIndexDL];
                DataLayer stability = stabilityLayer[arrayIndexDL];
                BiomesTFC biome = (BiomesTFC) getBiomeOffset(xCoord, zCoord);

                for (int height = 127; height >= 0; --height)
                {
                    //int index = ((arrayIndex) * 128 + height);
                    int indexBig = (arrayIndex) * 256 + height;
                    metaBig[indexBig] = 0;

                    if (height <= 1 + (seaLevelOffsetMap[arrayIndex] / 3) + this.rand.nextInt(3))
                    {
                        idsBig[indexBig] = Blocks.bedrock;
                    }
                    else if(idsBig[indexBig] == null)
                    {
                        convertStone(height, arrayIndex, indexBig, idsBig, metaBig, rock1, rock2, rock3);
                        if(TFC_Core.isBeachBiome(biome) || TFC_Core.isOceanicBiome(biome))
                        {
                            if(idsBig[indexBig+1] == TFCBlocks.saltWaterStationary)
                            {
                                idsBig[indexBig] = TFC_Core.getTypeForSand(rock1.data1);
                                metaBig[indexBig] = (byte)TFC_Core.getSoilMeta(rock1.data1);
                                idsBig[indexBig-1] = TFC_Core.getTypeForSand(rock1.data1);
                                metaBig[indexBig-1] = (byte)TFC_Core.getSoilMeta(rock1.data1);
                            }
                        }
                    }

                    if (height <= 6 && stability.data1 == 1 && idsBig[indexBig] == Blocks.air)
                    {
                        idsBig[indexBig] = TFCBlocks.lava;
                        metaBig[indexBig] = 0;
                        if(idsBig[indexBig+1] != TFCBlocks.lava && rand.nextBoolean())
                        {
                            idsBig[indexBig+1] = TFCBlocks.lava;
                            metaBig[indexBig+1] = 0;
                        }
                    }
                }
            }
        }
    }
    */

}
