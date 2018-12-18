/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Sets;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.AlloyRecipe;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.util.IMetalObject;

/**
 * A helper class for working with alloys
 *
 * @author AlcatrazEscapee
 */
public class Alloy
{

    private final Map<Metal, Integer> MAP;
    private int totalAmount;
    private boolean isValid;

    /**
     * Constructs a new alloy. It starts with no metal content
     */
    public Alloy()
    {
        MAP = new HashMap<>();
        totalAmount = 0;
        isValid = true;
    }


    /**
     * Add metal to an alloy from each item in an inventory
     * Note if the an item doesn't implement {@link IMetalObject} it will be ignored, and {@param isValid} will be set to false
     *
     * @param inventory an inventory to iterate through.
     * @return the alloy, for method chaining
     */
    public Alloy add(@Nonnull IItemHandler inventory)
    {
        for (int i = 0; i < inventory.getSlots(); i++)
            add(inventory.getStackInSlot(i));
        return this;
    }

    /**
     * Add metal to an alloy from an item stack
     * Note if the an item doesn't implement {@link IMetalObject} it will be ignored, and {@param isValid} will be set to false
     * @param stack an item stack
     * @return the alloy, for method chaining
     */
    public Alloy add(@Nonnull ItemStack stack)
    {
        if (stack.isEmpty())
            return this;
        if (stack.getItem() instanceof IMetalObject)
        {
            IMetalObject m = (IMetalObject) stack.getItem();
            add(m.getMetal(stack), m.getSmeltAmount(stack) * stack.getCount());
        }
        else
        {
            isValid = false;
        }
        return this;
    }

    /**
     * Copy the contents of one alloy into another
     * @param other The other alloy
     * @return The alloy, for method chaining
     */
    public Alloy add(@Nonnull Alloy other)
    {
        for (Map.Entry<Metal, Integer> entry : other.MAP.entrySet())
            add(entry.getKey(), entry.getValue());
        return this;
    }

    /**
     * The simplest way to add to an alloy
     *
     * @param metal  The metal to add
     * @param amount The amount to add
     * @return The alloy, for method chaining
     */
    public Alloy add(@Nullable Metal metal, int amount)
    {
        if (metal != null)
        {
            MAP.merge(metal, amount, (x, y) -> x + y);
            totalAmount += amount;
        }
        else
        {
            isValid = false;
        }
        return this;
    }

    /**
     * Gets the result of mixing the alloy right now
     * @return the result metal. Unknown if it doesn't match any recipe
     */
    @Nonnull
    public Metal getResult()
    {
        if (MAP.size() == 1)
            return MAP.keySet().iterator().next(); // Easy way to get the only metal in the alloy
        for (AlloyRecipe r : TFCRegistries.ALLOYS.getValuesCollection())
            if (matchesRecipe(r))
                return r.getResult();
        //noinspection ConstantConditions
        return Metal.UNKNOWN;
    }

    /**
     * Gets the total amount of alloy created
     * @return The amount
     */
    public int getAmount()
    {
        return totalAmount;
    }

    /**
     * Note: this is not a check if the alloy will turn into unknown metal
     * @return is the alloy valid (set if it was constructed via ItemStacks and one ItemStack wasn't an IMetalObject)
     */
    public boolean isValid()
    {
        return isValid;
    }

    private boolean matchesRecipe(AlloyRecipe recipe)
    {
        if (this.MAP.containsKey(recipe.getResult()))
        {
            Alloy other = new Alloy().add(this);
            int resultAmount = other.MAP.remove(recipe.getResult());
            other.totalAmount -= resultAmount;
            return other.matchesRecipeExact(recipe);
        }
        return this.matchesRecipeExact(recipe);
    }

    private boolean matchesRecipeExact(AlloyRecipe recipe)
    {
        // for each metal in the alloy, it needs to satisfy an ingredient
        // for each metal in the recipe, it needs to match with an alloy
        for (Metal metal : Sets.union(recipe.MAP.keySet(), MAP.keySet()))
        {
            if (!MAP.containsKey(metal) ||
                !recipe.MAP.containsKey(metal) ||
                !recipe.MAP.get(metal).test(MAP.get(metal).floatValue() / totalAmount))
                return false;
        }
        return true;
    }

}
