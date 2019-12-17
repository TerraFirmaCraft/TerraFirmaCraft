/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.ingredient;

import java.util.function.Predicate;
import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * This is an ingredient wrapper for various types
 * It includes static constructors for both item stack and fluid stack ingredients
 *
 * @param <T> the type of ingredient (i.e. ItemStack, FluidStack, etc.)
 * @author AlcatrazEscapee
 */
public interface IIngredient<T> extends Predicate<T>
{
    IIngredient<?> EMPTY = input -> false;
    IIngredient<?> ANY = input -> true;

    @SuppressWarnings("unchecked")
    static <P> IIngredient<P> empty()
    {
        return (IIngredient<P>) EMPTY;
    }

    @SuppressWarnings("unchecked")
    static <P> IIngredient<P> any()
    {
        return (IIngredient<P>) ANY;
    }

    static IIngredient<ItemStack> of(@Nonnull Block predicateBlock)
    {
        return new IngredientItemStack(new ItemStack(predicateBlock, 1, OreDictionary.WILDCARD_VALUE));
    }

    static IIngredient<ItemStack> of(@Nonnull Item predicateItem)
    {
        return new IngredientItemStack(new ItemStack(predicateItem, 1, OreDictionary.WILDCARD_VALUE));
    }

    static IIngredient<ItemStack> of(@Nonnull Item predicateItem, int amount)
    {
        return new IngredientItemStack(new ItemStack(predicateItem, amount, OreDictionary.WILDCARD_VALUE));
    }

    static IIngredient<ItemStack> of(@Nonnull ItemStack predicateStack)
    {
        return new IngredientItemStack(predicateStack);
    }

    static IIngredient<ItemStack> of(@Nonnull String oreName)
    {
        return new IngredientOreDict(oreName);
    }

    static IIngredient<ItemStack> of(@Nonnull String oreName, int amount)
    {
        return new IngredientOreDict(oreName, amount);
    }

    static IIngredient<FluidStack> of(@Nonnull FluidStack predicateStack)
    {
        return new IngredientFluidStack(predicateStack);
    }

    static IIngredient<FluidStack> of(@Nonnull Fluid fluid, int amount)
    {
        return new IngredientFluidStack(fluid, amount);
    }

    static IIngredient<FluidStack> of(int amount, @Nonnull Fluid... fluids)
    {
        return new IngredientMultipleFluidStack(amount, fluids);
    }


    /**
     * This is used by JEI-CT hooks, return a valid list of inputs for this IIngredient
     *
     * @return NonNullList containing valid ingredients(fluidstack/itemstack) for this IIngredient
     */
    default NonNullList<T> getValidIngredients()
    {
        return NonNullList.create();
    }

    /**
     * This is used by recipes to test if the ingredient matches the input
     *
     * @param input the input supplied to the recipe
     * @return true if the ingredient matches the input
     */
    @Override
    boolean test(T input);

    /**
     * This is used by recipes to test if an input is valid, but necessarily high enough quantity
     * i.e. used to check if an item is valid for a slot, not if the recipe will complete.
     *
     * @param input the input supplied to the recipe
     * @return true if the ingredient matches the input, ignoring the amount of input
     */
    default boolean testIgnoreCount(T input)
    {
        return test(input);
    }

    /**
     * Consume one recipe's worth of input
     * Depending on the input type, this may modify input and return it, or create a new output
     *
     * @param input the input supplied to the recipe
     * @return the result after modification.
     */
    default T consume(T input)
    {
        return input;
    }

    /**
     * Get the amount represented by this ingredient
     *
     * @return the amount
     */
    default int getAmount()
    {
        return 1;
    }
}
