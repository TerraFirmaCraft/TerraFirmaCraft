/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;

public enum CopyHeatModifier implements ItemStackModifier.SingleInstance<CopyHeatModifier>
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        final @Nullable IHeat heat = HeatCapability.get(input);
        if (heat != null)
        {
            HeatCapability.setTemperature(stack, heat.getTemperature());
        }
        return stack;
    }

    @Override
    public CopyHeatModifier instance()
    {
        return INSTANCE;
    }
}
