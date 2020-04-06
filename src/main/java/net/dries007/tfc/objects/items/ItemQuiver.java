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

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.ArmorMaterialTFC;
import net.dries007.tfc.objects.inventory.capability.ISlotCallback;
import net.dries007.tfc.objects.items.metal.ItemMetalJavelin;
import net.dries007.tfc.objects.items.rock.ItemRockJavelin;
import net.dries007.tfc.util.OreDictionaryHelper;

@ParametersAreNonnullByDefault
public class ItemQuiver extends ItemArmorTFC
{
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

    static QuiverCapability findQuiver(InventoryPlayer playerInv)
    {
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
            for (int i = 0; i < playerInv.mainInventory.size(); i++)
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
            return (QuiverCapability)quiverCapability;
        }
        return null;
    }

    public static void replenishJavelin(InventoryPlayer playerInv)
    {
        QuiverCapability quiver = findQuiver(playerInv);
        if (quiver != null)
        {
            ItemStack newJav = quiver.findJavelin();
            if (newJav != null)
            {
                playerInv.setInventorySlotContents(playerInv.currentItem, newJav);
            }
        }
    }

    //called from ArrowNockEvent handler, we know there are no arrows in current inventory
    public static boolean replenishArrow(EntityPlayer player)
    {
        //empty destination slot for single arrow from quiver
        int empty = player.inventory.getFirstEmptyStack();

        if (empty > 0 )
        {
            QuiverCapability quiver = findQuiver(player.inventory);
            if (quiver != null) {
                ItemStack newArrow = quiver.findArrow();
                if (newArrow != null)
                {
                    player.inventory.setInventorySlotContents(empty, newArrow);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean pickupAmmo(EntityItemPickupEvent event)
    {
        ItemStack stack = event.getItem().getItem(); //really.
        Item item = stack.getItem();
        if (item instanceof ItemRockJavelin || item instanceof ItemMetalJavelin)
        {
            InventoryPlayer inv = event.getEntityPlayer().inventory;
            boolean found = false;
            for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++)
            {
                ItemStack slot = inv.getStackInSlot(i);
                if (slot.getItem() instanceof ItemMetalJavelin || slot.getItem() instanceof ItemRockJavelin)
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                return false;
            }
        }
        if (item instanceof ItemRockJavelin || item instanceof ItemMetalJavelin || item instanceof ItemArrow)
        {
            QuiverCapability quiver = findQuiver(event.getEntityPlayer().inventory);
            if (quiver != null)
            {
                ItemStack remain = ItemHandlerHelper.insertItem(quiver, stack, true);
                if (remain.isEmpty()) // Forge doesn't handle mods picking up partial stacks well, EntityItem#onCollideWithPlayer
                {
                    ItemHandlerHelper.insertItem(quiver, stack, false);
                    return true;
                }
            }
        }
        return false;
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
