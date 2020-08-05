/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.stone;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.util.ICollapsableBlock;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockRockSmooth extends BlockRockVariant implements ICollapsableBlock
{
    public static final PropertyBool CAN_FALL = PropertyBool.create("can_fall");

    public BlockRockSmooth(Rock.Type type, Rock rock)
    {
        super(type, rock);

        setDefaultState(getBlockState().getBaseState().withProperty(CAN_FALL, false));
    }

    @Override
    public BlockRockVariantFallable getFallingVariant()
    {
        return (BlockRockVariantFallable) BlockRockVariant.get(rock, Rock.Type.COBBLE);
    }

    @Override
    public boolean canCollapse(World world, BlockPos pos)
    {
        return world.getBlockState(pos).getValue(CAN_FALL) && ICollapsableBlock.super.canCollapse(world, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(CAN_FALL, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(CAN_FALL) ? 1 : 0;
    }

    @Override
    public void onPlayerDestroy(World worldIn, BlockPos pos, IBlockState state)
    {
        if (state.getValue(CAN_FALL))
        {
            checkCollapsingArea(worldIn, pos);
        }
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, CAN_FALL);
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return 0;
    }
}
