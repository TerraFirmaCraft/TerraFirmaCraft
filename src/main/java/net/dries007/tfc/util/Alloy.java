/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Map;
import javax.annotation.Nullable;

import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.server.ServerLifecycleHooks;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.dries007.tfc.common.recipes.AlloyRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.inventory.AlloyInventory;

public class Alloy implements INBTSerializable<CompoundTag>, AlloyView
{
    /**
     * This is the maximum safe value for an alloy.
     * If an alloy is larger than this, due to epsilon based comparisons, the following duplication glitch can be observed:
     * Add {@code MAX_ALLOY} amount of metal A
     * Add 1 unit of metal B
     * Then {@code 1 / (1 + MAX_ALLOY) < EPSILON}, so you can extract {@code (1 + MAX_ALLOY)} units of metal A, effectively transmuting metal B into A.
     */
    public static final int MAX_ALLOY = Integer.MAX_VALUE - 2;

    /**
     * This is the epsilon that alloy ratios are compared against.
     */
    public static final double EPSILON = 1d / (2 + MAX_ALLOY);

    static
    {
        assert 1d / (1 + MAX_ALLOY) >= EPSILON;
    }

    private final Object2DoubleMap<Metal> metalMap, sanitizedMetalMap;
    private int totalUnits;
    private int maxUnits;

    @Nullable private AlloyInventory wrapper; // Lazy, initialized on demand and cached
    @Nullable private Metal cachedResult;
    @Nullable private AlloyView readonlyView; // A read-only view of this alloy

    /**
     * Constructs a new alloy. It starts with no metal content
     */
    public Alloy()
    {
        this(MAX_ALLOY);
    }

    /**
     * Constructs an alloy with a maximum limit. Anything added over this limit will do nothing
     *
     * @param maxUnits The maximum alloy amount (in units)
     */
    public Alloy(int maxUnits)
    {
        this.metalMap = new Object2DoubleOpenHashMap<>();
        this.sanitizedMetalMap = new Object2DoubleOpenHashMap<>();
        this.totalUnits = 0;
        this.maxUnits = maxUnits;
    }

    /**
     * Add the contents of another alloy {@code other} to this alloy.
     */
    public void add(Alloy other)
    {
        int newTotalAmount = totalUnits + other.totalUnits;
        double keepRatio = 1;
        if (newTotalAmount > maxUnits)
        {
            // Some will overflow - need to add a percentage of the other alloy
            keepRatio = (double) (maxUnits - totalUnits) / other.totalUnits;
        }
        // Directly add the other alloy exact values. This is important as it needs to not round floating point alloy amounts
        totalUnits += other.totalUnits;
        for (Map.Entry<Metal, Double> entry : other.metalMap.object2DoubleEntrySet())
        {
            metalMap.mergeDouble(entry.getKey(), keepRatio * entry.getValue(), Double::sum);
        }
        updateCaches();
    }

    /**
     * Adds an {@code amount} of a {@code metal} to an alloy.
     *
     * @param simulate If the action should just be simulated.
     * @return The amount of metal that would (or has) be added.
     */
    public int add(Metal metal, int amount, boolean simulate)
    {
        if (totalUnits + amount >= maxUnits) // Account for alloy limits
        {
            // Find the amount that can be added
            amount = maxUnits - totalUnits;
            if (amount <= 0)
            {
                // No more, i.e. totalAmount >= maxAmount
                return 0;
            }
        }
        if (!simulate)
        {
            metalMap.mergeDouble(metal, amount, Double::sum);
            totalUnits += amount;
            updateCaches();
        }
        return amount;
    }

    @Override
    public Metal getResult()
    {
        if (cachedResult == null)
        {
            if (metalMap.size() == 1)
            {
                cachedResult = metalMap.keySet().iterator().next(); // Easy way to get the only metal in the alloy
            }
            else
            {
                final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                if (server != null)
                {
                    cachedResult = server.getRecipeManager().getRecipeFor(TFCRecipeTypes.ALLOY, getWrapper(), server.overworld())
                        .map(AlloyRecipe::getResult)
                        .orElseGet(Metal::unknown);
                }
            }
        }
        if (cachedResult == null)
        {
            return Metal.unknown();
        }
        return cachedResult;
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
            return Math.min(totalUnits, removeAmount);
        }
        if (removeAmount >= totalUnits)
        {
            clear();
            return totalUnits;
        }
        else
        {
            Object2DoubleMap<Metal> resultMap = new Object2DoubleOpenHashMap<>(metalMap.size());
            for (Object2DoubleMap.Entry<Metal> entry : metalMap.object2DoubleEntrySet())
            {
                // Remove the amount of metal from each component, add the remainder (if it exists) into the result map
                double remove = removeAmount * entry.getDoubleValue() / totalUnits;
                if (entry.getDoubleValue() > remove)
                {
                    resultMap.put(entry.getKey(), entry.getDoubleValue() - remove);
                }
            }
            totalUnits -= removeAmount;
            metalMap.clear();
            metalMap.putAll(resultMap);
            updateCaches();
            return removeAmount;
        }
    }

    @Override
    public int getAmount()
    {
        return totalUnits;
    }

    @Override
    public int getMaxUnits()
    {
        return maxUnits;
    }

    public void setMaxUnits(int value)
    {
        int surplus = getAmount() - value;
        if (surplus > 0)
        {
            removeAlloy(surplus, false);
        }
        maxUnits = value;
    }

    @Override
    public Object2DoubleMap<Metal> getMetals()
    {
        return sanitizedMetalMap;
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("maxUnits", maxUnits);
        nbt.putInt("totalUnits", totalUnits);
        CompoundTag alloys = new CompoundTag();
        for (Object2DoubleMap.Entry<Metal> entry : this.metalMap.object2DoubleEntrySet())
        {
            alloys.putDouble(entry.getKey().getId().toString(), entry.getDoubleValue());
        }
        nbt.put("contents", alloys);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        clear();

        if (nbt.contains("maxUnits"))
        {
            // Don't reset the maxUnits if it's not in the saved data.
            // Since this is set by the constructor and (generally) expected to be constant.
            // When we deserialize for the first time, it's very easy to set it to zero.
            maxUnits = nbt.getInt("maxUnits");
        }
        totalUnits = nbt.getInt("totalUnits");

        final CompoundTag contents = nbt.getCompound("contents");
        for (Metal metal : Metal.MANAGER.getValues())
        {
            String key = metal.getId().toString();
            if (contents.contains(key))
            {
                metalMap.put(metal, contents.getDouble(key));
            }
        }

        updateCaches();
    }

    public AlloyView unmodifiableView()
    {
        if (readonlyView == null)
        {
            readonlyView = new View(this);
        }
        return readonlyView;
    }

    public boolean matches(AlloyRecipe recipe)
    {
        if (metalMap.containsKey(recipe.getResult()))
        {
            // Construct a dummy alloy to compare with. The dummy does not have an accurate total units value, this is intentional.
            Alloy other = new Alloy();
            other.add(this);
            other.metalMap.removeDouble(recipe.getResult());
            other.updateCaches();
            return other.matchesExactly(recipe);
        }
        return matchesExactly(recipe);
    }

    /**
     * Resets the alloy
     */
    private void clear()
    {
        metalMap.clear();
        sanitizedMetalMap.clear();
        totalUnits = 0;
        cachedResult = null;
    }

    /**
     * Updates all cached values.
     * The sanitized map is kept as a read-only view of the current alloy, with values < epsilon excluded.
     */
    private void updateCaches()
    {
        cachedResult = null;

        sanitizedMetalMap.clear();
        double actualTotalAmount = getExactAmount();
        metalMap.object2DoubleEntrySet().forEach(entry -> {
            if (entry.getDoubleValue() > actualTotalAmount * EPSILON)
            {
                sanitizedMetalMap.put(entry.getKey(), entry.getDoubleValue());
            }
        });
    }

    private double getExactAmount()
    {
        return metalMap.values().stream().mapToDouble(x -> x).sum();
    }

    private AlloyInventory getWrapper()
    {
        if (wrapper == null)
        {
            wrapper = new AlloyInventory(this);
        }
        return wrapper;
    }

    private boolean matchesExactly(AlloyRecipe recipe)
    {
        // for each metal in the alloy, it needs to satisfy an ingredient
        // for each metal in the recipe, it needs to match with an alloy
        Map<Metal, Double> metals = getMetals();
        double actualTotalAmount = getExactAmount();
        for (Metal metal : Sets.union(recipe.getRanges().keySet(), metals.keySet()))
        {
            if (!metals.containsKey(metal) || !recipe.getRanges().containsKey(metal) || !recipe.getRanges().get(metal).isIn(metals.get(metal) / actualTotalAmount, EPSILON))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * An unmodifiable view of an alloy
     */
    private static class View implements AlloyView
    {
        private final Alloy alloy;

        private View(Alloy alloy)
        {
            this.alloy = alloy;
        }

        @Override
        public Metal getResult()
        {
            return alloy.getResult();
        }

        @Override
        public int getAmount()
        {
            return alloy.getAmount();
        }

        @Override
        public int getMaxUnits()
        {
            return alloy.getMaxUnits();
        }

        @Override
        public Object2DoubleMap<Metal> getMetals()
        {
            return alloy.getMetals();
        }
    }
}
