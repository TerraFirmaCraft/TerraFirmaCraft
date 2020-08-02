/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
public class TEPlacedItem extends TEInventory
{
    public static final int SLOT_LARGE_ITEM = 0;

    public static void convertPitKilnToPlacedItem(World world, BlockPos pos)
    {
        TEPitKiln teOld = Helpers.getTE(world, pos, TEPitKiln.class);
        if (teOld != null)
        {
            // Remove inventory items
            // This happens here to stop the block dropping its items in onBreakBlock()
            IItemHandler capOld = teOld.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            ItemStack[] inventory = new ItemStack[4];
            if (capOld != null)
            {
                for (int i = 0; i < 4; i++)
                {
                    inventory[i] = capOld.extractItem(i, 64, false);
                }
            }
            // Replace the block
            world.setBlockState(pos, BlocksTFC.PLACED_ITEM.getDefaultState());

            // Replace inventory items
            TEPlacedItem teNew = Helpers.getTE(world, pos, TEPlacedItem.class);
            if (teNew != null)
            {
                IItemHandler capNew = teNew.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (capNew != null)
                {
                    for (int i = 0; i < 4; i++)
                    {
                        if (inventory[i] != null && !inventory[i].isEmpty())
                        {
                            capNew.insertItem(i, inventory[i], false);
                        }
                    }
                }
                // Copy misc data
                teNew.isHoldingLargeItem = teOld.isHoldingLargeItem;
            }
        }
    }

    protected boolean isHoldingLargeItem;

    public TEPlacedItem()
    {
        // the capability is used for the main inventory
        super(4);
        this.isHoldingLargeItem = false;
    }

    @Override
    @Nonnull
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(getPos(), getPos().add(1, 1, 1));
    }

    public boolean onRightClick(EntityPlayer player, ItemStack stack, RayTraceResult rayTrace)
    {
        return onRightClick(player, stack, Math.round(rayTrace.hitVec.x) < rayTrace.hitVec.x, Math.round(rayTrace.hitVec.z) < rayTrace.hitVec.z);
    }

    public boolean insertItem(EntityPlayer player, ItemStack stack, RayTraceResult rayTrace)
    {
        boolean x = Math.round(rayTrace.hitVec.x) < rayTrace.hitVec.x;
        boolean z = Math.round(rayTrace.hitVec.z) < rayTrace.hitVec.z;
        final int slot = (x ? 1 : 0) + (z ? 2 : 0);
        return insertItem(player, stack, slot);
    }

    /**
     * @return true if an action was taken (passed back through onItemRightClick)
     */
    public boolean onRightClick(EntityPlayer player, ItemStack stack, boolean x, boolean z)
    {
        final int slot = (x ? 1 : 0) + (z ? 2 : 0);
        if (player.getHeldItem(EnumHand.MAIN_HAND).isEmpty() || player.isSneaking())
        {
            ItemStack current;
            if (isHoldingLargeItem)
            {
                current = inventory.getStackInSlot(SLOT_LARGE_ITEM);
            }
            else
            {
                current = inventory.getStackInSlot(slot);
            }

            // Try and grab the item
            if (!current.isEmpty())
            {
                player.addItemStackToInventory(current.splitStack(1));
                inventory.setStackInSlot(slot, ItemStack.EMPTY);

                // This is set to false no matter what happens earlier
                isHoldingLargeItem = false;

                updateBlock();
                return true;
            }
        }
        else if (!stack.isEmpty())
        {
            return insertItem(player, stack, slot);
        }
        return false;
    }

    public boolean insertItem(EntityPlayer player, ItemStack stack, int slot)
    {
        // Try and insert an item
        // Check the size of item to determine if insertion is possible, or if it requires the large slot
        IItemSize sizeCap = CapabilityItemSize.getIItemSize(stack);
        Size size = Size.NORMAL;
        if (sizeCap != null)
        {
            size = sizeCap.getSize(stack);
        }

        if (size.isSmallerThan(Size.VERY_LARGE) && !isHoldingLargeItem)
        {
            // Normal and smaller can be placed normally
            if (inventory.getStackInSlot(slot).isEmpty())
            {
                ItemStack input;
                if (player.isCreative())
                {
                    input = stack.copy();
                    input.setCount(1);
                }
                else
                {
                    input = stack.splitStack(1);
                }
                inventory.setStackInSlot(slot, input);
                updateBlock();
                return true;
            }
        }
        else if (!size.isSmallerThan(Size.VERY_LARGE)) // Very Large or Huge
        {
            // Large items are placed in the single center slot
            if (isEmpty())
            {
                ItemStack input;
                if (player.isCreative())
                {
                    input = stack.copy();
                    input.setCount(1);
                }
                else
                {
                    input = stack.splitStack(1);
                }
                inventory.setStackInSlot(SLOT_LARGE_ITEM, input);
                isHoldingLargeItem = true;
                updateBlock();
                return true;
            }
        }
        return false;
    }

    public boolean holdingLargeItem()
    {
        return isHoldingLargeItem;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        isHoldingLargeItem = nbt.getBoolean("itemSize");
        super.readFromNBT(nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setBoolean("itemSize", isHoldingLargeItem);
        return super.writeToNBT(nbt);
    }

    protected void updateBlock()
    {
        if (isEmpty())
        {
            world.setBlockToAir(pos);
        }
        else
        {
            markForBlockUpdate();
        }
    }

    protected boolean isEmpty()
    {
        if (isHoldingLargeItem && inventory.getStackInSlot(SLOT_LARGE_ITEM).isEmpty())
        {
            return true;
        }
        for (int i = 0; i < 4; i++)
        {
            if (!inventory.getStackInSlot(i).isEmpty())
            {
                return false;
            }
        }
        return true;
    }
}
