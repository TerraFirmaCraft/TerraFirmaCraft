/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class MossSpreadingWallBlock extends WallBlock
{
    public MossSpreadingWallBlock(Properties properties)
    {
        super(properties.randomTicks());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        MossSpreadingBlock.spreadMoss(worldIn, pos, random);
    }
}
