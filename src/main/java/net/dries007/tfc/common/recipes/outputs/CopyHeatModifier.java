/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeat;

public enum CopyHeatModifier implements ItemStackModifier
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input, Context context)
    {
        final @Nullable IHeat heat = HeatCapability.get(input);
        if (heat != null)
        {
            HeatCapability.setTemperature(stack, heat.getTemperature());
        }
        return stack;
    }

    @Override
    public boolean dependsOnInput()
    {
        return true;
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return ItemStackModifiers.COPY_HEAT.get();
    }
}
