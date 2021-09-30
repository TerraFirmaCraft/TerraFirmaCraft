/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;

import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.world.river.Flow;

public class RiverWaterBlock extends LiquidBlock
{
    public static final EnumProperty<Flow> FLOW = TFCBlockStateProperties.FLOW;

    public RiverWaterBlock(Properties properties)
    {
        super(TFCFluids.RIVER_WATER, properties);

        registerDefaultState(getStateDefinition().any().setValue(LEVEL, 0).setValue(FLOW, Flow.NONE));
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return fluid.defaultFluidState().setValue(FLOW, state.getValue(FLOW));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FLOW));
    }

    @Override
    protected synchronized void initFluidStateCache()
    {
        // No-op
    }
}
