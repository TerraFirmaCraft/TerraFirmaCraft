/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateModel;

public class MeltingBlock extends Block
{
    public MeltingBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if (random.nextFloat() < 0.5f)
        {
            final ClimateModel model = Climate.get(level);
            if (model.getTemperature(level, pos) > 0)
            {
                level.removeBlock(pos, false);
            }
        }
    }
}
