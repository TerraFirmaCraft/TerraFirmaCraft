/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;

public class FoodCombiningCraftingRecipe extends CustomRecipe implements ISimpleRecipe<CraftingContainer>
{
    public FoodCombiningCraftingRecipe(ResourceLocation id, CraftingBookCategory category)
    {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level)
    {
        int notEmptyCount = 0;
        ItemStack foodStack = ItemStack.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); i++)
        {
            final ItemStack stack = inv.getItem(i);
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
    public ItemStack assemble(CraftingContainer inv, RegistryAccess access)
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
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container)
    {
        // Avoid container items since they should have been moved to output (see TerraFirmaCraft#2788)
        return NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.FOOD_COMBINING_CRAFTING.get();
    }
}
