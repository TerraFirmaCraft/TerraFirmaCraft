/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.ingredient;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodTrait;
import net.dries007.tfc.api.capability.food.IFood;

public class IngredientItemFoodTrait implements IIngredient<ItemStack>
{
    private final IIngredient<ItemStack> innerIngredient;
    private final FoodTrait trait;

    public IngredientItemFoodTrait(IIngredient<ItemStack> innerIngredient, FoodTrait trait)
    {
        this.innerIngredient = innerIngredient;
        this.trait = trait;
    }

    @Override
    public NonNullList<ItemStack> getValidIngredients()
    {
        NonNullList<ItemStack> ingredients = innerIngredient.getValidIngredients();
        for (ItemStack stack : ingredients)
        {
            IFood food = stack.getCapability(CapabilityFood.CAPABILITY, null);
            if (food != null)
            {
                CapabilityFood.applyTrait(food, trait);
            }
        }
        return ingredients;
    }

    @Override
    public boolean test(ItemStack input)
    {
        return innerIngredient.test(input) && hasTrait(input);
    }

    @Override
    public boolean testIgnoreCount(ItemStack stack)
    {
        return innerIngredient.testIgnoreCount(stack) && hasTrait(stack);
    }

    @Override
    public ItemStack consume(ItemStack input)
    {
        return innerIngredient.consume(input);
    }

    private boolean hasTrait(ItemStack stack)
    {
        IFood cap = stack.getCapability(CapabilityFood.CAPABILITY, null);
        return cap != null && cap.getTraits().contains(trait);
    }
}
