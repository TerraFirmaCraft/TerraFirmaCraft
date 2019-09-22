/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.metal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.api.types.Metal;

public class MetalItemHandler implements ICapabilityProvider, IMetalItem
{
    private final Metal metal;
    private final int amount;
    private final boolean canMelt;

    public MetalItemHandler(Metal metal, int amount, boolean canMelt)
    {
        this.metal = metal;
        this.amount = amount;
        this.canMelt = canMelt;
    }

    public MetalItemHandler()
    {
        this(Metal.UNKNOWN, 0, false);
    }

    @Nullable
    @Override
    public Metal getMetal(ItemStack stack)
    {
        return metal;
    }

    @Override
    public int getSmeltAmount(ItemStack stack)
    {
        return amount;
    }

    @Override
    public boolean canMelt(ItemStack stack)
    {
        return canMelt;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityMetalItem.METAL_OBJECT_CAPABILITY;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityMetalItem.METAL_OBJECT_CAPABILITY ? (T) this : null;
    }
}
