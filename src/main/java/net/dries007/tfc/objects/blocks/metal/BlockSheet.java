/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.blocks.metal;

import java.util.EnumMap;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.Metal;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockSheet extends Block
{
    public static final PropertyDirection FACE = PropertyDirection.create("face");
    private static final EnumMap<Metal, BlockSheet> MAP = new EnumMap<>(Metal.class);

    public static BlockSheet get(Metal metal)
    {
        return MAP.get(metal);
    }

    public static ItemStack get(Metal metal, int amount)
    {
        return new ItemStack(MAP.get(metal), amount);
    }

    public final Metal metal;

    public BlockSheet(Metal metal)
    {
        super(Material.IRON);

        this.metal = metal;
        if (MAP.put(metal, this) != null) throw new IllegalStateException("There can only be one.");

        this.setDefaultState(blockState.getBaseState().withProperty(FACE, EnumFacing.NORTH));
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACE, EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACE).getIndex();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return FULL_BLOCK_AABB; // todo: proper bounding box
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACE);
    }
}
