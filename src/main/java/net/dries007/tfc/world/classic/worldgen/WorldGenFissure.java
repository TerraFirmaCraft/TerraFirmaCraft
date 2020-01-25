/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.*;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

import static net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC.getRock3;

/**
 * Rewrite on fissure generation logic
 * *EXPERIMENTAL* Needs more tweaking in rock placement
 */
public class WorldGenFissure implements IWorldGenerator
{
    private final IBlockState fillBlock;
    private final boolean checkStability;

    public WorldGenFissure(boolean lava)
    {
        fillBlock = lava ? ChunkGenTFC.LAVA : ChunkGenTFC.HOT_WATER;
        checkStability = lava;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        BlockPos start = new ChunkPos(chunkX, chunkZ).getBlock(random.nextInt(14) + 9, 0, random.nextInt(14) + 9);
        Biome biome = world.getBiome(start);

        if (biome == BiomesTFC.BEACH || biome == BiomesTFC.OCEAN || biome == BiomesTFC.GRAVEL_BEACH || biome == BiomesTFC.LAKE || biome == BiomesTFC.RIVER || biome == BiomesTFC.DEEP_OCEAN)
        {
            return;
        }

        start = world.getTopSolidOrLiquidBlock(start).down(3);

        final boolean stable = ChunkDataTFC.isStable(world, start);
        if (checkStability && stable)
        {
            return;
        }

        final IBlockState rock = BlockRockVariant.get(getRock3(world, start), Rock.Type.RAW).getDefaultState();

        int depth = 2 + random.nextInt(3);
        int radius = 1 + random.nextInt(2);
        List<BlockPos> clearing = getCircle(start, radius + 2);

        // Checks for water bodies above fissure
        for (int y = 1; y < 4; y++)
        {
            for (BlockPos pos : clearing)
            {
                IBlockState block = world.getBlockState(pos.up(y));
                if (BlocksTFC.isWater(block) && !BlocksTFC.isGround(block)) return;
            }
        }

        // No water bodies found, let's create a fissure by first clearing blocks on the ground!
        for (int y = 1; y < 4; y++)
        {
            for (BlockPos clear : clearing)
            {
                world.setBlockToAir(clear.up(y));
            }
        }

        // Actually fills the fissure
        Set<BlockPos> blocks = getCollapseSet(random, start, radius, depth);
        for (BlockPos filling : blocks)
        {
            smartFill(world, filling, blocks, rock, fillBlock);
        }

        // This is an experimental way of fixing "missing" rocks
        // I disabled it because the looks is more man-made
        // Replaces the blocks not filled by water/lava
        /*
        for(int y = 0; y <= depth + 1; y++)
        {
            for (BlockPos clear : clearing) //Using the same circle we used to clear blocks
            {
                BlockPos replace = clear.down(y);
                if(!world.isAirBlock(replace) && world.getBlockState(replace) != fillBlock)
                {
                    world.setBlockState(replace, rock);
                }
            }
        }*/
    }

    /**
     * Gives a list of block positions for a circle.
     * Used to clear the blocks above the fissure
     *
     * @param center the center block
     * @param radius the radius
     * @return ArrayList of blockPos for a circle
     */
    private List<BlockPos> getCircle(BlockPos center, int radius)
    {
        List<BlockPos> list = new ArrayList<>();
        double rSq = Math.pow(radius, 2);
        for (int x = -radius + center.getX(); x <= +radius + center.getX(); x++)
        {
            for (int z = -radius + center.getZ(); z <= +radius + center.getZ(); z++)
            {
                if (Math.pow(x - center.getX(), 2) + Math.pow(z - center.getZ(), 2) <= rSq)
                {
                    list.add(new BlockPos(x, center.getY(), z));
                }
            }
        }
        return list;
    }

    /**
     * Cylinder like fissure.
     *
     * @param random the Random obj from generate to keep it procedural
     * @param center the top-center of the cylinder
     * @param radius the radius of the circle
     * @param depth  the depth of this fissure
     * @return a Set containing block pos to fill with hot water/lava
     */
    private Set<BlockPos> getCollapseSet(Random random, BlockPos center, int radius, int depth)
    {
        int maxOffset = 2 + random.nextInt(radius);
        Set<BlockPos> blocks = new HashSet<>();
        for (int y = 0; y < depth; y++)
        {
            BlockPos centerHeight = center.down(y);
            double rSq = Math.pow(radius, 2);
            for (int x = -radius + centerHeight.getX(); x <= +radius + centerHeight.getX(); x++)
            {
                for (int z = -radius + centerHeight.getZ(); z <= +radius + centerHeight.getZ(); z++)
                {
                    if (Math.pow(x - centerHeight.getX(), 2) + Math.pow(z - centerHeight.getZ(), 2) <= rSq)
                    {
                        BlockPos b = new BlockPos(x, centerHeight.getY(), z);
                        if (random.nextFloat() < 0.65f)
                        {
                            blocks.add(b);
                            for (EnumFacing facing : EnumFacing.VALUES)
                            {
                                if (facing == EnumFacing.UP) continue;
                                int off = 0;
                                while (off < maxOffset && random.nextFloat() < 0.35f)
                                {
                                    off++;
                                    blocks.add(b.offset(facing));
                                }
                            }
                        }
                    }
                }
            }
        }
        // Now, let's make a "tunnel" all way down so this is gonna look a bit more like a fissure
        int tunnelDepth = depth + 20 + random.nextInt(60);
        int tunnelY = center.down(tunnelDepth).getY();
        if (tunnelY < 20) tunnelY = 20;
        BlockPos tunnelPos = center.down(depth);
        blocks.add(tunnelPos);
        radius = 8;
        while (tunnelPos.getY() > tunnelY)
        {
            int value = random.nextInt(8); // 50% down, 12.5% each side
            if (value < 1)
            {
                tunnelPos = tunnelPos.offset(EnumFacing.NORTH);
            }
            else if (value < 2)
            {
                tunnelPos = tunnelPos.offset(EnumFacing.SOUTH);
            }
            else if (value < 3)
            {
                tunnelPos = tunnelPos.offset(EnumFacing.EAST);
            }
            else if (value < 4)
            {
                tunnelPos = tunnelPos.offset(EnumFacing.WEST);
            }
            else
            {
                tunnelPos = tunnelPos.down();
            }
            // Keep it under control
            if (tunnelPos.getX() > center.getX() + radius)
            {
                tunnelPos = tunnelPos.add(-1, 0, 0);
            }
            if (tunnelPos.getX() < center.getX() - radius)
            {
                tunnelPos = tunnelPos.add(1, 0, 0);
            }
            if (tunnelPos.getZ() > center.getZ() + radius)
            {
                tunnelPos = tunnelPos.add(0, 0, -1);
            }
            if (tunnelPos.getZ() < center.getZ() - radius)
            {
                tunnelPos = tunnelPos.add(0, 0, 1);
            }
            blocks.add(tunnelPos);
            for (EnumFacing horiz : EnumFacing.HORIZONTALS)
            {
                blocks.add(tunnelPos.offset(horiz));
            }
        }
        return blocks;
    }

    // A bit smarter fill, try to not fill the "insides" with rock
    // Needs more tweaking
    private void smartFill(World world, BlockPos pos, Set<BlockPos> fillBlockPos, IBlockState rock, IBlockState fillBlock)
    {
        world.setBlockState(pos, fillBlock);
        for (EnumFacing facing : EnumFacing.VALUES)
        {
            if (facing == EnumFacing.UP) continue;
            if (world.getBlockState(pos.offset(facing)) == fillBlock) continue;
            BlockPos rockPos = pos.offset(facing);
            int filledBlocks = 0;
            for (EnumFacing facing2 : EnumFacing.VALUES)
            {
                BlockPos facingPos = rockPos.offset(facing2);
                if (fillBlockPos.contains(facingPos))
                {
                    filledBlocks++;
                }
            }
            if (filledBlocks < 3)
            {
                world.setBlockState(rockPos, rock);
            }
            else
            {
                world.setBlockState(rockPos, fillBlock);
            }
        }
    }
}
