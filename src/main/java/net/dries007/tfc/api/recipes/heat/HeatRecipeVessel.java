/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes.heat;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.items.ceramics.ItemSmallVessel;

@ParametersAreNonnullByDefault
public class HeatRecipeVessel extends HeatRecipe
{
    public HeatRecipeVessel(IIngredient<ItemStack> ingredient, float transformTemp, Metal.Tier minTier)
    {
        super(ingredient, transformTemp, minTier);
    }

    @Override
    @Nonnull
    public ItemStack getOutputStack(ItemStack input)
    {
        ItemStack output = input.copy();
        if (output.getItem() instanceof ItemSmallVessel)
        {
            return ((ItemSmallVessel) output.getItem()).getFiringResult(output);
        }
        return output;
    }
}