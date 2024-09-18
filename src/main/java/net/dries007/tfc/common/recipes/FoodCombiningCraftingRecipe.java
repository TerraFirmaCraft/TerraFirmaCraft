/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.IFood;

public class FoodCombiningCraftingRecipe extends CustomRecipe
{
    public static final FoodCombiningCraftingRecipe INSTANCE = new FoodCombiningCraftingRecipe();

    private FoodCombiningCraftingRecipe()
    {
        super(CraftingBookCategory.MISC);
    }

    @Override
    public boolean matches(CraftingInput input, Level level)
    {
        int notEmptyCount = 0;
        ItemStack foodStack = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++)
        {
            final ItemStack stack = input.getItem(i);
            if (!stack.isEmpty())
            {
                notEmptyCount++;

                // All nonempty stacks must be a food
                if (!FoodCapability.has(stack))
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
        return notEmptyCount > 1;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries)
    {
        ItemStack resultStack = ItemStack.EMPTY;
        int outputAmount = 0;
        long minCreationDate = -1;
        for (int i = 0; i < input.size(); i++)
        {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty())
            {
                // Get the food capability
                final @Nullable IFood cap = FoodCapability.get(stack);
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
        FoodCapability.setCreationDate(resultStack, date);
        return resultStack;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input)
    {
        // Avoid container items since they should have been moved to output (see TerraFirmaCraft#2788)
        return NonNullList.withSize(input.size(), ItemStack.EMPTY);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.FOOD_COMBINING_CRAFTING.get();
    }
}
