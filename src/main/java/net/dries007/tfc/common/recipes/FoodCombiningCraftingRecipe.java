/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;

public class FoodCombiningCraftingRecipe extends CustomRecipe implements ISimpleRecipe<CraftingContainer>
{
    public FoodCombiningCraftingRecipe(ResourceLocation id)
    {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level)
    {
        boolean empty = true;
        ItemStack foodStack = ItemStack.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); i++)
        {
            final ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty())
            {
                empty = false;

                // All nonempty stacks must be a food
                if (!stack.getCapability(FoodCapability.CAPABILITY).isPresent())
                {
                    return false;
                }
                // If there's more than one stack, keep count of the previous ones
                if (foodStack.isEmpty())
                {
                    // First stack, so save and move on
                    foodStack = stack;
                }
                else
                {
                    // Another stack, so compare. If not equal, or one is not a food, the recipe is invalid
                    if (!FoodCapability.areStacksStackableExceptCreationDate(stack, foodStack))
                    {
                        return false;
                    }
                }
            }
        }
        // Don't match a completely empty grid. However, if the grid isn't empty, at this point, match the recipe since we only contain valid component items.
        return !empty;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv)
    {
        ItemStack resultStack = ItemStack.EMPTY;
        int outputAmount = 0;
        long minCreationDate = -1;
        for (int i = 0; i < inv.getContainerSize(); i++)
        {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty())
            {
                // Get the food capability
                IFood cap = stack.getCapability(FoodCapability.CAPABILITY).resolve().orElse(null);
                if (cap != null)
                {
                    // Increment output amount
                    outputAmount++;

                    // Find the min creation date
                    if (minCreationDate == -1 || minCreationDate > cap.getCreationDate())
                    {
                        minCreationDate = cap.getCreationDate();
                    }

                    // And save the stack
                    if (resultStack.isEmpty())
                    {
                        resultStack = stack.copy();
                    }
                }
            }
        }

        // Update the capability and count of the result
        resultStack.setCount(outputAmount);
        final long date = minCreationDate;
        resultStack.getCapability(FoodCapability.CAPABILITY).ifPresent(cap -> cap.setCreationDate(date));
        return resultStack;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.FOOD_COMBINING_CRAFTING.get();
    }
}
