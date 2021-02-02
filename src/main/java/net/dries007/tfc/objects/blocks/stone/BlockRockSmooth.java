/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.stone;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.util.FallingBlockManager;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockRockSmooth extends BlockRockVariant
{
    public static final PropertyBool CAN_FALL = PropertyBool.create("can_fall");

    public BlockRockSmooth(Rock.Type type, Rock rock)
    {
        super(type, rock);

        FallingBlockManager.Specification spec = new FallingBlockManager.Specification(type.getFallingSpecification()); // Copy as each raw stone has an unique resultingState
        FallingBlockManager.registerFallable(this.getDefaultState().withProperty(CAN_FALL, true), spec);

        setDefaultState(getBlockState().getBaseState().withProperty(CAN_FALL, false));
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
