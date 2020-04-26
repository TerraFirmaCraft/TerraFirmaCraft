/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.VanillaDoubleChestItemHandler;

import net.dries007.tfc.objects.te.TEChestTFC;

@SuppressWarnings("WeakerAccess")
public class TFCDoubleChestItemHandler extends VanillaDoubleChestItemHandler
{
    @Nullable
    public static VanillaDoubleChestItemHandler get(TileEntityChest chest)
    {
        World world = chest.getWorld();
        BlockPos pos = chest.getPos();
        //noinspection ConstantConditions
        if (world == null || pos == null || !world.isBlockLoaded(pos))
            return null; // Still loading

        Block blockType = chest.getBlockType();

        EnumFacing[] horizontals = EnumFacing.HORIZONTALS;
        for (int i = horizontals.length - 1; i >= 0; i--)   // Use reverse order so we can return early
        {
            EnumFacing enumfacing = horizontals[i];
            BlockPos blockpos = pos.offset(enumfacing);
            Block block = world.getBlockState(blockpos).getBlock();

            if (block == blockType)
            {
                TileEntity otherTE = world.getTileEntity(blockpos);

                if (otherTE instanceof TileEntityChest)
                {
                    TileEntityChest otherChest = (TileEntityChest) otherTE;
                    return new TFCDoubleChestItemHandler(chest, otherChest,
                        enumfacing != EnumFacing.WEST && enumfacing != EnumFacing.NORTH);

                }
            }
        }
        return NO_ADJACENT_CHESTS_INSTANCE; //All alone
    }

    public TFCDoubleChestItemHandler(@Nullable TileEntityChest mainChest, @Nullable TileEntityChest other, boolean mainChestIsUpper)
    {
        super(mainChest, other, mainChestIsUpper);
    }

    @Override
    public int getSlots()
    {
        return TEChestTFC.SIZE * 2;
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot)
    {
        boolean accessingUpperChest = slot < TEChestTFC.SIZE;
        int targetSlot = accessingUpperChest ? slot : slot - TEChestTFC.SIZE;
        TileEntityChest chest = getChest(accessingUpperChest);
        return chest != null ? chest.getStackInSlot(targetSlot) : ItemStack.EMPTY;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        boolean accessingUpperChest = slot < TEChestTFC.SIZE;
        int targetSlot = accessingUpperChest ? slot : slot - TEChestTFC.SIZE;
        TileEntityChest chest = getChest(accessingUpperChest);
        if (chest != null)
        {
            IItemHandler singleHandler = chest.getSingleChestHandler();
            if (singleHandler instanceof IItemHandlerModifiable)
            {
                ((IItemHandlerModifiable) singleHandler).setStackInSlot(targetSlot, stack);
            }
        }

        chest = getChest(!accessingUpperChest);
        if (chest != null)
        {
            chest.markDirty();
        }
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        boolean accessingUpperChest = slot < TEChestTFC.SIZE;
        int targetSlot = accessingUpperChest ? slot : slot - TEChestTFC.SIZE;
        TileEntityChest chest = getChest(accessingUpperChest);
        if (chest == null)
        {
            return stack;
        }
        if (chest instanceof ISlotCallback && !((ISlotCallback) chest).isItemValid(slot, stack))
        {
            return stack;
        }

        int starting = stack.getCount();
        ItemStack ret = chest.getSingleChestHandler().insertItem(targetSlot, stack, simulate);
        if (ret.getCount() != starting && !simulate)
        {
            chest = getChest(!accessingUpperChest);
            if (chest != null)
            {
                chest.markDirty();
            }
        }

        return ret;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        boolean accessingUpperChest = slot < TEChestTFC.SIZE;
        int targetSlot = accessingUpperChest ? slot : slot - TEChestTFC.SIZE;
        TileEntityChest chest = getChest(accessingUpperChest);
        if (chest == null)
        {
            return ItemStack.EMPTY;
        }

        ItemStack ret = chest.getSingleChestHandler().extractItem(targetSlot, amount, simulate);
        if (!ret.isEmpty() && !simulate)
        {
            chest = getChest(!accessingUpperChest);
            if (chest != null)
            {
                chest.markDirty();
            }
        }

        return ret;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        boolean accessingUpperChest = slot < TEChestTFC.SIZE;
        //noinspection ConstantConditions
        return getChest(accessingUpperChest).getInventoryStackLimit();
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        boolean accessingUpperChest = slot < TEChestTFC.SIZE;
        int targetSlot = accessingUpperChest ? slot : slot - TEChestTFC.SIZE;
        TileEntityChest chest = getChest(accessingUpperChest);
        if (chest != null)
        {
            return chest.getSingleChestHandler().isItemValid(targetSlot, stack);
        }
        return true;
    }
}
