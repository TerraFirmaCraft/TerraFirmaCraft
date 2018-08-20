/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import static net.dries007.tfc.world.classic.ChunkGenTFC.FRESH_WATER;

public class WorldGenWaterlilyTFC extends WorldGenerator
{
    @Override
    public boolean generate(final World worldIn, final Random rand, final BlockPos position)
    {
        for (int i = 0; i < 10; ++i)
        {
            final BlockPos p2 = position.add(rand.nextInt(8) - rand.nextInt(8),
                rand.nextInt(4) - rand.nextInt(4),
                rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(p2) && Blocks.WATERLILY.canPlaceBlockAt(worldIn, p2) &&
                worldIn.getBlockState(p2.add(0, -1, 0)) == FRESH_WATER &&
                worldIn.getBlockState(p2.add(0, -2, 0)) != FRESH_WATER) // todo: make this a little less harsh
            {
                worldIn.setBlockState(p2, Blocks.WATERLILY.getDefaultState(), 0x02);
            }
        }

        return true;
    }
}
