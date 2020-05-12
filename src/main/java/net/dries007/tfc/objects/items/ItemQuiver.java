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
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
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
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.ArmorMaterialTFC;
import net.dries007.tfc.objects.inventory.capability.ISlotCallback;
import net.dries007.tfc.util.OreDictionaryHelper;

@ParametersAreNonnullByDefault
public class ItemQuiver extends ItemArmorTFC
{
    public static void replenishJavelin(InventoryPlayer playerInv)
    {
        QuiverCapability quiver = findQuiver(playerInv);
        if (quiver != null)
        {
            ItemStack newJav = quiver.findJavelin();
            if (newJav != null)
            {
                playerInv.setInventorySlotContents(playerInv.currentItem, newJav);
                playerInv.markDirty();
            }
        }
    }

    //called from ArrowNockEvent handler, we know there are no arrows in current inventory
    public static boolean replenishArrow(EntityPlayer player)
    {
        //empty destination slot for single arrow from quiver
        int empty = player.inventory.getFirstEmptyStack();

        if (empty >= 0)
        {
            QuiverCapability quiver = findQuiver(player.inventory);
            if (quiver != null)
            {
                ItemStack newArrow = quiver.findArrow();
                if (newArrow != null)
                {
                    player.inventory.setInventorySlotContents(empty, newArrow);
                    player.inventory.markDirty();
                    return true;
                }
            }
        }
        return false;
    }

    //true = we picked up the whole stack, false = none or some picked up
    public static boolean pickupAmmo(EntityItemPickupEvent event)
    {
        ItemStack stack = event.getItem().getItem(); //really.
        if (OreDictionaryHelper.doesStackMatchOre(stack, "javelin"))
        { // if no javelin on hotbar, don't put in quiver unless no empty slots
            InventoryPlayer inv = event.getEntityPlayer().inventory;
            boolean found = false;
            boolean empty = false;
            for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++)
            {
                ItemStack slot = inv.getStackInSlot(i);
                if (!empty && slot.isEmpty())
                {
                    empty = true;
                    continue;
                }
                if (OreDictionaryHelper.doesStackMatchOre(slot, "javelin"))
                {
                    found = true;
                    break;
                }
            }
            if (!found && empty)
            {
                return false;
            }
        }
        Item item = stack.getItem();
        if (OreDictionaryHelper.doesStackMatchOre(stack, "javelin") || item instanceof ItemArrow)
        {
            QuiverCapability quiver = findQuiver(event.getEntityPlayer().inventory);
            if (quiver != null)
            {
                stack.setCount(ItemHandlerHelper.insertItem(quiver, stack, false).getCount());
                // Tell forge if we picked the whole stack up
                return stack.isEmpty();
            }
        }
        return false;
    }

    static QuiverCapability findQuiver(InventoryPlayer playerInv)
    {
        int mainToSearch = 0;
        switch (ConfigTFC.General.PLAYER.quiverSearch)
        {
            case DISABLED:
                return null;
            case ARMOR:
                break; // search nowhere else
            case HOTBAR:
                mainToSearch = InventoryPlayer.getHotbarSize();
                break;
            case INVENTORY:
                mainToSearch = playerInv.mainInventory.size();
                break;
        }

        ItemStack cur = null;
        boolean found = false;
        for (int i = 0; i < playerInv.armorInventory.size(); i++)
        {
            cur = playerInv.armorInventory.get(i);
            if (cur.getItem() instanceof ItemQuiver)
            {
                found = true;
                break;
            }
        }
        if (!found)
        {
            for (int i = 0; i < mainToSearch; i++)
            {
                cur = playerInv.mainInventory.get(i);
                if (cur.getItem() instanceof ItemQuiver)
                {
                    found = true;
                    break;
                }
            }
        }
        if (found)
        {
            IItemHandler quiverCapability = cur.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            return (QuiverCapability) quiverCapability;
        }
        return null;
    }

    public ItemQuiver()
    {
        super(ArmorMaterialTFC.QUIVER, 1 /* chest*/, EntityEquipmentSlot.CHEST);
    }

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
        return Weight.MEDIUM;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new QuiverCapability(nbt);
    }

    // Extends ItemStackHandler for ease of use.
    public static class QuiverCapability extends ItemStackHandler implements ICapabilityProvider, ISlotCallback
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

        public ItemStack findJavelin()
        {
            for (int i = 0; i < getSlots(); i++)
            {
                ItemStack stack = extractItem(i, 1, true);
                if (!stack.isEmpty() && (OreDictionaryHelper.doesStackMatchOre(stack, "javelin")))
                {
                    return extractItem(i, 1, false);
                }
            }
            return null;
        }

        public ItemStack findArrow()
        {
            for (int i = 0; i < getSlots(); i++)
            {
                ItemStack stack = extractItem(i, 1, true);
                if (!stack.isEmpty() && stack.getItem() instanceof ItemArrow)
                {
                    return extractItem(i, 1, false); //just pull one at a time
                }
            }
            return null;
        }
    }
}
