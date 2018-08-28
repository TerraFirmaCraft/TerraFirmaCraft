/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.blocks.wood.BlockChestTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@MethodsReturnNonnullByDefault
public class TEChestTFC extends TileEntityChest
{
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "chest");
    public static final int SIZE = 18;

    private Tree cachedWood;

    {
        chestContents = NonNullList.withSize(SIZE, ItemStack.EMPTY); // todo: make chest size configurable.
    }

    @Override
    public BlockChestTFC getBlockType()
    {
        Block block = super.getBlockType();
        if (!(block instanceof BlockChestTFC))
            throw new IllegalArgumentException("Block type is invalid; must be instance of BlockChestTFC");
        return ((BlockChestTFC) block);
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
            if (world == null)
                return null;
            cachedWood = getBlockType().wood;
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
}
