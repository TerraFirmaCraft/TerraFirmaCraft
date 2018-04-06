package net.dries007.tfc.world.classic;

import com.google.common.collect.ImmutableList;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.objects.biomes.BiomesTFC;
import net.dries007.tfc.objects.blocks.BlockTFCVariant;
import net.dries007.tfc.objects.blocks.BlockTFCVariant.Material;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.world.classic.capabilities.ChunkDataProvider;
import net.dries007.tfc.world.classic.capabilities.ChunkDataTFC;
import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;
import net.dries007.tfc.world.classic.genlayers.datalayers.drainage.GenDrainageLayer;
import net.dries007.tfc.world.classic.genlayers.datalayers.evt.GenEVTLayer;
import net.dries007.tfc.world.classic.genlayers.datalayers.ph.GenPHLayer;
import net.dries007.tfc.world.classic.genlayers.datalayers.rain.GenRainLayerTFC;
import net.dries007.tfc.world.classic.genlayers.datalayers.rock.GenRockLayer;
import net.dries007.tfc.world.classic.genlayers.datalayers.stability.GenStabilityLayer;
import net.dries007.tfc.world.classic.genlayers.datalayers.tree.GenTreeLayer;
import net.dries007.tfc.world.classic.mapgen.MapGenCavesTFC;
import net.dries007.tfc.world.classic.mapgen.MapGenRavineTFC;
import net.dries007.tfc.world.classic.mapgen.MapGenRiverRavine;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.layer.IntCache;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static net.dries007.tfc.world.classic.DataLayer.*;
import static net.dries007.tfc.world.classic.WorldTypeTFC.ROCKLAYER2;
import static net.dries007.tfc.world.classic.WorldTypeTFC.ROCKLAYER3;

/**
 * Todo: make caves in top stone layer under ocean water? (maybe 2 layers under deep ocean?)
 * todo: *important* Store generated layerdata on chunk with capabilities?
 * todo: ravine gen is not on point, the ravines are smaller (less wide) than the 1.7.10 ones.
 * todo: lava has not yet been seen
 */
public class ChunkGenTFC implements IChunkGenerator
{
    public static final IBlockState STONE = Blocks.STONE.getDefaultState();
    public static final IBlockState AIR = Blocks.AIR.getDefaultState();
    public static final IBlockState SALT_WATER = Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.BLUE); // todo: replace
    public static final IBlockState FRESH_WATER = Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.LIGHT_BLUE); // todo: replace
    public static final IBlockState HOT_WATER = Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.MAGENTA); // todo: replace
    public static final IBlockState LAVA = Blocks.LAVA.getDefaultState(); // todo: replace
    public static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
    public static final IBlockState SNOW = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, 2);

    public final WorldGenSettings s;

    private final World world;
    private final long seed;
    private final Random rand;

    private final NoiseGeneratorOctaves noiseGen1;
    private final NoiseGeneratorOctaves noiseGen2;
    private final NoiseGeneratorOctaves noiseGen3;
    private final NoiseGeneratorOctaves noiseGen4;
    private final NoiseGeneratorOctaves noiseGen5;
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

    private final double[] noise4 = new double[256];

    private final double[] noise5 = new double[425];
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

    private final MapGenBase caveGen;
    private final MapGenBase surfaceRavineGen;
    private final MapGenBase ravineGen;
    private final MapGenBase riverRavineGen;

    public ChunkGenTFC(World w, String settingsString)
    {
        world = w;
        seed = world.getSeed();
        rand = new Random(seed);
        s = WorldGenSettings.fromString(settingsString).build();

        noiseGen1 = new NoiseGeneratorOctaves(rand, 4);
        noiseGen2 = new NoiseGeneratorOctaves(rand, 16);
        noiseGen3 = new NoiseGeneratorOctaves(rand, 8);
        noiseGen4 = new NoiseGeneratorOctaves(rand, 4);
        noiseGen5 = new NoiseGeneratorOctaves(rand, 2);
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

        caveGen = new MapGenCavesTFC(rockLayer1, rainfallLayer);
        surfaceRavineGen = new MapGenRavineTFC(s.surfaceRavineRarity, s.surfaceRavineHeight, s.surfaceRavineVariability);
        ravineGen = new MapGenRavineTFC(s.ravineRarity, s.ravineHeight, s.ravineVariability);
        riverRavineGen = new MapGenRiverRavine(s.riverRavineRarity);
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ)
    {
        Arrays.fill(noise1, 0);
        Arrays.fill(noise2, 0);
        Arrays.fill(noise3, 0);
        Arrays.fill(noise4, 0);
        Arrays.fill(noise5, 0);
        Arrays.fill(noise6, 0);
        Arrays.fill(seaLevelOffsetMap, 0);
        Arrays.fill(chunkHeightMap, 0);
        Arrays.fill(heightMap, 0);

        rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        ChunkPrimer chunkPrimerIn = new ChunkPrimer();
        generateRoughTerrain(chunkX, chunkZ, chunkPrimerIn);

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

        caveGen.generate(world, chunkX, chunkZ, chunkPrimerOut);
        surfaceRavineGen.generate(world, chunkX, chunkZ, chunkPrimerOut);
        ravineGen.generate(world, chunkX, chunkZ, chunkPrimerOut);
        riverRavineGen.generate(world, chunkX, chunkZ, chunkPrimerOut);

        if (ConfigTFC.GENERAL.debugWorldGen)
        {
            for (int x = 0; x < 16; ++x)
            {
                for (int z = 0; z < 16; ++z)
                {
                    chunkPrimerOut.setBlockState(x, 240, z, Blocks.STAINED_GLASS.getStateFromMeta(Biome.getIdForBiome(getBiomeOffset(x, z)) & 15));

                    chunkPrimerOut.setBlockState(x, 242, z, Blocks.STAINED_GLASS.getStateFromMeta(rockLayer1[z << 4 | x].layerID & 15));
                    chunkPrimerOut.setBlockState(x, 244, z, Blocks.STAINED_GLASS.getStateFromMeta(rockLayer2[z << 4 | x].layerID & 15));
                    chunkPrimerOut.setBlockState(x, 246, z, Blocks.STAINED_GLASS.getStateFromMeta(rockLayer3[z << 4 | x].layerID & 15));

                    chunkPrimerOut.setBlockState(x, 248, z, Blocks.STAINED_GLASS.getStateFromMeta(evtLayer[x << 4 | z].layerID & 15));
                    chunkPrimerOut.setBlockState(x, 250, z, Blocks.STAINED_GLASS.getStateFromMeta(rainfallLayer[x << 4 | z].layerID & 15));
                    chunkPrimerOut.setBlockState(x, 252, z, Blocks.STAINED_GLASS.getStateFromMeta(stabilityLayer[x << 4 | z].layerID & 15));
                    chunkPrimerOut.setBlockState(x, 254, z, Blocks.STAINED_GLASS.getStateFromMeta(drainageLayer[x << 4 | z].layerID & 15));

                }
            }
        }

        Chunk chunk = new Chunk(world, chunkPrimerOut, chunkX, chunkZ);

        ChunkDataTFC chunkData = chunk.getCapability(ChunkDataProvider.CHUNK_DATA_CAPABILITY, null);
        if (chunkData == null) throw new IllegalStateException("ChunkData capability is missing.");
        chunkData.setGenerationData(rockLayer1, rockLayer2, rockLayer3, evtLayer, rainfallLayer, stabilityLayer, drainageLayer, seaLevelOffsetMap);

        byte[] biomeIds = chunk.getBiomeArray();
        for (int x = 0; x < 16; ++x)
        {
            for (int z = 0; z < 16; ++z)
            {
                biomeIds[z << 4 | x] = (byte) Biome.getIdForBiome(getBiomeOffset(x, z));
            }
        }

        chunk.setHeightMap(chunkHeightMap);
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(int chunkX, int chunkZ)
    {
        BlockFalling.fallInstantly = true;
        final int worldX = chunkX << 4;
        final int worldZ = chunkZ << 4;
        BlockPos blockpos = new BlockPos(worldX, 0, worldZ);
        final Biome biome = world.getBiome(blockpos.add(16, 0, 16));
        rand.setSeed(world.getSeed());
        rand.setSeed((long)chunkX * (rand.nextLong() / 2L * 2L + 1L) + (long)chunkZ * (rand.nextLong() / 2L * 2L + 1L) ^ world.getSeed());
        ChunkPos chunkpos = new ChunkPos(chunkX, chunkZ);
//        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, world, rand, chunkX, chunkZ, false);

//        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
//        ChunkDataTFC chunkData = chunk.getCapability(ChunkDataProvider.CHUNK_DATA_CAPABILITY, null);
//        if (chunkData == null) throw new IllegalStateException("ChunkData capability is missing.");

        /* fissue gen, is commented out in 1.7.10
        if (this.rand.nextInt(chunkData.isStable(0, 0) ? 4 : 6) == 0)
        {
            x = xCoord + this.rand.nextInt(16) + 8;
            z = zCoord + this.rand.nextInt(16) + 8;
            y = Global.SEALEVEL - rand.nextInt(45);
        } */

        biome.decorate(world, rand, blockpos);

        /*
        if (TerrainGen.populate(this, world, rand, chunkX, chunkZ, false, ANIMALS))
            WorldEntitySpawner.performWorldGenSpawning(world, biome, worldX + 8, worldZ + 8, 16, 16, rand);
        */

        blockpos = blockpos.add(8, 0, 8);
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                final int y = world.getPrecipitationHeight(blockpos.add(x, 0, z)).getY();

                world.canBlockFreeze(blockpos.add(x, y - 1, z), false); // todo: maybe actually freeze the water? Now nothing is done here.

                if (canSnowAt(blockpos.add(x, y, z))) world.setBlockState(blockpos.add(x, y, z), SNOW); // todo: Vary depth based on rainfall?
            }
        }

//        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(chunkProvider, worldObj, rand, chunkX, chunkZ, var11));

//        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(false, this, world, rand, chunkX, chunkZ, false);



        BlockFalling.fallInstantly = false;
    }

    private boolean canSnowAt(BlockPos pos)
    {
        if (!world.isAirBlock(pos) && !world.isAirBlock(pos.add(0, -1, 0)) && !SNOW.getBlock().canPlaceBlockAt(world, pos)) return false;
        if (ClimateTFC.getHeightAdjustedTemp(world, pos) >= 0F) return false;
        if (world.getLightFor(EnumSkyBlock.BLOCK, pos) < 10 /* todo: why? && CalenderTFC.getTotalMonths() < 1*/) return false;
        return world.getBlockState(pos.add(0, -1, 0)).getMaterial().blocksMovement();
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z)
    {
        return false; //todo
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        return ImmutableList.of(); //todo
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
            layers[i] = DataLayer.get(ints[i]);
        }
    }

    private void generateRoughTerrain(int chunkX, int chunkZ, ChunkPrimer primer)
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
                                    primer.setBlockState(x * 4 + xx, y * 8 + yy, z * 4 + zz, SALT_WATER);
                                }
                                else
                                {
                                    primer.setBlockState(x * 4 + xx, y * 8 + yy, z * 4 + zz, AIR);
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
                        rootBlended += blendBiome.getBaseHeight() * blendedHeight;
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
        return biomes[(z + 1) * 18 + (x + 1)]; //todo: check, was (z + 1) + (x + 1) * 18
    }

    private void replaceBlocksForBiomeHigh(int chunkX, int chunkZ, ChunkPrimer inp, CustomChunkPrimer outp)
    {
        final int seaLevel = 16;
        int yOffset = 128;
        double var6 = 0.03125D;
        noiseGen4.generateNoiseOctaves(noise4, chunkX * 16, chunkZ * 16, 0, 16, 16, 1, var6 * 4.0D, var6, var6 * 4.0D);
        boolean[] cliffMap = new boolean[256];
        for (int x = 0; x < 16; ++x)
        {
            for (int z = 0; z < 16; ++z)
            {
                int colIndex = z << 4 | x;
                Biome biome = getBiomeOffset(x, z);
                DataLayer rock1 = rockLayer1[colIndex];
                DataLayer rock2 = rockLayer2[colIndex];
                DataLayer rock3 = rockLayer3[colIndex];
//                DataLayer evt = evtLayer[colIndex];
                DataLayer rain = rainfallLayer[colIndex];
                DataLayer drainage = drainageLayer[colIndex];
                DataLayer stability = stabilityLayer[colIndex];
                int noise = (int)(noise4[colIndex] / 3.0D + 6.0D);
                int smooth = -1;

                IBlockState surfaceBlock = rock1.block.getVariant(rain.valueFloat >= 500 ? Material.GRASS : Material.DRY_GRASS).getDefaultState();
                IBlockState subSurfaceBlock = rock1.block.getVariant(Material.DIRT).getDefaultState();

                float bioTemp = 25;//todo: TFC_Climate.getBioTemperature(worldObj, chunkX * 16 + x, chunkZ * 16 + z);

                if (BiomesTFC.isBeachBiome(getBiomeOffset(x-1, z)) || BiomesTFC.isBeachBiome(getBiomeOffset(x+1, z)) || BiomesTFC.isBeachBiome(getBiomeOffset(x, z+1)) || BiomesTFC.isBeachBiome(getBiomeOffset(x, z-1)))
                {
                    if(!BiomesTFC.isBeachBiome(getBiomeOffset(x, z))) cliffMap[colIndex] = true;
                }

                int h = 0;
                for (int y = 127; y >= 0; y--)
                {
                    /*
                     * HIGH PART (yOffset is used)
                     */

                    float temp = ClimateTFC.adjustHeightToTemp(y, bioTemp);
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

                        if (y + yOffset <= ROCKLAYER3 + seaLevelOffsetMap[colIndex])
                        {
                            outp.setBlockState(x, y + yOffset, z, rock3.block.getDefaultState());
                        }
                        else if(y + yOffset <= ROCKLAYER2 + seaLevelOffsetMap[colIndex] && y + yOffset > ROCKLAYER3 + seaLevelOffsetMap[colIndex])
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
                            subSurfaceBlock = surfaceBlock = rock1.block.getVariant(Material.SAND).getDefaultState();
                        }
                        //Next we check for all other warm deserts
                        else if(rain.valueFloat < 125 && biome.getHeightVariation() < 0.5f && temp > 20f)
                        {
                            subSurfaceBlock = surfaceBlock = rock1.block.getVariant(Material.SAND).getDefaultState();
                        }

                        if (biome == BiomesTFC.BEACH || biome == BiomesTFC.OCEAN || biome == BiomesTFC.DEEP_OCEAN)
                        {
                            subSurfaceBlock = surfaceBlock = rock1.block.getVariant(BlockTFCVariant.Material.SAND).getDefaultState();
                        }
                        else if(biome == BiomesTFC.GRAVEL_BEACH)
                        {
                            subSurfaceBlock = surfaceBlock = rock1.block.getVariant(Material.GRAVEL).getDefaultState();
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
                                    if (outp.getBlockState(x, yOffset + y + c + 1, z) == SALT_WATER)
                                    {
                                        outp.setBlockState(x, yOffset + y + c, z, subSurfaceBlock);
                                    }
                                }
                            }

                            // Determine the soil depth based on world y
                            int dirtH = Math.max(8-((y + 96 - WorldTypeTFC.SEALEVEL) / 16), 0);

                            if (smooth > 0)
                            {
                                if (y >= seaLevel - 1 && y+1 < yOffset && inp.getBlockState(x, y + 1, z) != SALT_WATER && dirtH > 0)
                                {
                                    outp.setBlockState(x, y + yOffset, z, surfaceBlock);

                                    boolean mountains = BiomesTFC.isMountainBiome(biome) || biome == BiomesTFC.HIGH_HILLS || biome == BiomesTFC.HIGH_HILLS_EDGE;
                                    for (int c = 1; c < dirtH && !mountains && !cliffMap[colIndex]; c++)
                                    {
                                        outp.setBlockState(x, y - c + yOffset, z, subSurfaceBlock);
                                        if (c > 1 + (5-drainage.valueInt))
                                        {
                                            outp.setBlockState(x, y - c + yOffset, z, rock1.block.getVariant(Material.GRAVEL).getDefaultState());
                                        }
                                    }
                                }
                            }
                        }

                        if (y > seaLevel - 2 && y < seaLevel && inp.getBlockState(x, y+1, z) == SALT_WATER ||
                                y < seaLevel && inp.getBlockState(x, y+1, z) == SALT_WATER)
                        {
                            if (biome != BiomesTFC.SWAMPLAND) // Most areas have gravel and sand bottoms
                            {
                                if (outp.getBlockState(x, y + yOffset, z) != rock1.block.getVariant(Material.SAND).getDefaultState() && rand.nextInt(5) != 0)
                                {
                                    outp.setBlockState(x, y + yOffset, z, rock1.block.getVariant(Material.GRAVEL).getDefaultState());
                                }
                            }
                            else // Swamp biomes have bottoms that are mostly dirt
                            {
                                if (outp.getBlockState(x, y + yOffset, z) != rock1.block.getVariant(BlockTFCVariant.Material.SAND).getDefaultState())
                                {
                                    outp.setBlockState(x, y + yOffset, z, rock1.block.getVariant(BlockTFCVariant.Material.DIRT).getDefaultState());
                                }
                            }
                        }
                    }
                    //  && biome != BiomesTFC.OCEAN && biome != BiomesTFC.DEEP_OCEAN && biome != BiomesTFC.BEACH && biome != BiomesTFC.GRAVEL_BEACH
                    else if (inp.getBlockState(x, y, z) == SALT_WATER && !(BiomesTFC.isOceanicBiome(biome) || BiomesTFC.isBeachBiome(biome)))
                    {
                        outp.setBlockState(x, y + yOffset, z, FRESH_WATER);
                    }

                    /*
                     * LOW PART (yOffset is NOT used)
                     */

                    if (y < 1 + /*(seaLevelOffsetMap[colIndex] / 3)*/ + (s.flatBedrock ? 0 : rand.nextInt(3)))
                    {
                        outp.setBlockState(x, y, z, BEDROCK);
                    }
                    else if (outp.isEmpty(x, y, z))
                    {
                        if (y <= ROCKLAYER3 + seaLevelOffsetMap[colIndex])
                        {
                            outp.setBlockState(x, y, z, rock3.block.getDefaultState());
                        }
                        else if(y <= ROCKLAYER2 + seaLevelOffsetMap[colIndex] && y > ROCKLAYER3 + seaLevelOffsetMap[colIndex])
                        {
                            outp.setBlockState(x, y, z, rock2.block.getDefaultState());
                        }
                        else
                        {
                            outp.setBlockState(x, y, z, rock1.block.getDefaultState());
                        }
                        if (BiomesTFC.isBeachBiome(biome) || BiomesTFC.isOceanicBiome(biome))
                        {
                            if (outp.getBlockState(x, y + 1, z) == SALT_WATER)
                            {
                                outp.setBlockState(x, y, z, rock1.block.getVariant(Material.SAND).getDefaultState());
                                outp.setBlockState(x, y-1, z, rock1.block.getVariant(BlockTFCVariant.Material.SAND).getDefaultState());
                            }
                        }
                    }
                    if (y <= 6 && stability.valueInt == 1 && outp.getBlockState(x, y, z) == AIR)
                    {
                        outp.setBlockState(x, y, z, LAVA);
                        if (outp.getBlockState(x, y + 1, z) != LAVA && rand.nextBoolean())
                        {
                            outp.setBlockState(x, y + 1, z, LAVA);
                        }
                    }
                }
            }
        }
    }
}
