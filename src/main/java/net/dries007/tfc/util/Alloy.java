/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Sets;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.dries007.tfc.api.capability.metal.CapabilityMetalItem;
import net.dries007.tfc.api.capability.metal.IMetalItem;
import net.dries007.tfc.api.recipes.AlloyRecipe;
import net.dries007.tfc.api.recipes.heat.HeatRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.fluids.properties.MetalProperty;

/**
 * A helper class for working with alloys
 *
 * @author AlcatrazEscapee
 */
public class Alloy implements INBTSerializable<NBTTagCompound>
{
    // We compare alloy ranges to an accuracy of +/- 0.05% Anything outside of this range is ignored
    public static final double EPSILON = 0.0005;

    private final Object2DoubleMap<Metal> metalMap, sanitizedMetalMap;
    private int totalAmount;
    private int maxAmount;

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
        this.metalMap = new Object2DoubleOpenHashMap<>();
        this.sanitizedMetalMap = new Object2DoubleOpenHashMap<>();
        this.totalAmount = 0;
        this.maxAmount = maxAmount;
    }

    /**
     * Adds metal to an alloy from a fluid stack
     *
     * @param stack a fluid stack
     * @return the alloy, for method chaining
     */
    public Alloy add(@Nonnull FluidStack stack)
    {
        MetalProperty metalProperty = FluidsTFC.getWrapper(stack.getFluid()).get(MetalProperty.METAL);
        if (metalProperty != null)
        {
            Metal metal = metalProperty.getMetal();
            add(metal, stack.amount);
        }
        return this;
    }

    /**
     * Add metal to an alloy from an item stack, ignoring temperature and tier
     * Note if the an item doesn't match a recipe it will be ignored
     *
     * @param stack an item stack
     * @return the alloy, for method chaining
     */
    public Alloy add(@Nonnull ItemStack stack)
    {
        return add(stack, Metal.Tier.TIER_VI, Float.MAX_VALUE);
    }

    /**
     * Add metal to an alloy from an item stack
     * Note if the an item doesn't match a heat recipe it will be ignored
     *
     * @param stack       an item stack
     * @param deviceTier  the tier of the device doing the heating
     * @param temperature the temperature to melt items
     * @return the alloy, for method chaining
     */
    public Alloy add(@Nonnull ItemStack stack, @Nonnull Metal.Tier deviceTier, float temperature)
    {
        if (!stack.isEmpty())
        {
            HeatRecipe recipe = HeatRecipe.get(stack, deviceTier);
            if (recipe != null && recipe.isValidTemperature(temperature))
            {
                return add(stack, recipe);
            }
            else
            {
                IMetalItem metalObject = CapabilityMetalItem.getMetalItem(stack);
                if (metalObject != null)
                {
                    // Melt into unknown alloy (so items aren't simply voided and becomes something)
                    add(new FluidStack(FluidsTFC.getFluidFromMetal(Metal.UNKNOWN), metalObject.getSmeltAmount(stack) * stack.getCount()));
                }
            }
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
            metalMap.merge(metal, amount, Double::sum);
            totalAmount += amount;
            updateSanitizedMap();
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
     * Removes an amount of a specific metal ingredient from the alloy.
     * This will NOT removed the completed alloy. Use {@link Alloy#removeAlloy(int, boolean)} instead
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
            int actualRemoveAmount;
            if (currentAmount > removeAmount)
            {
                metalMap.put(metalToRemove, currentAmount - removeAmount);
                actualRemoveAmount = removeAmount;
            }
            else
            {
                actualRemoveAmount = (int) metalMap.remove(metalToRemove).doubleValue();
            }
            totalAmount -= actualRemoveAmount;
            updateSanitizedMap();
            return actualRemoveAmount;
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
            return Math.min(totalAmount, removeAmount);
        }
        if (removeAmount >= totalAmount)
        {
            clear();
            return totalAmount;
        }
        else
        {
            Map<Metal, Double> resultMap = new Object2DoubleOpenHashMap<>(metalMap.size());
            for (Map.Entry<Metal, Double> entry : metalMap.entrySet())
            {
                // Remove the amount of metal from each component, add the remainder (if it exists) into the result map
                double remove = removeAmount * entry.getValue() / totalAmount;
                if (entry.getValue() > remove)
                {
                    resultMap.put(entry.getKey(), entry.getValue() - remove);
                }
            }
            totalAmount -= removeAmount;
            metalMap.clear();
            metalMap.putAll(resultMap);
            updateSanitizedMap();
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
     * Returns a read-only copy of the metals in an alloy
     * The alloy may also contain values with a % content less than epsilon, which are not visible in this view
     *
     * @return a map of metals -> unit values
     */
    public Map<Metal, Double> getMetals()
    {
        return sanitizedMetalMap;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
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
            updateSanitizedMap();
        }
    }

    /**
     * Resets the alloy
     */
    private void clear()
    {
        metalMap.clear();
        totalAmount = 0;
    }

    /**
     * The sanitized map is kept as a read-only view of the current alloy, with values < epsilon excluded
     */
    private void updateSanitizedMap()
    {
        sanitizedMetalMap.clear();
        metalMap.forEach((metal, value) -> {
            if (value > totalAmount * EPSILON)
            {
                sanitizedMetalMap.put(metal, value);
            }
        });
    }

    private boolean matchesRecipe(AlloyRecipe recipe)
    {
        if (this.metalMap.containsKey(recipe.getResult()))
        {
            Alloy other = new Alloy().add(this);
            other.remove(recipe.getResult(), Integer.MAX_VALUE);
            return other.matchesRecipeExact(recipe);
        }
        return this.matchesRecipeExact(recipe);
    }

    private boolean matchesRecipeExact(AlloyRecipe recipe)
    {
        // for each metal in the alloy, it needs to satisfy an ingredient
        // for each metal in the recipe, it needs to match with an alloy
        Map<Metal, Double> metals = getMetals();
        for (Metal metal : Sets.union(recipe.getMetals().keySet(), metals.keySet()))
        {
            if (!metals.containsKey(metal) || !recipe.getMetals().containsKey(metal) || !recipe.getMetals().get(metal).test(metals.get(metal) / totalAmount))
            {
                return false;
            }
        }
        return true;
    }
}
