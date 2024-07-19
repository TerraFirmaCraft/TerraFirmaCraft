/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.component.glass.GlassOperation;
import net.dries007.tfc.common.component.glass.GlassWorking;
import net.dries007.tfc.common.recipes.RecipeHelpers;

public enum AddPowderModifier implements ItemStackModifier
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack primaryInput, Context context)
    {
        for (ItemStack input : RecipeHelpers.getCraftingInput())
        {
            final GlassOperation op = GlassOperation.getByPowder(input);
            if (op != null)
            {
                GlassWorking.apply(stack, op);
                return stack;
            }
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
        return ItemStackModifiers.ADD_POWDER.get();
    }
}
