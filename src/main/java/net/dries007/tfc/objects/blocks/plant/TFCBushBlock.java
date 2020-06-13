/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plant;

import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.Tags;

import net.dries007.tfc.objects.TFCTags;

public class TFCBushBlock extends BushBlock
{
    public TFCBushBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return super.isValidGround(state, worldIn, pos) || TFCTags.Blocks.GRASS.contains(state.getBlock()) || Tags.Blocks.DIRT.contains(state.getBlock());
    }
}
