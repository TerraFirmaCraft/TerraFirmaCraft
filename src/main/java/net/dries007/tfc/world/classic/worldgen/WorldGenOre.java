package net.dries007.tfc.world.classic.worldgen;

import com.google.common.collect.ImmutableList;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.blocks.BlockTFCOre;
import net.dries007.tfc.objects.blocks.BlockTFCVariant;
import net.dries007.tfc.util.OreSpawnData;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.capabilities.ChunkDataTFC;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class WorldGenOre implements IWorldGenerator
{
    @Override
    public void generate(Random rng, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (!(chunkGenerator instanceof ChunkGenTFC)) return;
        final BlockPos start = new BlockPos(chunkX << 4, 0, chunkZ << 4);
        ChunkDataTFC chunkData = ChunkDataTFC.get(world, start);
        if (!chunkData.isInitialized()) return;

        for (OreSpawnData spawnData : OreSpawnData.ORE_SPAWN_DATA)
        {
            int veinSize;
            int veinAmount;
            int height;
            int diameter;
            switch (spawnData.size)
            {
                case SMALL:
                    veinSize = 20;
                    veinAmount = 30;
                    height = 5;
                    diameter = 40;
                    break;
                case MEDIUM:
                    veinSize = 30;
                    veinAmount = 40;
                    height = 10;
                    diameter = 60;
                    break;
                case LARGE:
                    veinSize = 60;
                    veinAmount = 45;
                    height = 20;
                    diameter = 80;
                    break;
                default:
                    throw new RuntimeException("Enum constants not constant");
            }

            if (!spawnData.baseRocks.contains(chunkData.getRock1(0, 0).rock) &&
                    !spawnData.baseRocks.contains(chunkData.getRock2(0, 0).rock) &&
                    !spawnData.baseRocks.contains(chunkData.getRock2(0, 0).rock))
                continue;

            int gradeInt = rng.nextInt(100);
            BlockTFCOre.Grade grade;
            if (gradeInt < 20)
                grade = BlockTFCOre.Grade.RICH;
            else if (gradeInt < 50)
                grade = BlockTFCOre.Grade.POOR;
            else
                grade = BlockTFCOre.Grade.NORMAL;

            if (rng.nextInt(spawnData.rarity) != 0) continue;
            final int mineHeight = spawnData.minY + rng.nextInt(spawnData.maxY - spawnData.minY);

            for (int i = 0; i < veinAmount; i++)
            {
                final BlockPos pos = start.add(mPCalculateDensity(rng, diameter, spawnData.densityHorizontal), mineHeight + mPCalculateDensity(rng, height, spawnData.densityVertical), mPCalculateDensity(rng, diameter, spawnData.densityHorizontal));
                switch (spawnData.type)
                {
                    case DEFAULT:
                        generateDefault(spawnData.ore, grade, world, rng, pos, veinSize, chunkData, spawnData.baseRocks);
                        break;
                    case VEINS:
                        generateVein(spawnData.ore, grade, world, rng, pos, veinSize, chunkData, spawnData.baseRocks);
                        break;
                }
            }
        }
    }

    private void generateDefault(BlockTFCOre.Ore ore, BlockTFCOre.Grade grade, World world, Random rng, BlockPos start, int size, ChunkDataTFC chunkData, ImmutableList<BlockTFCVariant.Rock> baseRocks)
    {
        final float angle = rng.nextFloat() * (float) Math.PI;
        final double minX = start.getX() + 8 + MathHelper.sin(angle) * size / 8.0F;
        final double maxX = start.getX() + 8 - MathHelper.sin(angle) * size / 8.0F;
        final double minZ = start.getZ() + 8 + MathHelper.cos(angle) * size / 8.0F;
        final double maxZ = start.getZ() + 8 - MathHelper.cos(angle) * size / 8.0F;
        final double minY = start.getY() + rng.nextInt(3) - 2;
        final double maxY = start.getY() + rng.nextInt(3) - 2;

        for (int i = 0; i <= size; ++i)
        {
            final double centerX = minX + (maxX - minX) * i / size;
            final double centerY = minY + (maxY - minY) * i / size;
            final double centerZ = minZ + (maxZ - minZ) * i / size;
            final double scale = rng.nextDouble() * size / 16.0D;
            final double radius = (MathHelper.sin(i * (float) Math.PI / size) + 1.0F) * scale + 1.0D;
            final int startX = MathHelper.floor(centerX - radius / 2.0D);
            final int startY = MathHelper.floor(centerY - radius / 2.0D);
            final int startZ = MathHelper.floor(centerZ - radius / 2.0D);
            final int endX = MathHelper.floor(centerX + radius / 2.0D);
            final int endY = MathHelper.floor(centerY + radius / 2.0D);
            final int endZ = MathHelper.floor(centerZ + radius / 2.0D);

            for (int posX = startX; posX <= endX; ++posX)
            {
                double rx = (posX + 0.5D - centerX) / (radius / 2.0D);
                if (rx * rx >= 1.0D) continue;

                for (int posY = startY; posY <= endY; ++posY)
                {
                    double ry = (posY + 0.5D - centerY) / (radius / 2.0D);
                    if (rx * rx + ry * ry >= 1.0D) continue;

                    for (int posZ = startZ; posZ <= endZ; ++posZ)
                    {
                        double rz = (posZ + 0.5D - centerZ) / (radius / 2.0D);
                        if (rx * rx + ry * ry + rz * rz >= 1.0D) continue;

                        final BlockPos pos = new BlockPos(posX, posY + chunkData.getSeaLevelOffset(posX & 15, posZ & 15), posZ);
                        final IBlockState current = world.getBlockState(pos);

                        if (!(current.getBlock() instanceof BlockTFCVariant)) continue;

                        final BlockTFCVariant currentBlock = (BlockTFCVariant) current.getBlock();

                        if (currentBlock.type != BlockTFCVariant.Type.RAW || !baseRocks.contains(currentBlock.rock)) continue;

                        world.setBlockState(pos, BlockTFCOre.get(currentBlock.rock, ore, grade));
                    }
                }
            }
        }
        TerraFirmaCraft.getLog().info("Normal of {} was placed at {}", ore, start);
    }

    private void generateVein(BlockTFCOre.Ore ore, BlockTFCOre.Grade grade, World world, Random rng, BlockPos start, int size, ChunkDataTFC chunkData, ImmutableList<BlockTFCVariant.Rock> baseRocks)
    {
        int posX2 = 0;
        int posY2 = 0;
        int posZ2 = 0;
        int directionX;
        int directionY;
        int directionZ;
        int directionX2;
        int directionY2;
        int directionZ2;
        int directionChange;
        int directionChange2;
        int blocksToUse2;

        for (int blocksMade = 0; blocksMade <= size; ) // make veins
        {
            int posX = start.getX();
            int posY = start.getY();
            int posZ = start.getZ();

            blocksToUse2 = 1 + (size / 30);
            directionChange = rng.nextInt(6);
            directionX = rng.nextInt(2);
            directionY = rng.nextInt(2);
            directionZ = rng.nextInt(2);

            for (int blocksMade1 = 0; blocksMade1 <= blocksToUse2; ) // make branch
            {
                if (directionX == 0 && directionChange != 1)
                    posX = posX + rng.nextInt(2);
                if (directionX == 1 && directionChange != 1)
                    posX = posX - rng.nextInt(2);
                if (directionY == 0 && directionChange != 2)
                    posY = posY + rng.nextInt(2);
                if (directionY == 1 && directionChange != 2)
                    posY = posY - rng.nextInt(2);
                if (directionZ == 0 && directionChange != 3)
                    posZ = posZ + rng.nextInt(2);
                if (directionZ == 1 && directionChange != 3)
                    posZ = posZ - rng.nextInt(2);
                if (rng.nextInt(4) == 0)
                {
                    posX2 = posX2 + rng.nextInt(2);
                    posY2 = posY2 + rng.nextInt(2);
                    posZ2 = posZ2 + rng.nextInt(2);
                    posX2 = posX2 - rng.nextInt(2);
                    posY2 = posY2 - rng.nextInt(2);
                    posZ2 = posZ2 - rng.nextInt(2);
                }
                if (rng.nextInt(3) == 0) // make sub-branch
                {
                    posX2 = posX;
                    posY2 = posY;
                    posZ2 = posZ;
                    directionX2 = rng.nextInt(2);
                    directionY2 = rng.nextInt(2);
                    directionZ2 = rng.nextInt(2);
                    directionChange2 = rng.nextInt(6);
                    if (directionX2 == 0 && directionChange2 != 0)
                        posX2 = posX2 + rng.nextInt(2);
                    if (directionY2 == 0 && directionChange2 != 1)
                        posY2 = posY2 + rng.nextInt(2);
                    if (directionZ2 == 0 && directionChange2 != 2)
                        posZ2 = posZ2 + rng.nextInt(2);
                    if (directionX2 == 1 && directionChange2 != 0)
                        posX2 = posX2 - rng.nextInt(2);
                    if (directionY2 == 1 && directionChange2 != 1)
                        posY2 = posY2 - rng.nextInt(2);
                    if (directionZ2 == 1 && directionChange2 != 2)
                        posZ2 = posZ2 - rng.nextInt(2);

                    for (int blocksMade2 = 0; blocksMade2 <= (1 + (blocksToUse2 / 5)); )
                    {
                        if (directionX2 == 0 && directionChange2 != 0)
                            posX2 = posX2 + rng.nextInt(2);
                        if (directionY2 == 0 && directionChange2 != 1)
                            posY2 = posY2 + rng.nextInt(2);
                        if (directionZ2 == 0 && directionChange2 != 2)
                            posZ2 = posZ2 + rng.nextInt(2);
                        if (directionX2 == 1 && directionChange2 != 0)
                            posX2 = posX2 - rng.nextInt(2);
                        if (directionY2 == 1 && directionChange2 != 1)
                            posY2 = posY2 - rng.nextInt(2);
                        if (directionZ2 == 1 && directionChange2 != 2)
                            posZ2 = posZ2 - rng.nextInt(2);

                        blocksMade++;
                        blocksMade1++;
                        blocksMade2++;

                        final BlockPos pos = new BlockPos(posX2, posY2 + chunkData.getSeaLevelOffset(posX2 & 15, posZ2 & 15), posZ2);
                        final IBlockState current = world.getBlockState(pos);

                        if (!(current.getBlock() instanceof BlockTFCVariant)) continue;

                        final BlockTFCVariant currentBlock = (BlockTFCVariant) current.getBlock();

                        if (currentBlock.type != BlockTFCVariant.Type.RAW || !baseRocks.contains(currentBlock.rock)) continue;

                        world.setBlockState(pos, BlockTFCOre.get(currentBlock.rock, ore, grade));
                    }
                }

                blocksMade++;
                blocksMade1++;

                final BlockPos pos = new BlockPos(posX, posY + chunkData.getSeaLevelOffset(posX & 15, posZ & 15), posZ);
                final IBlockState current = world.getBlockState(pos);

                if (!(current.getBlock() instanceof BlockTFCVariant)) continue;

                final BlockTFCVariant currentBlock = (BlockTFCVariant) current.getBlock();

                if (currentBlock.type != BlockTFCVariant.Type.RAW || !baseRocks.contains(currentBlock.rock)) continue;

                world.setBlockState(pos, BlockTFCOre.get(currentBlock.rock, ore, grade));
            }

            start = start.add(rng.nextInt(3) - 1, rng.nextInt(3) - 1, rng.nextInt(3) - 1);
        }
        TerraFirmaCraft.getLog().info("Vein of {} was placed at {}", ore, start);
    }

    private int mPCalculateDensity(Random rand, int oreDistance, float oreDensity) // returns the density value
    {
        int loopCount;
        int densityValuePassInner;
        int densityValuePass;
        oreDensity = oreDensity * .01F;
        oreDensity = oreDensity * (oreDistance >> 1) + 1F;// establishes number of times to loop
        loopCount = (int) (oreDensity); //stores number of times to loop
        densityValuePassInner = oreDistance / loopCount; // distance devided by number of times it will loop, establishes the number for randomization
        densityValuePassInner += (oreDistance - (densityValuePassInner * loopCount)) / loopCount;
        densityValuePass = 0;
        while (loopCount > 0) // loops to acumulate random values
        {
            densityValuePass = densityValuePass + rand.nextInt(densityValuePassInner); // acumulate randoms
            loopCount = loopCount - 1; // decriment loop
        }
        return densityValuePass; // return proccesed random value
    }
}
