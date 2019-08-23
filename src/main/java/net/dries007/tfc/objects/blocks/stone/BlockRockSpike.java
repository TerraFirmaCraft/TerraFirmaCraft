/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.stone;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.types.Rock;

/**
 * Stalactites and stalagmites in one block!
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockRockSpike extends BlockRockVariant
{
    public static final PropertyBool CEILING = PropertyBool.create("ceiling"); //If this comes from ceiling
    public static final PropertyBool BASE = PropertyBool.create("base"); //If this block is the base

    public BlockRockSpike(Rock.Type type, Rock rock)
    {
        super(type, rock);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(CEILING, meta % 2 == 1).withProperty(BASE, meta >= 2);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(CEILING) ? 1 : 0) + (state.getValue(BASE) ? 2 : 0);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        boolean toUp = true;
        if (state.getValue(CEILING))
        {
            toUp = false;
        }
        if (!state.getValue(BASE))
        {
            toUp = !toUp;
        }
        BlockPos otherPart = toUp ? pos.up() : pos.down();
        if (otherPart.equals(fromPos))
        {
            worldIn.destroyBlock(pos, false);
        }
        else if (state.getValue(BASE) && worldIn.isAirBlock(toUp ? pos.down() : pos.up()))
        {
            worldIn.destroyBlock(pos, false);
            worldIn.destroyBlock(otherPart, false);
        }
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, CEILING, BASE);
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return 0;
    }
}
