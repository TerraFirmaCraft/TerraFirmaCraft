/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.world.classic.WorldTypeTFC;

public class WorldGenFalls implements IWorldGenerator
{
    private final IBlockState block;
    private final int rarity;

    public WorldGenFalls(IBlockState blockIn, int rarity)
    {
        this.block = blockIn;
        this.rarity = rarity;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        for (int k5 = 0; k5 < rarity; ++k5)
        {
            int x = random.nextInt(16) + 8;
            int z = random.nextInt(16) + 8;
            int y = random.nextInt(WorldTypeTFC.SEALEVEL - 50) + 30;
            BlockPos pos = new BlockPos(chunkX << 4, y, chunkZ << 4).add(x, 0, z);
            if (!BlocksTFC.isRawStone(world.getBlockState(pos.down())) && !BlocksTFC.isRawStone(world.getBlockState(pos.up())) && (!BlocksTFC.isRawStone(world.getBlockState(pos)) || !world.isAirBlock(pos)))
            {
                continue;
            }
            int rawHorizontal = 0, airHorizontal = 0;
            for (EnumFacing facing : EnumFacing.HORIZONTALS)
            {
                if (world.isAirBlock(pos.offset(facing)))
                {
                    airHorizontal++;
                }
                else if (BlocksTFC.isRawStone(world.getBlockState(pos.offset(facing))))
                {
                    rawHorizontal++;
                }
                if (airHorizontal > 1) break;
            }
            if (rawHorizontal == 3 && airHorizontal == 1)
            {
                world.setBlockState(pos, block, 2);
                world.immediateBlockTick(pos, block, random);
            }
        }
    }
}