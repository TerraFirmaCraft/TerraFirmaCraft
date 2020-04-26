/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonObject;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;

@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class FoodStackingRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        ItemStack foodStack = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                // All nonempty stacks must be a food
                if (!stack.hasCapability(CapabilityFood.CAPABILITY, null))
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
                    if (!CapabilityFood.areStacksStackableExceptCreationDate(stack, foodStack))
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        ItemStack resultStack = ItemStack.EMPTY;
        int outputAmount = 0;
        long minCreationDate = -1;
        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                // Get the food capability
                IFood cap = stack.getCapability(CapabilityFood.CAPABILITY, null);
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
        IFood cap = resultStack.getCapability(CapabilityFood.CAPABILITY, null);
        if (cap != null)
        {
            cap.setCreationDate(minCreationDate);
        }
        return resultStack;
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getRecipeOutput()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic()
    {
        return true;
    }

    public static class Factory implements IRecipeFactory
    {
        @Override
        public IRecipe parse(JsonContext context, JsonObject json)
        {
            return new FoodStackingRecipe();
        }
    }
}
