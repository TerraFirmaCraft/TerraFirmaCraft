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

@ParametersAreNonnullByDefault
public class HeatRecipeSimple extends HeatRecipe
{
    private final ItemStack output;

    public HeatRecipeSimple(IIngredient<ItemStack> ingredient, ItemStack output, float transformTemp)
    {
        super(ingredient, Metal.Tier.TIER_0, transformTemp);
        this.output = output;
    }

    @Override
    @Nonnull
    public ItemStack getOutputStack(ItemStack input)
    {
        return output.copy();
    }
}
