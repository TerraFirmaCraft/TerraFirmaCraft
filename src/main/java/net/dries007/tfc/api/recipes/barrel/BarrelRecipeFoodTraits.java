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
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.inventory.ingredient.IngredientItemFoodTrait;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;

public class BarrelRecipeFoodTraits extends BarrelRecipe
{
    public static BarrelRecipe pickling(@Nonnull IIngredient<ItemStack> inputStack)
    {
        return new BarrelRecipeFoodTraits(IIngredient.of(FluidsTFC.VINEGAR.get(), 125), new IngredientItemFoodTrait(inputStack, FoodTrait.BRINED), FoodTrait.PICKLED, 4 * ICalendar.TICKS_IN_HOUR, "barrel_recipe_pickling");
    }

    public static BarrelRecipe brining(@Nonnull IIngredient<ItemStack> inputStack)
    {
        return new BarrelRecipeFoodTraits(IIngredient.of(FluidsTFC.BRINE.get(), 125), inputStack, FoodTrait.BRINED, 4 * ICalendar.TICKS_IN_HOUR, "barrel_recipe_brining");
    }

    private final FoodTrait trait;
    private final String tooltipName;

    private BarrelRecipeFoodTraits(@Nonnull IIngredient<FluidStack> inputFluid, @Nonnull IIngredient<ItemStack> inputStack, FoodTrait trait, int duration, String tooltipName)
    {
        super(inputFluid, inputStack, null, ItemStack.EMPTY, duration);
        this.trait = trait;
        this.tooltipName = tooltipName;
    }

    @Override
    public boolean isValidInput(@Nullable FluidStack inputFluid, ItemStack inputStack)
    {
        IFood food = inputStack.getCapability(CapabilityFood.CAPABILITY, null);
        return super.isValidInput(inputFluid, inputStack) && food != null && !food.getTraits().contains(trait); // Don't apply again and again.
    }

    @Nonnull
    @Override
    public List<ItemStack> getOutputItem(FluidStack inputFluid, ItemStack inputStack)
    {
        ItemStack stack = inputStack.copy();
        IFood food = stack.getCapability(CapabilityFood.CAPABILITY, null);
        if (food != null)
        {
            CapabilityFood.applyTrait(food, trait);
        }
        return Helpers.listOf(stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getResultName()
    {
        return I18n.format("tfc.tooltip." + tooltipName);
    }
}
