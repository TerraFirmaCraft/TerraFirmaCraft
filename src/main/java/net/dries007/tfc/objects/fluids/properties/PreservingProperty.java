/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.fluids.properties;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.capability.food.FoodTrait;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

public class PreservingProperty
{
    public static final FluidProperty<PreservingProperty> PRESERVING = new FluidProperty<>("preserving");

    private final FoodTrait trait;
    private final IIngredient<ItemStack> ingredient;

    public PreservingProperty(FoodTrait trait, IIngredient<ItemStack> ingredient)
    {
        this.trait = trait;
        this.ingredient = ingredient;
    }

    public FoodTrait getTrait()
    {
        return trait;
    }

    public boolean test(ItemStack stack)
    {
        return ingredient.testIgnoreCount(stack);
    }
}
