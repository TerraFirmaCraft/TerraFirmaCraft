/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks.rock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;

import net.minecraft.block.Block.Properties;

public class RawRockBlock extends Block
{
    public static final BooleanProperty SUPPORTED = TFCBlockStateProperties.SUPPORTED;

    public RawRockBlock(Properties properties)
    {
        super(properties);

        registerDefaultState(stateDefinition.any().setValue(SUPPORTED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(SUPPORTED);
    }
}