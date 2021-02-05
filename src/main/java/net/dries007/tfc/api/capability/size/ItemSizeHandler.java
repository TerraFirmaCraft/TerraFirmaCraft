/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.size;

import java.util.EnumMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ItemSizeHandler implements ICapabilityProvider, IItemSize
{

    private static final EnumMap<Size, EnumMap<Weight, ItemSizeHandler[]>> CACHE = new EnumMap<>(Size.class);

    public static ItemSizeHandler get(Size size, Weight weight, boolean canStack)
    {
        EnumMap<Weight, ItemSizeHandler[]> nested = CACHE.get(size);
        if (nested == null)
        {
            CACHE.put(size, nested = new EnumMap<>(Weight.class));
        }
        ItemSizeHandler[] handlers = nested.get(weight);
        if (handlers == null)
        {
            nested.put(weight, handlers = new ItemSizeHandler[2]);
        }
        if (handlers[canStack ? 1 : 0] == null)
        {
            handlers[canStack ? 1 : 0] = new ItemSizeHandler(size, weight, canStack);
        }
        return handlers[canStack ? 1 : 0];
    }

    public static ItemSizeHandler getDefault()
    {
        return get(Size.SMALL, Weight.LIGHT, true); // Default to fitting in small vessels and stacksize = 32
    }

    private final Size size;
    private final Weight weight;
    private final boolean canStack;

    public ItemSizeHandler(Size size, Weight weight, boolean canStack)
    {
        this.size = size;
        this.weight = weight;
        this.canStack = canStack;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemSize.ITEM_SIZE_CAPABILITY;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemSize.ITEM_SIZE_CAPABILITY ? (T) this : null;
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return this.size;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return this.weight;
    }

    @Override
    public boolean canStack(@Nonnull ItemStack stack)
    {
        return canStack;
    }

    /**
     * Should be called from {@link net.minecraft.item.Item#getItemStackLimit(ItemStack)}
     */
    @Override
    public int getStackSize(@Nonnull ItemStack stack)
    {
        return this.canStack ? this.weight.stackSize : 1;
    }
}
