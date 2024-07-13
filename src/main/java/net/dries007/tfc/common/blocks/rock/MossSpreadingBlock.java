/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.config.TFCConfig;

public class MossSpreadingBlock extends Block
{
    public static void spreadMoss(Level world, BlockPos pos, RandomSource random)
    {
        if (world.isAreaLoaded(pos, 5) && TFCConfig.SERVER.enableMossyRockSpreading.get() && random.nextInt(TFCConfig.SERVER.mossyRockSpreadRate.get()) == 0)
        {
            final BlockPos targetPos = pos.offset(random.nextInt(4) - random.nextInt(4), random.nextInt(4) - random.nextInt(4), random.nextInt(4) - random.nextInt(4));
            final BlockState targetState = world.getBlockState(targetPos);
            if (targetState.getBlock() instanceof IMossGrowingBlock block)
            {
                block.convertToMossy(world, targetPos, targetState, true);
            }
        }
    }

    public MossSpreadingBlock(Properties properties)
    {
        super(properties.randomTicks());
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        MossSpreadingBlock.spreadMoss(level, pos, random);
    }
}
