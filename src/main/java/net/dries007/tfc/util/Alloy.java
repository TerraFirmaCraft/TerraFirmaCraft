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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.api.recipes.AlloyRecipe;
import net.dries007.tfc.api.recipes.heat.HeatRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.util.IMetalObject;
import net.dries007.tfc.objects.fluids.FluidMetal;

/**
 * A helper class for working with alloys
 *
 * @author AlcatrazEscapee
 */
public class Alloy implements INBTSerializable<NBTTagCompound>
{
    private final Map<Metal, Double> metalMap;
    private int totalAmount;
    private int maxAmount;
    private boolean isValid;

    /**
     * Constructs a new alloy. It starts with no metal content
     */
    public Alloy()
    {
        this(Integer.MAX_VALUE);
    }

    /**
     * Constructs an alloy with a maximum limit. Anything added over this limit will do nothing
     *
     * @param maxAmount The maximum alloy amount (in units)
     */
    public Alloy(int maxAmount)
    {
        this.metalMap = new HashMap<>();
        this.totalAmount = 0;
        this.isValid = true;
        this.maxAmount = maxAmount;
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
        {
            add(inventory.getStackInSlot(i));
        }
        return this;
    }

    /**
     * Adds metal to an alloy from a fluid stack
     * Note if the fluid is not an instance of {@link net.dries007.tfc.objects.fluids.FluidMetal} then it will be ignored, and {@link Alloy#isValid} will be set to false
     *
     * @param stack a fluid stack
     * @return the alloy, for method chaining
     */
    public Alloy add(@Nonnull FluidStack stack)
    {
        if (stack.getFluid() instanceof FluidMetal)
        {
            Metal metal = ((FluidMetal) stack.getFluid()).getMetal();
            add(metal, stack.amount);
        }
        else
        {
            isValid = false;
        }
        return this;
    }

    /**
     * Add metal to an alloy from an item stack
     * Note if the an item doesn't match a recipe it will be ignored, and {@link Alloy#isValid} will be set to false
     *
     * @param stack an item stack
     * @return the alloy, for method chaining
     */
    public Alloy add(@Nonnull ItemStack stack)
    {
        return add(stack, Metal.Tier.TIER_VI);
    }

    /**
     * Add metal to an alloy from an item stack
     * Note if the an item doesn't match a heat recipe it will be ignored, and {@link Alloy#isValid} will be set to false
     *
     * @param stack an item stack
     * @param deviceTier the tier of the device doing the heating
     * @return the alloy, for method chaining
     */
    public Alloy add(@Nonnull ItemStack stack, @Nonnull Metal.Tier deviceTier)
    {
        if (!stack.isEmpty())
        {
            HeatRecipe recipe = HeatRecipe.get(stack, deviceTier);
            if (recipe != null)
            {
                return add(stack, recipe);
            }
            isValid = false;
        }
        return this;
    }

    /**
     * Add metal to an alloy from an item stack
     *
     * @param stack  an item stack
     * @param recipe the recipe to use to convert the stack into fluid
     * @return the alloy, for method chaining
     */
    public Alloy add(@Nonnull ItemStack stack, @Nonnull HeatRecipe recipe)
    {
        if (!stack.isEmpty())
        {
            FluidStack fluidStack = recipe.getOutputFluid(stack);
            if (fluidStack != null)
            {
                fluidStack.amount *= stack.getCount();
                add(fluidStack);
            }
        }
        return this;
    }

    /**
     * Copy the contents of one alloy into another
     * Does not modify the other alloy
     *
     * @param other The other alloy
     * @return The alloy, for method chaining
     */
    public Alloy add(@Nonnull Alloy other)
    {
        for (Map.Entry<Metal, Double> entry : other.metalMap.entrySet())
        {
            add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * The simplest way to add to an alloy
     *
     * @param metal  The metal to add
     * @param amount The amount to add
     * @return The alloy, for method chaining
     */
    public Alloy add(@Nullable Metal metal, double amount)
    {
        if (metal != null)
        {
            // Account for alloy limits
            if (totalAmount + amount >= maxAmount)
            {
                // Find the amount that can be added
                amount = maxAmount - totalAmount;
                if (amount <= 0)
                {
                    // No more, i.e. totalAmount >= maxAmount
                    return this;
                }
            }
            metalMap.merge(metal, amount, (x, y) -> x + y);
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
     *
     * @return the result metal. Unknown if it doesn't match any recipe
     */
    @Nonnull
    public Metal getResult()
    {
        if (metalMap.size() == 1)
        {
            return metalMap.keySet().iterator().next(); // Easy way to get the only metal in the alloy
        }
        for (AlloyRecipe r : TFCRegistries.ALLOYS.getValuesCollection())
        {
            if (matchesRecipe(r))
            {
                return r.getResult();
            }
        }
        return Metal.UNKNOWN;
    }

    /**
     * Removes an amount of metal from the alloy.
     *
     * @param metalToRemove the metal component to remove
     * @param removeAmount  the amount to remove
     * @return the amount that was actually removed
     */
    public int remove(Metal metalToRemove, int removeAmount)
    {
        if (metalMap.containsKey(metalToRemove))
        {
            double currentAmount = metalMap.get(metalToRemove);
            if (currentAmount > removeAmount)
            {
                metalMap.put(metalToRemove, currentAmount - removeAmount);
                return removeAmount;
            }
            else
            {
                return (int) metalMap.remove(metalToRemove).doubleValue();
            }
        }
        return 0;
    }

    /**
     * Removes an amount of the current result from the alloy
     * Use {@link Alloy#getResult()} to see what alloy has been removed / returned
     *
     * @param removeAmount the amount to remove
     * @param simulate     if true, no actual changes will be made to the alloy
     * @return the amount that was actually removed
     */
    public int removeAlloy(int removeAmount, boolean simulate)
    {
        if (simulate)
        {
            return totalAmount < removeAmount ? totalAmount : removeAmount;
        }
        if (removeAmount >= totalAmount)
        {
            clear();
            return totalAmount;
        }
        else
        {
            for (Map.Entry<Metal, Double> entry : metalMap.entrySet())
            {
                // Remove the amount of metal from each component
                double remove = removeAmount * entry.getValue() / totalAmount;
                metalMap.put(entry.getKey(), entry.getValue() - remove);
            }
            totalAmount -= removeAmount;
            return removeAmount;
        }
    }

    /**
     * Gets the total amount of alloy created
     *
     * @return The amount, rounded to the closest integer
     */
    public int getAmount()
    {
        return totalAmount;
    }

    /**
     * Note: this is not a check if the alloy will turn into unknown metal
     *
     * @return is the alloy valid (set if it was constructed via ItemStacks and one ItemStack wasn't an IMetalObject)
     */
    public boolean isValid()
    {
        return isValid;
    }

    public Map<Metal, Double> getMetals()
    {
        return metalMap;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("isValid", isValid);
        nbt.setInteger("maxAmount", maxAmount);
        nbt.setInteger("totalAmount", totalAmount);
        NBTTagCompound alloys = new NBTTagCompound();
        for (Map.Entry<Metal, Double> entry : this.metalMap.entrySet())
        {
            //noinspection ConstantConditions
            alloys.setDouble(entry.getKey().getRegistryName().toString(), entry.getValue());
        }
        nbt.setTag("contents", alloys);
        return nbt;
    }

    @Override
    public void deserializeNBT(@Nullable NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            clear();
            isValid = nbt.getBoolean("isValid");
            maxAmount = nbt.getInteger("maxAmount");
            totalAmount = nbt.getInteger("totalAmount");

            NBTTagCompound alloys = nbt.getCompoundTag("contents");
            for (Metal metal : TFCRegistries.METALS.getValuesCollection())
            {
                //noinspection ConstantConditions
                String key = metal.getRegistryName().toString();
                if (alloys.hasKey(key))
                {
                    double amount = alloys.getDouble(key);
                    this.metalMap.put(metal, amount);
                }
            }
        }
    }

    /**
     * Resets the alloy
     */
    private void clear()
    {
        metalMap.clear();
        totalAmount = 0;
        isValid = true;
    }

    private boolean matchesRecipe(AlloyRecipe recipe)
    {
        if (this.metalMap.containsKey(recipe.getResult()))
        {
            Alloy other = new Alloy().add(this);
            double resultAmount = other.metalMap.remove(recipe.getResult());
            other.totalAmount -= resultAmount;
            return other.matchesRecipeExact(recipe);
        }
        return this.matchesRecipeExact(recipe);
    }

    private boolean matchesRecipeExact(AlloyRecipe recipe)
    {
        // for each metal in the alloy, it needs to satisfy an ingredient
        // for each metal in the recipe, it needs to match with an alloy
        for (Metal metal : Sets.union(recipe.getMetals().keySet(), metalMap.keySet()))
        {
            if (!metalMap.containsKey(metal) ||
                !recipe.getMetals().containsKey(metal) ||
                !recipe.getMetals().get(metal).test(metalMap.get(metal) / totalAmount))
                return false;
        }
        return true;
    }

}
