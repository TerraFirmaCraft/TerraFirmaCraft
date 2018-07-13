/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.Size;
import net.dries007.tfc.objects.Weight;

public class CapabilityItemSize
{
    private static final CapabilityItemSize INSTANCE = new CapabilityItemSize();
    private static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "item_size");

    @CapabilityInject(IItemSize.class)
    public static Capability<IItemSize> ITEM_SIZE_CAPABILITY = null;

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IItemSize.class, new Capability.IStorage<IItemSize>()
        {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IItemSize> capability, IItemSize instance, EnumFacing side)
            {
                return null;
            }

            @Override
            public void readNBT(Capability<IItemSize> capability, IItemSize instance, EnumFacing side, NBTBase nbt)
            {

            }
        }, () -> INSTANCE.getCapability(Size.SMALL, Weight.MEDIUM, true));

    }

    /**
     * Adds a simple IItemSize capability to an item instance. Call this from an AttachCapabilitiesEvent handler.
     * This will also override the item's stacksize. If an item uses a custom getStacksize implementation, that will take priority
     *
     * @param event    The AttachCapabilitiesEvent that was fired
     * @param item     The item to attach the capability to
     * @param size     The item size
     * @param weight   The item weight
     * @param canStack An override for if this item can stack or not.
     */
    public static void add(AttachCapabilitiesEvent<ItemStack> event, Item item, Size size, Weight weight, boolean canStack)
    {
        event.addCapability(ID, INSTANCE.getProvider(size, weight, canStack));
        item.setMaxStackSize(IItemSize.getStackSize(size, weight, canStack));
    }

    /**
     * Gets the IItemSize instance from an itemstack, either via capability or via interface
     *
     * @param stack The stack
     * @return The IItemSize if it exists, or null if it doesn't
     */
    @Nullable
    public static IItemSize getIItemSize(ItemStack stack)
    {
        if (stack.getItem() instanceof IItemSize)
        {
            return (IItemSize) stack.getItem();
        }
        return stack.getCapability(ITEM_SIZE_CAPABILITY, null);
    }

    /**
     * Gets a default instance of a ICapabilityProvider for IItemSize. Use with CapabilityItemSize.INSTANCE.getProvider
     *
     * @param size     The size
     * @param weight   The weight
     * @param canStack override for non-stackable items
     * @return The ICapabilityProvider for an object
     */
    public ICapabilityProvider getProvider(Size size, Weight weight, boolean canStack)
    {
        return new ItemSizeProvider(size, weight, canStack);
    }

    /**
     * Gets a default implementation of IItemSize. Use with CapabilityItemSize.INSTANCE.getCapability
     *
     * @param size     The size
     * @param weight   The weight
     * @param canStack override for non-stackable items
     * @return The IItemSize
     */
    public IItemSize getCapability(Size size, Weight weight, boolean canStack)
    {
        return new ItemSize(size, weight, canStack);
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    private class ItemSize implements IItemSize
    {
        private final Size size;
        private final Weight weight;
        private boolean canStack;

        private ItemSize(Size size, Weight weight, boolean canStack)
        {
            this.size = size;
            this.weight = weight;
            this.canStack = canStack;
        }

        @Override
        public Size getSize(ItemStack stack)
        {
            return this.size;
        }

        @Override
        public Weight getWeight(ItemStack stack)
        {
            return this.weight;
        }

        @Override
        public boolean canStack(ItemStack stack)
        {
            return canStack;
        }
    }

    private class ItemSizeProvider implements ICapabilityProvider
    {
        private final IItemSize capability;

        private ItemSizeProvider(Size size, Weight weight, boolean canStack)
        {
            this.capability = new ItemSize(size, weight, canStack);
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            //noinspection ConstantConditions
            return capability == ITEM_SIZE_CAPABILITY;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            return capability == ITEM_SIZE_CAPABILITY ? (T) this.capability : null;
        }
    }
}
