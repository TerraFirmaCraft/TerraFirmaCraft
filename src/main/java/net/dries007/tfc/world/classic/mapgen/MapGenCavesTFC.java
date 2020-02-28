/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.mapgen;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraftforge.registries.ForgeRegistry;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.world.classic.DataLayer;

import static net.dries007.tfc.world.classic.ChunkGenTFC.AIR;
import static net.dries007.tfc.world.classic.ChunkGenTFC.LAVA;

/**
 * todo: this is rewritten in 1.14 anyway
 */
public class MapGenCavesTFC extends MapGenBase
{
    private final DataLayer[] stabilityLayer;
    private int[] rockLayer1;
    private float rainfall = 0f;

    public MapGenCavesTFC(DataLayer[] stabilityLayer)
    {
        this.stabilityLayer = stabilityLayer;
    }

    public void setGenerationData(float rainfall, int[] rockLayer1)
    {
        this.rainfall = rainfall;
        this.rockLayer1 = rockLayer1;
    }

    @Override
    protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int originalX, int originalZ, ChunkPrimer primer)
    {
        int runs = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(40) + 1) + 1);
        final int xCoord = chunkX * 16 + this.rand.nextInt(16);
        final int yCoord = this.rand.nextInt(1 + this.rand.nextInt(140)) + 60;
        final int zCoord = chunkZ * 16 + this.rand.nextInt(16);
        final int dlIndex = (zCoord & 15) << 4 | (xCoord & 15);

        double width = 1.5d + rainfall / 500d;
        int caveChance = 30 + (int) (rainfall / 50d);

        width += ((ForgeRegistry<Rock>) TFCRegistries.ROCKS).getValue(rockLayer1[dlIndex]).getRockCategory().getCaveGenMod();
        runs += ((ForgeRegistry<Rock>) TFCRegistries.ROCKS).getValue(rockLayer1[dlIndex]).getRockCategory().getCaveFreqMod();

        if (yCoord < 32) width *= 0.5;
        else if (yCoord < 64) width *= 0.65;
        else if (yCoord < 96) width *= 0.80;
        else if (yCoord < 120) width *= 0.90;
        else width *= 0.5;

        if (this.rand.nextInt(8) == 0) width += 1;
        if (this.rand.nextInt(caveChance) != 0) return;

        for (int i = 0; i < runs; i++)
        {
            int runs2 = 1;
            if (this.rand.nextInt(4) == 0)
            {
                this.generateLargeCaveNode(this.rand.nextLong(), originalX, originalZ, primer, xCoord, yCoord, zCoord);
                runs2 += this.rand.nextInt(4);
            }

            for (int j = 0; j < runs2; j++)
            {
                float d1 = this.rand.nextFloat() * (float) Math.PI * 2.0F;
                float d2 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float d3 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
                if (this.rand.nextInt(10) == 0) d3 *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
                this.generateCaveNode(this.rand.nextLong(), originalX, originalZ, primer, xCoord, yCoord, zCoord, d3, d1, d2, 0, 1.0D, width);
            }
        }
    }

    /**
     * Generates a larger initial cave node than usual. Called 25% of the time.
     */
    protected void generateLargeCaveNode(long seed, int chunkX, int chunkZ, ChunkPrimer primer, double x, double y, double z)
    {
        this.generateCaveNode(seed, chunkX, chunkZ, primer, x, y, z, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, 0.5D, 2.5D);
    }

    /**
     * Generates a node in the current cave system recursion tree.
     */
    protected void generateCaveNode(long seed, int chunkX, int chunkZ, ChunkPrimer primer, double xOffset, double yOffset, double zOffset, float f1, float f2, float f3, int i1, double yRadiusMult, double width)
    {
        final Random rng = new Random(seed);
        final int worldX = chunkX * 16 + 8;
        final int worldZ = chunkZ * 16 + 8;
        float lf1 = 0.0F;
        float lf2 = 0.0F;

        final int rndRange = (this.range * 16 - 16) - rng.nextInt((this.range * 16 - 16) / 4);

        boolean onlyOne = false;
        if (i1 == -1)
        {
            i1 = rndRange / 2;
            onlyOne = true;
        }

        final int rndRange2 = rng.nextInt(rndRange / 2) + rndRange / 4;
        boolean smallRnd = rng.nextInt(6) == 0;
        outer:
        for (; i1 < rndRange; ++i1)
        {
            float var33 = MathHelper.cos(f3);
            float var34 = MathHelper.sin(f3);
            xOffset += MathHelper.cos(f2) * var33;
            yOffset += var34;
            zOffset += MathHelper.sin(f2) * var33;

            f3 *= smallRnd ? 0.92F : 0.7F;
            f3 += lf2 * 0.1F;
            f2 += lf1 * 0.1F;
            lf2 *= 0.9F;
            lf1 *= 0.75F;
            lf2 += (rng.nextFloat() - rng.nextFloat()) * rng.nextFloat() * 2.0F;
            lf1 += (rng.nextFloat() - rng.nextFloat()) * rng.nextFloat() * 4.0F;

            if (!onlyOne && i1 == rndRange2 && f1 > 1.0F && rndRange > 0)
            {
                this.generateCaveNode(rng.nextLong(), chunkX, chunkZ, primer, xOffset, yOffset, zOffset, rng.nextFloat() * 0.5F + 0.5F, f2 - ((float) Math.PI / 2F), f3 / 3.0F, i1, 1.0D, width);
                this.generateCaveNode(rng.nextLong(), chunkX, chunkZ, primer, xOffset, yOffset, zOffset, rng.nextFloat() * 0.5F + 0.5F, f2 + ((float) Math.PI / 2F), f3 / 3.0F, i1, 1.0D, width);
                return;
            }

            double radius = width + MathHelper.sin(i1 * (float) Math.PI / rndRange) * f1 * 1.0F;
            double yRadius = radius * yRadiusMult;
            if (onlyOne || rng.nextInt(4) != 0)
            {
                final double localXOffset = xOffset - worldX;
                final double localZOffset = zOffset - worldZ;
                final double var39 = rndRange - i1;
                final double var41 = f1 + 2.0F + 16.0F;

                if (localXOffset * localXOffset + localZOffset * localZOffset - var39 * var39 > var41 * var41)
                    return;

                if (!(xOffset >= worldX - 16.0D - radius * 2.0D) || !(zOffset >= worldZ - 16.0D - radius * 2.0D) || !(xOffset <= worldX + 16.0D + radius * 2.0D) || !(zOffset <= worldZ + 16.0D + radius * 2.0D))
                    continue;

                int initialX = MathHelper.floor(xOffset - radius) - chunkX * 16 - 1;
                int maxX = MathHelper.floor(xOffset + radius) - chunkX * 16 + 1;

                int minY = MathHelper.floor(yOffset - yRadius) - 1;
                int initialY = MathHelper.floor(yOffset + yRadius) + 1;

                int initialZ = MathHelper.floor(zOffset - radius) - chunkZ * 16 - 1;
                int maxZ = MathHelper.floor(zOffset + radius) - chunkZ * 16 + 1;

                if (initialX < 0) initialX = 0;
                if (maxX > 16) maxX = 16;
                if (minY < 1) minY = 1;
                if (initialY > 250) initialY = 250;
                if (initialZ < 0) initialZ = 0;
                if (maxZ > 16) maxZ = 16;

                for (int xCoord = Math.max(initialX - 1, 0); xCoord < Math.min(maxX + 1, 16); ++xCoord)
                {
                    for (int zCoord = Math.max(initialZ - 1, 0); zCoord < Math.min(maxZ + 1, 16); ++zCoord)
                    {
                        for (int yCoord = Math.min(initialY + 1, 250); yCoord > Math.max(minY - 1, 0); --yCoord)
                        {
                            if (BlocksTFC.isWater(primer.getBlockState(xCoord, yCoord, zCoord)))
                                continue outer;
                        }
                    }
                }

                for (int xCoord = initialX; xCoord < maxX; ++xCoord)
                {
                    final double xDistNorm = (xCoord + chunkX * 16 + 0.5D - xOffset) / radius;
                    for (int zCoord = initialZ; zCoord < maxZ; ++zCoord)
                    {
                        final double zDistNorm = (zCoord + chunkZ * 16 + 0.5D - zOffset) / radius;
//                            int index = (xCoord * 16 + zCoord) * 256 + initialY;

                        if (xDistNorm * xDistNorm + zDistNorm * zDistNorm >= 1.0D)
                            continue;

                        IBlockState grass = null;

                        for (int y = initialY - 1; y >= minY; y--)
                        {
                            double yNorm = (y + 0.5D - yOffset) / yRadius;
                            if (!(yNorm > -0.7D) || !(xDistNorm * xDistNorm + yNorm * yNorm + zDistNorm * zDistNorm < 1.0D))
                                continue;

                            final IBlockState current = primer.getBlockState(xCoord, y, zCoord);

                            if (!BlocksTFC.isSoil(current) && !BlocksTFC.isRawStone(current)) continue;

                            if (BlocksTFC.isGrass(current)) grass = primer.getBlockState(xCoord, y, zCoord);

                            for (int upCount = 1; BlocksTFC.isSoilOrGravel(primer.getBlockState(xCoord, y + upCount, zCoord)); upCount++)
                                primer.setBlockState(xCoord, y + upCount, zCoord, AIR);


                            if (y < 20 /* todo make option? was 10*/ && stabilityLayer[(worldZ & 15) << 4 | (worldX & 15)].valueInt == 1)
                            {
                                primer.setBlockState(xCoord, y, zCoord, LAVA);
                            }
                            else
                            {
                                primer.setBlockState(xCoord, y, zCoord, AIR);
                                if (grass != null && BlocksTFC.isDirt(primer.getBlockState(xCoord, y - 1, zCoord)))
                                {
                                    primer.setBlockState(xCoord, y - 1, zCoord, grass);
                                }
                            }
                        }
                    }
                }
                if (onlyOne) break;
            }
        }
    }
}