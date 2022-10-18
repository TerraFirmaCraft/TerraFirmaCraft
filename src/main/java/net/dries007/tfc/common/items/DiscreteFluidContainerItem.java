/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.common.capabilities.DiscreteItemStackFluidHandler;
import org.jetbrains.annotations.Nullable;

public class DiscreteFluidContainerItem extends FluidContainerItem
{
    public DiscreteFluidContainerItem(Properties properties, Supplier<Integer> capacity, TagKey<Fluid> whitelist, boolean canPlaceSourceBlocks)
    {
        this(properties, capacity, whitelist, true, canPlaceSourceBlocks);
    }

    public DiscreteFluidContainerItem(Properties properties, Supplier<Integer> capacity, TagKey<Fluid> whitelist, boolean canPlaceLiquidsInWorld, boolean canPlaceSourceBlocks)
    {
        super(properties, capacity, whitelist, canPlaceLiquidsInWorld, canPlaceSourceBlocks);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new DiscreteItemStackFluidHandler(stack, whitelist, capacity.get());
    }
}
