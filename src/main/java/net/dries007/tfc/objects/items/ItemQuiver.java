/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayer;
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

            deserializeNBT(nbt);
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
            //noinspection ConstantConditions
            return OreDictionaryHelper.doesStackMatchOre(stack, "javelin") ||
                   //OreDictionaryHelper.doesStackMatchOre(stack, "arrow") ||
                   stack.getItem().getRegistryName().getPath().endsWith("arrow"); // no oreDict for vanilla arrows
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
                //noinspection ConstantConditions
                if (!stack.isEmpty() && stack.getItem().getRegistryName().getPath().endsWith("arrow"))
                {
                    return extractItem(i, 1, false);
                }
            }
            return null;
        }
    }

}
