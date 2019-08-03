/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.blocks.wood.BlockChestTFC;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TEChestTFC extends TileEntityChest
{
    public static final int SIZE = 18;

    private Tree cachedWood;

    {
        chestContents = NonNullList.withSize(SIZE, ItemStack.EMPTY); // todo: make chest size configurable.
    }

    @Override
    @SideOnly(Side.CLIENT)
    @Nonnull
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(getPos().add(-1, 0, -1), getPos().add(2, 2, 2));
    }

    @Nullable
    public Tree getWood()
    {
        if (cachedWood == null)
        {
            if (world != null)
            {
                cachedWood = ((BlockChestTFC) world.getBlockState(pos).getBlock()).wood;
            }
        }
        return cachedWood;
    }

    @Override
    public int getSizeInventory()
    {
        return SIZE;
    }

    @Override
    protected boolean isChestAt(@Nonnull BlockPos posIn)
    {
        if (world == null) return false;

        Block block = this.world.getBlockState(posIn).getBlock();
        return block instanceof BlockChestTFC && ((BlockChestTFC) block).wood == getWood() && ((BlockChest) block).chestType == getChestType();
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }
}
