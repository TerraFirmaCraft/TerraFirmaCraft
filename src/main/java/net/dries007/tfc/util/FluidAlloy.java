/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.fluid.FluidContainerInfo;
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

    static
    {
        assert 1d / (1 + MAX_ALLOY) >= EPSILON;
    }

    public static final Codec<FluidAlloy> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.INT.fieldOf("amount").forGetter(c -> c.amount),
        Codec.unboundedMap(BuiltInRegistries.FLUID.byNameCodec(), Codec.DOUBLE)
            .<Object2DoubleMap<Fluid>>xmap(Object2DoubleOpenHashMap::new, e -> e)
            .fieldOf("content")
            .forGetter(c -> c.content)
    ).apply(i, FluidAlloy::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, Map.Entry<Fluid, Double>> ELEMENT_CODEC = StreamCodec.composite(
        ByteBufCodecs.registry(Registries.FLUID), Map.Entry::getKey,
        ByteBufCodecs.DOUBLE, Map.Entry::getValue,
        Map::entry
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidAlloy> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, c -> c.amount,
        ByteBufCodecs.map(
            Object2DoubleOpenHashMap::new,
            ByteBufCodecs.registry(Registries.FLUID),
            ByteBufCodecs.DOUBLE
        ), c -> c.content,
        FluidAlloy::new
    );

    /**
     * @return A new, empty fluid alloy. This is a new instance and is mutable.
     */
    public static FluidAlloy empty()
    {
        return new FluidAlloy(0, new Object2DoubleOpenHashMap<>());
    }

    private Object2DoubleMap<Fluid> content;
    private int amount;

    private @Nullable Object2DoubleMap<Fluid> cachedContent = null; // The cached content of the alloy > EPSILON, or null if it needs to be recomputed
    private @Nullable FluidStack cachedResult = null; // The cached result of the alloy, or null if it needs to be recomputed

    private FluidAlloy(int amount, Object2DoubleMap<Fluid> content)
    {
        this.amount = amount;
        this.content = content;
    }

    /**
     * @return A copy of this fluid alloy with all the same content
     */
    public FluidAlloy copy()
    {
        return new FluidAlloy(amount, new Object2DoubleOpenHashMap<>(content));
    }

    /**
     * @param fluid  The fluid to add to this alloy
     * @param action If the action should just be simulated.
     * @return The amount of fluid that has (or would've) been added
     */
    public int fill(FluidStack fluid, FluidAction action, FluidContainerInfo info)
    {
        if (!info.canContainFluid(fluid))
        {
            return 0; // Cannot contain this fluid
        }
        int amount = fluid.getAmount();
        if (this.amount + amount >= info.fluidCapacity()) // Account for alloy limits
        {
            // Find the amount that can be added
            amount = info.fluidCapacity() - this.amount;
            if (amount <= 0)
            {
                // No more, i.e. totalAmount >= maxAmount
                return 0;
            }
        }
        if (action.execute())
        {
            content.mergeDouble(fluid.getFluid(), amount, Double::sum);
            this.amount += amount;
            clearCaches();
        }
        return amount;
    }

    public FluidStack drain(int removeAmount, FluidAction action)
    {
        return drain(Helpers.getUnsafeRecipeManager(), removeAmount, action);
    }

    public FluidStack drain(Level level, int removeAmount, FluidAction action)
    {
        return drain(level.getRecipeManager(), removeAmount, action);
    }

    private FluidStack drain(RecipeManager recipes, int removeAmount, FluidAction action)
    {
        final FluidStack result = getResult(recipes);
        if (action.simulate())
        {
            // If simulating, only return a fluid stack of the provided amount, and current content
            return result.copyWithAmount(Math.min(amount, removeAmount));
        }
        if (removeAmount >= amount)
        {
            // If extracting more than the alloy contains, simply clear the alloy content and return the
            // total amount contained before clearing
            final int total = amount;
            clear();
            return result.copyWithAmount(total);
        }
        else
        {
            // If extracting a portion, we need to extract the relevant portion from each of the input fluids in the map,
            // remove the total amount, and then return a fluid stack of the extracted amount
            final Object2DoubleMap<Fluid> newContent = new Object2DoubleOpenHashMap<>(content.size());
            for (Object2DoubleMap.Entry<Fluid> entry : content.object2DoubleEntrySet())
            {
                // Remove the amount of metal from each component, add the remainder (if it exists) into the result map
                final double remove = removeAmount * entry.getDoubleValue() / amount;
                if (entry.getDoubleValue() > remove)
                {
                    newContent.put(entry.getKey(), entry.getDoubleValue() - remove);
                }
            }
            amount -= removeAmount;
            content = newContent;
            clearCaches();
            return result.copyWithAmount(removeAmount);
        }
    }

    public FluidStack getResult()
    {
        return getResult(Helpers.getUnsafeRecipeManager());
    }

    public FluidStack getResult(Level level)
    {
        return getResult(level.getRecipeManager());
    }

    private FluidStack getResult(RecipeManager recipes)
    {
        if (cachedResult == null)
        {
            cachedResult = new FluidStack(switch (content.size())
            {
                case 0 -> Fluids.EMPTY;
                case 1 -> content.keySet()
                    .iterator()
                    .next(); // Easy way to get the only metal in the alloy
                default -> {
                    // Invoke a recipe to get the result of this mix
                    final @Nullable AlloyRecipe recipe = AlloyRecipe.get(recipes, this);
                    yield recipe != null ? recipe.result() : TFCFluids.METALS.get(Metal.UNKNOWN).getSource();
                }
            }, amount);
        }
        return cachedResult;
    }

    public int getAmount()
    {
        return amount;
    }

    public boolean isEmpty()
    {
        return amount == 0;
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

        amount = other.amount;
        content = other.content;
        clearCaches();
    }

    public boolean matches(AlloyRecipe recipe)
    {
        if (content.containsKey(recipe.result()))
        {
            // If we contain the result of an alloy, we consider if the content without the result would match the ratios
            // of that alloy.
            final FluidAlloy other = copy();
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
        amount = 0;
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
