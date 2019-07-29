/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.mapgen;

import java.util.Random;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;

import net.dries007.tfc.objects.blocks.BlocksTFC;

import static net.dries007.tfc.world.classic.ChunkGenTFC.*;

/**
 * todo: this is even more of a mess than the other two mapgen classes, cause the generate method is weird.
 */
public class MapGenRiverRavine extends MapGenBase
{
    private final float[] multipliers = new float[256];

    private final int riverRavineRarity;

    public MapGenRiverRavine(int rarity)
    {
        riverRavineRarity = rarity;
        range = 32;
    }

    @Override
    public void generate(World worldIn, int x, int z, ChunkPrimer primer)
    {
        recursiveGenerate(worldIn, x, z, x, z, primer); // todo: wtf?
    }

    @Override
    protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int originalX, int originalZ, ChunkPrimer chunkPrimerIn)
    {
        if (rand.nextInt(riverRavineRarity) != 0) return;
        double x = chunkX * 16 + rand.nextInt(16);
        double y = 80;
        double z = chunkZ * 16 + rand.nextInt(16);
        float angleY = rand.nextFloat() * (float) Math.PI * 2.0F;
        float angleZ = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
        float angleX = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;

        generateRavine(rand.nextLong(), chunkX, chunkZ, chunkPrimerIn, x, y, z, angleX, angleY, angleZ, y);
    }

    protected void generateRavine(long seed, int chunkX, int chunkZ, ChunkPrimer primer, double startX, double startY, double startZ, float angleX, float angleY, float angleZ, double waterHeight)
    {
        final Random rand = new Random(seed);
        final double worldX = chunkX * 16 + 8;
        final double worldZ = chunkZ * 16 + 8;
        float runningZMultiplier = 0.0F;
        float runningYMultiplier = 0.0F;

        final int rounds = (range * 16 - 16) - rand.nextInt((range * 16 - 16) / 4);

        {
            float f = 1.0F + rand.nextFloat() * rand.nextFloat() * 1.0F;
            multipliers[0] = f * f;
            for (int i = 1; i < 256; ++i)
            {
                if (rand.nextInt(3) == 0) f = 1.0F + rand.nextFloat() * rand.nextFloat() * 1.0F;
                this.multipliers[i] = f * f;
            }
        }

        outer:
        for (int round = 0; round < rounds; round++)
        {
            double min = 3.5D + MathHelper.sin(round * (float) Math.PI / rounds) * angleX * 1.0F;
            double max = min * 0.8;
            min *= rand.nextFloat() * 0.25D + 0.75D;
            max *= rand.nextFloat() * 0.25D + 0.75D;
            float cosZ = MathHelper.cos(angleZ);
            float sinZ = MathHelper.sin(angleZ);
            startX += MathHelper.cos(angleY) * cosZ;
            startY += sinZ;
            startZ += MathHelper.sin(angleY) * cosZ;
            angleZ *= 0.7F;
            angleZ += runningZMultiplier * 0.05F;
            angleY += runningYMultiplier * 0.05F;
            runningZMultiplier *= 0.8F;
            runningYMultiplier *= 0.5F;
            runningZMultiplier += (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 2.0F;
            runningYMultiplier += (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 4.0F;

            if (rand.nextInt(4) == 0) continue; // <--Determines the length of the ravine // todo: make setting?

            {
                double xOffset = startX - worldX;
                double zOffset = startZ - worldZ;
                double roundsLeft = rounds - round;
                double radius = angleX + 2.0F + 16.0F;

                if (xOffset * xOffset + zOffset * zOffset - roundsLeft * roundsLeft > radius * radius)
                    return;
            }

            if (!(startX >= worldX - 16.0D - min * 2.0D) || !(startZ >= worldZ - 16.0D - min * 2.0D) || !(startX <= worldX + 16.0D + min * 2.0D) || !(startZ <= worldZ + 16.0D + min * 2.0D))
                continue;

            int xMin = MathHelper.floor(startX - min) - chunkX * 16 - 1;
            int xMax = MathHelper.floor(startX + min) - chunkX * 16 + 1;
            int yMin = MathHelper.floor(startY - max) - 1;
            int yMax = MathHelper.floor(startY + max) + 1;
            int zMin = MathHelper.floor(startZ - min) - chunkZ * 16 - 1;
            int zMax = MathHelper.floor(startZ + min) - chunkZ * 16 + 1;

            if (xMin < 0) xMin = 0;
            if (xMax > 16) xMax = 16;
            if (yMin < 1) yMin = 1;
            if (yMax > 250) yMax = 250;
            if (zMin < 0) zMin = 0;
            if (zMax > 16) zMax = 16;

            for (int x = Math.max(xMin - 1, 0); x < Math.min(xMax + 1, 16); ++x)
            {
                for (int z = Math.max(zMin - 1, 0); z < Math.min(zMax + 1, 16); ++z)
                {
                    for (int y = Math.min(yMax + 1, 250); y >= Math.max(yMin - 2, 1); --y)
                    {
                        if (BlocksTFC.isWater(primer.getBlockState(x, y, z)))
                            continue outer;
                    }
                }
            }

            for (int x = xMin; x < xMax; x++)
            {
                final double xNormalized = (x + chunkX * 16 + 0.5D - startX) / min;
                for (int z = zMin; z < zMax; z++)
                {
                    final double zNormalized = (z + chunkZ * 16 + 0.5D - startZ) / min;
                    if (xNormalized * xNormalized + zNormalized * zNormalized >= 1.0D) continue;

                    for (int y = yMax - 1; y >= yMin; y--)
                    {
                        final double yNormalized = (y + 0.5D - startY) / max;
                        if ((xNormalized * xNormalized + zNormalized * zNormalized) * multipliers[y] + yNormalized * yNormalized / 6.0D >= 1.0D)
                            continue;
                        if (!BlocksTFC.isRawStone(primer.getBlockState(x, y, z)) && !BlocksTFC.isSoil(primer.getBlockState(x, y, z)))
                            continue;

                        if (y < 20/* todo make option, was 10*/)
                            primer.setBlockState(x, y, z, LAVA); // todo: check stability?
                        else if (y < waterHeight) primer.setBlockState(x, y, z, FRESH_WATER);
                        else primer.setBlockState(x, y, z, AIR);
                    }
                }
            }
        }
    }
}
