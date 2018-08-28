/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.api.capability.size;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ItemSizeHandler implements ICapabilityProvider, IItemSize
{
    private final Size size;
    private final Weight weight;
    private boolean canStack;

    public ItemSizeHandler(Size size, Weight weight, boolean canStack)
    {
        this.size = size;
        this.weight = weight;
        this.canStack = canStack;
    }

    public ItemSizeHandler()
    {
        this(Size.NORMAL, Weight.MEDIUM, true);
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

    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return this.size;
    }

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
}
