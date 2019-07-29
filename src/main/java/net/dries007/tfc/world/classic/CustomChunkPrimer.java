/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

public class CustomChunkPrimer extends ChunkPrimer
{
    private static final IBlockState DEFAULT_STATE = Blocks.AIR.getDefaultState();
    private final IBlockState[] data = new IBlockState[65536];

    @Override
    @Nonnull
    public IBlockState getBlockState(int x, int y, int z)
    {
        IBlockState iblockstate = data[x << 12 | z << 8 | y];
        return iblockstate == null ? DEFAULT_STATE : iblockstate;
    }

    @Override
    public void setBlockState(int x, int y, int z, @Nonnull IBlockState state)
    {
        data[x << 12 | z << 8 | y] = state;
    }

    @Override
    public int findGroundBlockIdx(int x, int z)
    {
        int i = (x << 12 | z << 8) + 256 - 1;

        for (int j = 255; j >= 0; --j)
        {
            IBlockState iblockstate = data[i + j];
            if (iblockstate != null && iblockstate != DEFAULT_STATE)
            {
                return j;
            }
        }
        return 0;
    }

    public boolean isEmpty(int x, int y, int z)
    {
        return data[x << 12 | z << 8 | y] == null;
    }
}
