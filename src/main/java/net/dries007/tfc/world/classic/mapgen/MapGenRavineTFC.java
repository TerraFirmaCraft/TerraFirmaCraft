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

import static net.dries007.tfc.world.classic.ChunkGenTFC.AIR;
import static net.dries007.tfc.world.classic.ChunkGenTFC.LAVA;

/**
 * todo: clean up. This needs to be simplified a lot, or split up in functions with sensible variable names.
 */
public class MapGenRavineTFC extends MapGenBase
{
    private final float[] multipliers = new float[256];
    private final int height;
    private final int variability;
    private final int ravineRarity;

    public MapGenRavineTFC(int rarity, int h, int v)
    {
        height = h;
        variability = v;
        ravineRarity = rarity;
    }

    @Override
    protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int originalX, int originalZ, ChunkPrimer primer)
    {
        if (ravineRarity > 0 && rand.nextInt(ravineRarity) == 0)
        {
            double startX = chunkX * 16 + rand.nextInt(16);
            double startY = rand.nextInt(variability) + height;
            double startZ = chunkZ * 16 + rand.nextInt(16);
            float angleY = rand.nextFloat() * (float) Math.PI * 2.0F;
            float angleZ = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float angleX = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;
            double scaleY = 1.2 + rand.nextFloat() + rand.nextFloat();
            generateRavine(rand.nextLong(), originalX, originalZ, primer, startX, startY, startZ, angleX, angleY, angleZ, scaleY);
        }
    }

    private void generateRavine(long seed, int chunkX, int chunkZ, ChunkPrimer primer, double xCoord, double yCoord, double zCoord, float angleX, float angleY, float angleZ, double yScale)
    {
        final Random rng = new Random(seed);
        final double chunkMidX = chunkX * 16 + 8;
        final double chunkMidZ = chunkZ * 16 + 8;
        float runningYMultiplier = 0.0F;
        float runningZMultiplier = 0.0F;

        final int rounds = (range * 16 - 16) - rng.nextInt((range * 16 - 16) / 4);

        {
            float f = 1.0F + rng.nextFloat() * rng.nextFloat() * 1.0F;
            multipliers[0] = f * f;
            for (int i = 1; i < 256; i++)
            {
                if (rng.nextInt(3) == 0) f = 1.0F + rng.nextFloat() * rng.nextFloat() * 1.0F;
                multipliers[i] = f * f;
            }
        }

        outer:
        for (int round = 0; round < rounds; ++round)
        {
            final double min = (1.5D + MathHelper.sin(round * (float) Math.PI / rounds) * angleX * 1.0F) * rng.nextFloat() * 0.25D + 0.75D;
            final double max = (min * yScale) * rng.nextFloat() * 0.25D + 0.75D;
            final float cosZ = MathHelper.cos(angleZ);
            final float sinZ = MathHelper.sin(angleZ);
            xCoord += MathHelper.cos(angleY) * cosZ;
            yCoord += sinZ;
            zCoord += MathHelper.sin(angleY) * cosZ;
            angleZ *= 0.7F;
            angleY += runningYMultiplier * 0.05F;
            angleZ += runningZMultiplier * 0.05F;
            runningZMultiplier *= 0.8F;
            runningYMultiplier *= 0.5F;
            runningZMultiplier += (rng.nextFloat() - rng.nextFloat()) * rng.nextFloat() * 2.0F;
            runningYMultiplier += (rng.nextFloat() - rng.nextFloat()) * rng.nextFloat() * 4.0F;

            if (rng.nextInt(4) == 0) continue; // <--Determines the length of the ravine // todo: make setting?

            {
                final double xOffset = xCoord - chunkMidX;
                final double zOffset = zCoord - chunkMidZ;
                final double roundsLeft = rounds - round;
                final double radius = angleX + 2.0F + 16.0F;

                if (xOffset * xOffset + zOffset * zOffset - roundsLeft * roundsLeft > radius * radius) return;
            }

            if (!(xCoord >= chunkMidX - 16.0D - min * 2.0D &&
                zCoord >= chunkMidZ - 16.0D - min * 2.0D &&
                xCoord <= chunkMidX + 16.0D + min * 2.0D &&
                zCoord <= chunkMidZ + 16.0D + min * 2.0D))
                continue;

            int xMin = MathHelper.floor(xCoord - min) - chunkX * 16 - 1;
            int xMax = MathHelper.floor(xCoord + min) - chunkX * 16 + 1;
            int yMin = MathHelper.floor(yCoord - max) - 1;
            int yMax = MathHelper.floor(yCoord + max) + 1;
            int zMin = MathHelper.floor(zCoord - min) - chunkZ * 16 - 1;
            int zMax = MathHelper.floor(zCoord + min) - chunkZ * 16 + 1;

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

            for (int x = xMin; x < xMax; ++x)
            {
                final double xNormalized = (x + chunkX * 16 + 0.5D - xCoord) / min;

                for (int z = zMin; z < zMax; ++z)
                {
                    final double zNormalized = (z + chunkZ * 16 + 0.5D - zCoord) / min;

                    if (xNormalized * xNormalized + zNormalized * zNormalized >= 1.0D) continue;

                    for (int y = yMax - 1; y >= yMin; --y)
                    {
                        final double yNormalized = (y + 0.5D - yCoord) / max;

                        if (!((xNormalized * xNormalized + zNormalized * zNormalized) * multipliers[y] + yNormalized * yNormalized / 6.0D < 1.0D))
                            continue;
                        if (!BlocksTFC.isGround(primer.getBlockState(x, y, z))) continue;

                        for (int upCount = 1; BlocksTFC.isSoilOrGravel(primer.getBlockState(x, y + upCount, z)); upCount++)
                            primer.setBlockState(x, y + upCount, z, AIR);

                        primer.setBlockState(x, y, z, y < 20 /*todo: make option, was 10*/ ? LAVA : AIR); // todo: check stability?
                    }
                }
            }
        }
    }
}
