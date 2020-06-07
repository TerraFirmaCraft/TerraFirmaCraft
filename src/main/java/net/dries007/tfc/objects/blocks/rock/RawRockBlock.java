/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.rock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;

import net.dries007.tfc.objects.blocks.TFCBlockStateProperties;

public class RawRockBlock extends Block
{
    public static final BooleanProperty SUPPORTED = TFCBlockStateProperties.SUPPORTED;

    public RawRockBlock(Properties properties)
    {
        super(properties);

        setDefaultState(stateContainer.getBaseState().with(SUPPORTED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(SUPPORTED);
    }
}
