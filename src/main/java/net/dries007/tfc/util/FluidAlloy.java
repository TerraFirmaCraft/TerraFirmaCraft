/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.recipes.AlloyRecipe;

/**
 * A fluid-only implementation of a mixing mechanic, which stores multiple internal fluids (which are not individually extractable),
 * but respects recipes that convert ratios of fluids to a new fluid.
 */
public final class FluidAlloy
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

    public static final Codec<FluidAlloy> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.INT.optionalFieldOf("max_units", MAX_ALLOY).forGetter(c -> c.maxUnits),
        Codec.INT.fieldOf("units").forGetter(c -> c.totalUnits),
        Codec.unboundedMap(BuiltInRegistries.FLUID.byNameCodec(), Codec.DOUBLE)
            .<Object2DoubleMap<Fluid>>xmap(Object2DoubleOpenHashMap::new, e -> e)
            .fieldOf("content")
            .forGetter(c -> c.content)
    ).apply(i, FluidAlloy::new));

    static
    {
        assert 1d / (1 + MAX_ALLOY) >= EPSILON;
    }

    private static Fluid unknown()
    {
        return TFCFluids.METALS.get(Metal.UNKNOWN).getSource();
    }

    private Object2DoubleMap<Fluid> content;
    private int totalUnits;
    private int maxUnits;

    private @Nullable Object2DoubleMap<Fluid> cachedContent = null; // The cached content of the alloy > EPSILON, or null if it needs to be recomputed
    private @Nullable Fluid cachedResult = null; // The cached result of the alloy, or null if it needs to be recomputed

    /**
     * Constructs a new alloy with a maximum possible capacity
     */
    public FluidAlloy()
    {
        this(MAX_ALLOY);
    }

    /**
     * Constructs an alloy with a maximum limit. Anything added over this limit will do nothing
     *
     * @param maxUnits The maximum alloy amount (in units)
     */
    public FluidAlloy(int maxUnits)
    {
        this(maxUnits, 0, new Object2DoubleOpenHashMap<>());
    }

    private FluidAlloy(int maxUnits, int totalUnits, Object2DoubleMap<Fluid> content)
    {
        this.maxUnits = maxUnits;
        this.totalUnits = totalUnits;
        this.content = content;
    }

    /**
     * Add the contents of another alloy {@code other} to this alloy.
     */
    public void add(FluidAlloy other)
    {
        final int newTotalAmount = totalUnits + other.totalUnits;

        double keepRatio = 1;
        if (newTotalAmount > maxUnits)
        {
            // Some will overflow - need to add a percentage of the other alloy
            keepRatio = (double) (maxUnits - totalUnits) / other.totalUnits;
        }

        // Directly add the other alloy exact values. This is important as it needs to not round floating point alloy amounts
        totalUnits += other.totalUnits;
        for (Object2DoubleMap.Entry<Fluid> entry : other.content.object2DoubleEntrySet())
        {
            content.mergeDouble(entry.getKey(), keepRatio * entry.getDoubleValue(), Double::sum);
        }
        clearCaches();
    }

    /**
     * @param fluid  The fluid to add to this alloy
     * @param action If the action should just be simulated.
     * @return The amount of fluid that has (or would've) been added
     */
    public int add(FluidStack fluid, FluidAction action)
    {
        int amount = fluid.getAmount();
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
        if (action.execute())
        {
            content.mergeDouble(fluid.getFluid(), amount, Double::sum);
            totalUnits += amount;
            clearCaches();
        }
        return amount;
    }

    public FluidStack extract(RecipeManager recipes, int removeAmount, FluidAction action)
    {
        final Fluid result = getResult(recipes);
        if (action.simulate())
        {
            // If simulating, only return a fluid stack of the provided amount, and current content
            return new FluidStack(result, Math.min(totalUnits, removeAmount));
        }
        if (removeAmount >= totalUnits)
        {
            // If extracting more than the alloy contains, simply clear the alloy content and return the
            // total amount contained before clearing
            final int total = totalUnits;
            clear();
            return new FluidStack(result, total);
        }
        else
        {
            // If extracting a portion, we need to extract the relevant portion from each of the input fluids in the map,
            // remove the total amount, and then return a fluid stack of the extracted amount
            final Object2DoubleMap<Fluid> newContent = new Object2DoubleOpenHashMap<>(content.size());
            for (Object2DoubleMap.Entry<Fluid> entry : content.object2DoubleEntrySet())
            {
                // Remove the amount of metal from each component, add the remainder (if it exists) into the result map
                final double remove = removeAmount * entry.getDoubleValue() / totalUnits;
                if (entry.getDoubleValue() > remove)
                {
                    newContent.put(entry.getKey(), entry.getDoubleValue() - remove);
                }
            }
            totalUnits -= removeAmount;
            content = newContent;
            clearCaches();
            return new FluidStack(result, removeAmount);
        }
    }

    public FluidStack getResult()
    {
        return new FluidStack(getResult(Helpers.getUnsafeRecipeManager()), totalUnits);
    }

    public FluidStack getResult(Level level)
    {
        return new FluidStack(getResult(level.getRecipeManager()), totalUnits);
    }

    public Fluid getResult(RecipeManager recipes)
    {
        if (cachedResult == null)
        {
            if (content.size() == 1)
            {
                cachedResult = content.keySet()
                    .iterator()
                    .next(); // Easy way to get the only metal in the alloy
            }
            else
            {
                // Invoke a recipe to get the result of this mix
                final @Nullable AlloyRecipe recipe = AlloyRecipe.get(recipes, this);
                cachedResult = recipe != null
                    ? recipe.result()
                    : unknown();
            }
        }
        return cachedResult;
    }

    public int getAmount()
    {
        return totalUnits;
    }

    public int getMaxAmount()
    {
        return maxUnits;
    }

    public boolean isEmpty()
    {
        return totalUnits == 0;
    }

    public Object2DoubleMap<Fluid> getContent()
    {
        if (cachedContent == null)
        {
            double actualTotalAmount = getExactAmount();
            cachedContent = new Object2DoubleOpenHashMap<>(content.size());
            for (Object2DoubleMap.Entry<Fluid> entry : content.object2DoubleEntrySet())
            {
                if (entry.getDoubleValue() > actualTotalAmount * EPSILON)
                {
                    cachedContent.put(entry.getKey(), entry.getDoubleValue());
                }
            }
        }
        return cachedContent;
    }

    public Tag serializeNBT()
    {
        return CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
    }

    public void deserializeNBT(CompoundTag nbt)
    {
        final FluidAlloy other = CODEC.decode(NbtOps.INSTANCE, nbt).getOrThrow().getFirst();

        maxUnits = other.maxUnits;
        totalUnits = other.totalUnits;
        content = other.content;
        clearCaches();
    }

    public boolean matches(AlloyRecipe recipe)
    {
        if (content.containsKey(recipe.result()))
        {
            // If we contain the result of an alloy, we consider if the content without the result would match the ratios
            // of that alloy.
            final FluidAlloy other = new FluidAlloy();
            other.add(this);
            other.content.removeDouble(recipe.result());
            other.clearCaches();
            return other.matchesExactly(recipe);
        }
        return matchesExactly(recipe);
    }

    /**
     * Resets the alloy
     */
    private void clear()
    {
        content.clear();
        totalUnits = 0;
        clearCaches();
    }

    /**
     * Invalidates cached values, must be called on any change to the alloy contents
     */
    private void clearCaches()
    {
        cachedResult = null;
        cachedContent = null;
    }

    private double getExactAmount()
    {
        return content.values().doubleStream().sum();
    }

    private boolean matchesExactly(AlloyRecipe recipe)
    {
        final Object2DoubleMap<Fluid> content = getContent();
        final List<AlloyRange> requirements = recipe.contents();
        final double actualTotalAmount = getExactAmount();

        // Iterate through each range in the requirements, asserting that it is satisfied. If it is, remove
        // the input from the list of inputs not matched
        final Set<Fluid> inputsNotMatched = new HashSet<>(content.keySet());
        for (AlloyRange range : requirements)
        {
            final Fluid fluid = range.fluid();
            if (!content.containsKey(fluid) || !range.isIn(content.getDouble(fluid) / actualTotalAmount))
            {
                return false;
            }

            // Account for this input being in the recipe for the alloy
            inputsNotMatched.remove(fluid);
        }

        // Any excess values that were not matched, but were present in `content`, should have been checked. This makes the alloy
        // invalid at this point. `content` will be filtered to ignore elements that are smaller than `EPSILON`
        return inputsNotMatched.isEmpty();
    }
}
