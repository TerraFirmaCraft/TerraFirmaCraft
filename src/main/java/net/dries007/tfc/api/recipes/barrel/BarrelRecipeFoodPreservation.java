/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes.barrel;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodTrait;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.inventory.ingredient.IngredientItemFoodTrait;
import net.dries007.tfc.util.Helpers;

public class BarrelRecipeFoodPreservation extends BarrelRecipe
{
    public static BarrelRecipe vinegar(@Nonnull IIngredient<ItemStack> inputStack)
    {
        return new BarrelRecipeFoodPreservation(IIngredient.of(FluidsTFC.VINEGAR.get(), 125), new IngredientItemFoodTrait(inputStack, FoodTrait.PICKLED), FoodTrait.VINEGAR, "barrel_recipe_vinegar");
    }

    private final FoodTrait trait;
    private final String tooltipName;

    private BarrelRecipeFoodPreservation(@Nonnull IIngredient<FluidStack> inputFluid, @Nonnull IIngredient<ItemStack> inputStack, FoodTrait trait, String tooltipName)
    {
        super(inputFluid, inputStack, null, ItemStack.EMPTY, -1);
        this.trait = trait;
        this.tooltipName = tooltipName;
    }

    @Override
    public boolean isValidInput(@Nullable FluidStack inputFluid, ItemStack inputStack)
    {
        // Only preserve food it there's enough fluid (the amount needed for 1 item * the number of items)
        return super.isValidInput(inputFluid, inputStack) && (inputFluid == null || inputFluid.amount / this.inputFluid.getAmount() >= inputStack.getCount() / this.inputStack.getAmount());
    }

    @Nonnull
    @Override
    public List<ItemStack> getOutputItem(FluidStack inputFluid, ItemStack inputStack)
    {
        return Helpers.listOf(inputStack);
    }

    @Override
    public void onBarrelSealed(FluidStack inputFluid, ItemStack inputStack)
    {
        CapabilityFood.applyTrait(inputStack, trait);
    }

    @Nonnull
    @Override
    public List<ItemStack> getOutputItemOnUnseal(FluidStack inputFluid, ItemStack inputStack)
    {
        CapabilityFood.removeTrait(inputStack, trait);
        return Helpers.listOf(inputStack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getResultName()
    {
        return I18n.format("tfc.tooltip." + tooltipName);
    }
}
