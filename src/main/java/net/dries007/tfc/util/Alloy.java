/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.util;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

import com.google.common.collect.Sets;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.AlloyRecipe;
import net.dries007.tfc.api.types.Metal;

public class Alloy
{

    private final Map<Metal, Integer> MAP;
    private int totalAmount;
    private boolean isValid;

    public Alloy()
    {
        MAP = new HashMap<>();
        totalAmount = 0;
        isValid = true;
    }

    public Alloy add(@Nonnull IItemHandler inventory)
    {
        for (int i = 0; i < inventory.getSlots(); i++)
            add(inventory.getStackInSlot(i));
        return this;
    }

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

    public Alloy add(@Nonnull Alloy other)
    {
        for (Map.Entry<Metal, Integer> entry : other.MAP.entrySet())
            add(entry.getKey(), entry.getValue());
        return this;
    }

    public Alloy add(@Nonnull Metal metal, int amount)
    {
        MAP.merge(metal, amount, (x, y) -> x + y);
        totalAmount += amount;
        return this;
    }

    /**
     * Gets the result of mixing the alloy right now
     *
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
        return Metal.UNKNOWN;
    }

    /**
     * Gets the total amount of alloy created
     *
     * @return The amount
     */
    public int getAmount()
    {
        return totalAmount;
    }

    /**
     * Note: this is not a check if the alloy will turn into unknown metal
     *
     * @return is the alloy valid (set if it was constructed via ItemStacks and one itemstack wasn't an IMetalObject)
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
