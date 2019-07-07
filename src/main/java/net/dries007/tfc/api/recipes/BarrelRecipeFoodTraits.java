/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.api.capability.food.IFoodTrait;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

public class BarrelRecipeFoodTraits extends BarrelRecipe
{
    private final IFoodTrait trait;

    public BarrelRecipeFoodTraits(@Nonnull IIngredient<FluidStack> inputFluid, @Nonnull IIngredient<ItemStack> inputStack, IFoodTrait trait, int duration)
    {
        super(inputFluid, inputStack, null, ItemStack.EMPTY, duration);
        this.trait = trait;
    }

    @Nonnull
    @Override
    public ItemStack getOutputItem(FluidStack inputFluid, ItemStack inputStack)
    {
        // Apply the trait to the input and copy it to output
        IFood food = inputStack.getCapability(CapabilityFood.CAPABILITY, null);
        if (food != null)
        {
            if (!food.getTraits().contains(trait))
            {
                food.getTraits().add(trait);
            }
        }
        return inputStack.copy();
    }
}
