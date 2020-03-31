/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.inventory.capability.ISlotCallback;
import net.dries007.tfc.objects.items.metal.ItemMetalJavelin;
import net.dries007.tfc.objects.items.rock.ItemRockJavelin;
import net.dries007.tfc.util.OreDictionaryHelper;

@ParametersAreNonnullByDefault
public class ItemQuiver extends ItemTFC
{
    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote && !playerIn.isSneaking())
        {
            TFCGuiHandler.openGui(worldIn, playerIn, TFCGuiHandler.Type.QUIVER);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public boolean canStack(ItemStack stack)
    {
        return false;
    }

    @Nonnull
    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.NORMAL;
    }

    @Nonnull
    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.HEAVY;
    }

    static ItemStack findQuiver(InventoryPlayer playerInv)
    {
        for (int i = 0; i < playerInv.mainInventory.size(); i++)
        {
            ItemStack cur = playerInv.mainInventory.get(i);
            if (cur.getItem() instanceof ItemQuiver)
            {
                return cur;
            }
        }
        return null;
    }

    public static void replenishJavelin(InventoryPlayer playerInv)
    {
        ItemStack quiver = findQuiver(playerInv);
        if (quiver != null)
        {
            IItemHandler quiverCapability = quiver.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (quiverCapability instanceof ItemQuiver.QuiverCapability)
            {
                ItemStack newJav = ((ItemQuiver.QuiverCapability) quiverCapability).findJavelin();
                if (newJav != null)
                {
                    playerInv.setInventorySlotContents(playerInv.currentItem, newJav);
                }
            }
        }
    }

    //following ItemBow.findAmmo
    static ItemStack findAmmoNotMatching(EntityPlayer player, ItemStack match)
    {
        if (newArrows(player.getHeldItem(EnumHand.OFF_HAND), match))
        {
            return player.getHeldItem(EnumHand.OFF_HAND);
        }
        else if (newArrows(player.getHeldItem(EnumHand.MAIN_HAND), match))
        {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        }
        else
        {
            for(int i = 0; i < player.inventory.getSizeInventory(); ++i)
            {
                ItemStack itemstack = player.inventory.getStackInSlot(i);
                if (newArrows(itemstack, match))
//                    player.inventory.getSlotFor(match) != i)
                {
                    return itemstack;
                }
            }
            return ItemStack.EMPTY;
        }
    }

    static boolean newArrows(ItemStack arrow1, ItemStack arrow2)
    {
        return arrow1.getItem() instanceof ItemArrow && !arrow1.equals(arrow2); //yes, I really mean the same stack
    }

    public static void replenishArrows(EntityPlayer player)
    {
        //current arrow stack (if more than one arrow, return)
        ItemStack curArrows = findAmmoNotMatching(player, ItemStack.EMPTY);
        if (curArrows.getCount() > 1) // ItemBow shrinks stack by 1
        {
            return;
        }

        //additional arrow stack (if found, return)
        ItemStack nextArrows = findAmmoNotMatching(player, curArrows);
        if (nextArrows != ItemStack.EMPTY) {
            return;
        }

        //empty destination slot for arrow stack from quiver since we're called
        //before curArrows is deleted by ItemBow.onPlayerStoppedUsing
        int empty = player.inventory.getFirstEmptyStack();

        if (empty > 0 )
        {
            ItemStack quiver = findQuiver(player.inventory);
            if (quiver != null) {
                IItemHandler quiverCapability = quiver.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (quiverCapability instanceof ItemQuiver.QuiverCapability)
                {
                    ItemStack newArrows = ((ItemQuiver.QuiverCapability) quiverCapability).findArrows();
                    if (newArrows != null)
                    {
                        player.inventory.setInventorySlotContents(empty, newArrows);
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new QuiverCapability(nbt);
    }

    // Extends ItemStackHandler for ease of use.
    public class QuiverCapability extends ItemStackHandler implements ICapabilityProvider, ISlotCallback
    {

        QuiverCapability(@Nullable NBTTagCompound nbt)
        {
            super(8);

            if (nbt != null)
            {
                deserializeNBT(nbt);
            }
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            return hasCapability(capability, facing) ? (T) this : null;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack)
        {
            return OreDictionaryHelper.doesStackMatchOre(stack, "javelin") ||
                   stack.getItem() instanceof ItemArrow;
        }

        private boolean isInventoryEmpty()
        {
            for (int i = 0; i < getSlots(); i++)
            {
                if (!getStackInSlot(i).isEmpty())
                {
                    return false;
                }
            }
            return true;
        }

        public ItemStack findJavelin()
        {
            for (int i = 0; i < getSlots(); i++)
            {
                ItemStack stack = extractItem(i, 1, true);
                if (!stack.isEmpty() && (stack.getItem() instanceof ItemMetalJavelin ||
                    stack.getItem() instanceof ItemRockJavelin))
                {
                    return extractItem(i, 1, false);
                }
            }
            return null;
        }

        public ItemStack findArrows()
        {
            for (int i = 0; i < getSlots(); i++)
            {
                ItemStack stack = extractItem(i, 1, true);
                if (!stack.isEmpty() && stack.getItem() instanceof ItemArrow)
                {
                    return extractItem(i, stack.getMaxStackSize(), false);
                }
            }
            return null;
        }
    }

}
