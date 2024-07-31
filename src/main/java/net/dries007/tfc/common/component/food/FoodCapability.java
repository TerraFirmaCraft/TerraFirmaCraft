/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.food;

import java.util.Collection;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManager;

public final class FoodCapability
{
    public static final DataManager<FoodDefinition> MANAGER = new DataManager<>(Helpers.identifier("food"), FoodDefinition.CODEC, FoodDefinition.STREAM_CODEC);
    public static final IndirectHashCollection<Item, FoodDefinition> CACHE = IndirectHashCollection.create(r -> RecipeHelpers.itemKeys(r.ingredient()), MANAGER::getValues);

    /**
     * Most TFC foods have decay modifiers in the range [1, 4] (high = faster decay)
     * That puts decay times at 25% - 100% of this value
     * So meat / fruit will decay in ~5 days, grains take ~20 days
     * Other modifiers are applied on top of that
     */
    public static final int DEFAULT_DECAY_TICKS = ICalendar.TICKS_IN_DAY * 22;

    /**
     * @return An immutable view of the food component on an item stack.
     */
    @Nullable
    public static IFood get(ItemStack stack)
    {
        return stack.get(TFCComponents.FOOD);
    }

    public static boolean has(ItemStack stack)
    {
        return stack.has(TFCComponents.FOOD);
    }

    @Nullable
    public static FoodDefinition getDefinition(ItemStack stack)
    {
        return RecipeHelpers.getRecipe(CACHE, stack, stack.getItem());
    }

    public static void setFoodForDynamicItemOnCreate(ItemStack stack, FoodData data)
    {
        final @Nullable FoodComponent food = stack.get(TFCComponents.FOOD);
        if (food != null)
        {
            stack.set(TFCComponents.FOOD, food.with(data, getRoundedCreationDate()));
        }
    }

    /**
     * Applies {@code trait} to {@code stack}, and updates the creation date to preserve the decay proportion of the food.
     */
    public static ItemStack applyTrait(ItemStack stack, Holder<FoodTrait> trait)
    {
        final @Nullable FoodComponent food = stack.get(TFCComponents.FOOD);
        if (food != null && !food.hasTrait(trait) && !food.isRotten())
        {
            stack.set(TFCComponents.FOOD, food.withTraitApplied(
                trait,
                calculateNewCreationDate(food.getCreationDate(), 1f / trait.value().getDecayModifier())
            ));
        }
        return stack;
    }

    /**
     * Removes {@code trait} from {@code stack}, and updates the creation date to preserve the decay proportion of the food.
     */
    public static ItemStack removeTrait(ItemStack stack, Holder<FoodTrait> trait)
    {
        final @Nullable FoodComponent food = stack.get(TFCComponents.FOOD);
        if (food != null && food.hasTrait(trait) && !food.isRotten())
        {
            stack.set(TFCComponents.FOOD, food.withTraitRemoved(
                trait,
                calculateNewCreationDate(food.getCreationDate(), trait.value().getDecayModifier())
            ));
        }
        return stack;
    }

    /**
     * @return {@code true} if the {@code stack} is a food and has {@code trait}.
     */
    public static boolean hasTrait(ItemStack stack, Holder<FoodTrait> trait)
    {
        final @Nullable FoodComponent food = stack.get(TFCComponents.FOOD);
        return food != null && food.hasTrait(trait);
    }

    /**
     * @return {@code true} if the {@code stack} is a food and is rotten.
     */
    public static boolean isRotten(ItemStack stack)
    {
        final @Nullable FoodComponent food = stack.get(TFCComponents.FOOD);
        return food != null && food.isRotten();
    }

    public static void addTooltipInfo(ItemStack stack, Consumer<Component> tooltip)
    {
        final @Nullable FoodComponent food = stack.get(TFCComponents.FOOD);
        if (food != null)
        {
            food.addTooltipInfo(stack, tooltip);
        }
    }

    /**
     * Sets the creation date of the food directly, if one exists. If trying to apply preservation, prefer using the {@link FoodTrait}
     * mechanics rather than directly modifying the creation date of foods.
     *
     * @param stack An item stack
     * @param date A creation date
     * @return The original stack
     *
     * @see #setRotten(ItemStack)
     * @see #setNonDecaying(ItemStack)
     * @see #setCreatedNow(ItemStack)
     */
    public static ItemStack setCreationDate(ItemStack stack, long date)
    {
        // todo: 1.21, is there a more graceful way to handle this?
        if (!TFCComponents.FOOD.holder().isBound()) return stack; // Don't do anything if invoked way too early
        final @Nullable FoodComponent food = stack.get(TFCComponents.FOOD);
        if (food != null)
        {
            stack.set(TFCComponents.FOOD, food.with(date));
        }
        return stack;
    }

    /**
     * Sets the creation date of the item to now. This is used when creating new food items from non-food inputs. When creating food from other food,
     * in general you should use the overloads which take previous item(s) as input, to copy from.
     */
    public static ItemStack setCreatedNow(ItemStack stack)
    {
        return setCreationDate(stack, getRoundedCreationDate());
    }

    /**
     * Sets the food to transiently non-decaying, indicating that the food will not decay, this status should be cleared on copy, and it should
     * not show any "Never Expires" tooltip.
     *
     * @see IFood#TRANSIENT_NEVER_DECAY_FLAG
     */
    public static ItemStack setTransientNonDecaying(ItemStack stack)
    {
        return setCreationDate(stack, IFood.TRANSIENT_NEVER_DECAY_FLAG);
    }

    /**
     * Sets the food to invisibly non-decaying, indicating the food will not decay, and it will not show any decay related information in tooltips
     * @see IFood#INVISIBLE_NEVER_DECAY_FLAG
     */
    public static ItemStack setInvisibleNonDecaying(ItemStack stack)
    {
        return setCreationDate(stack, IFood.INVISIBLE_NEVER_DECAY_FLAG);
    }

    /**
     * Sets the given item stack to never expire, including showing a "Never Expires" tooltip. This is used for items that are
     * meant to not expire and are player-visible (i.e. golden apples).
     * @see IFood#NEVER_DECAY_FLAG
     */
    public static ItemStack setNonDecaying(ItemStack stack)
    {
        return setCreationDate(stack, IFood.NEVER_DECAY_FLAG);
    }

    /**
     * Sets the food to rotten
     * @see IFood#ROTTEN_FLAG
     */
    public static ItemStack setRotten(ItemStack stack)
    {
        return setCreationDate(stack, IFood.ROTTEN_FLAG);
    }

    /**
     * Sets the creation date of {@code newStack} based on the creation date and decay of {@code oldStack}, and also copies any
     * {@link FoodTrait}s from the {@code oldStack} to {@code newStack}. This preserves the relative decay between the two items.
     */
    public static ItemStack updateFoodFromPrevious(ItemStack oldStack, ItemStack newStack)
    {
        final @Nullable FoodComponent oldFood = oldStack.get(TFCComponents.FOOD);
        final @Nullable FoodComponent newFood = newStack.get(TFCComponents.FOOD);
        if (oldFood != null && newFood != null)
        {
            // Copy traits from old stack to new stack - we do this first, so we can calculate a proper decay modifier with both traits
            final FoodComponent tempFood = newFood.withTraitsApplied(oldFood.getTraits());
            final float decayDelta = tempFood.getDecayDateModifier() / oldFood.getDecayDateModifier();

            newStack.set(TFCComponents.FOOD, tempFood.with(calculateNewRoundedCreationDate(oldFood.getCreationDate(), decayDelta)));
        }
        return newStack;
    }

    /**
     * Sets the creation date of {@code newStack} based on the creation date and decay of {@code oldStacks}. This will take the average of all
     * previous decay modifiers and will not copy traits. It generally makes sense to be used when the {@code oldStacks} are all the same type.
     * <p>
     * If no old stacks are present, this will simply update the food as if it was newly created.
     */
    public static ItemStack updateFoodFromAllPrevious(Collection<ItemStack> oldStacks, ItemStack newStack)
    {
        final @Nullable FoodComponent newFood = newStack.get(TFCComponents.FOOD);
        if (newFood != null)
        {
            if (oldStacks.isEmpty())
            {
                return newStack;
            }

            float decayDateModifier = 0;
            long oldCreationDate = Long.MAX_VALUE;
            int oldFoodCount = 0;
            for (ItemStack oldStack : oldStacks)
            {
                final @Nullable FoodComponent oldFood = oldStack.get(TFCComponents.FOOD);
                if (oldFood != null)
                {
                    decayDateModifier += oldFood.getDecayDateModifier();
                    oldCreationDate = Math.min(oldCreationDate, oldFood.getCreationDate());
                    oldFoodCount++;
                }
            }
            if (oldFoodCount > 0)
            {
                final float decayDelta = oldFoodCount * newFood.getDecayDateModifier() / decayDateModifier;
                newStack.set(TFCComponents.FOOD, newFood.with(calculateNewCreationDate(oldCreationDate, decayDelta)));
            }
        }
        return newStack;
    }

    @SuppressWarnings("ConstantConditions")
    public static void markRecipeOutputsAsNonDecaying(RegistryAccess registryAccess, RecipeManager manager)
    {
        for (RecipeHolder<?> recipe : manager.getRecipes())
        {
            final @Nullable ItemStack stack = recipe.value().getResultItem(registryAccess);
            if (stack != null)
            {
                setTransientNonDecaying(stack);
            }
            else
            {
                TerraFirmaCraft.LOGGER.warn("Other mod issue: recipe with a null getResultItem(), in recipe {} of class {}", recipe.id(), recipe.getClass().getName());
            }
        }
    }

    /**
     * Merges two item stacks with different creation dates, taking the earlier of the two.
     *
     * @param stackToMergeInto the stack to merge into. Not modified.
     * @param stackToMerge     the stack to merge, which will be left with the remainder after merging. Will be modified.
     * @return The merged stack.
     */
    public static ItemStack mergeItemStacks(ItemStack stackToMergeInto, ItemStack stackToMerge)
    {
        if (stackToMerge.isEmpty())
        {
            return stackToMergeInto;
        }
        else if (stackToMergeInto.isEmpty())
        {
            final ItemStack merged = stackToMerge.copy();
            stackToMerge.setCount(0);
            return merged;
        }
        else if (FoodCapability.areStacksStackableExceptCreationDate(stackToMergeInto, stackToMerge))
        {
            final @Nullable FoodComponent mergeIntoFood = stackToMergeInto.get(TFCComponents.FOOD);
            final @Nullable FoodComponent mergeFood = stackToMerge.get(TFCComponents.FOOD);
            if (mergeIntoFood != null && mergeFood != null)
            {
                stackToMergeInto.set(TFCComponents.FOOD, mergeIntoFood.with(Math.min(mergeIntoFood.getCreationDate(), mergeFood.getCreationDate())));
            }

            final int mergeAmount = Math.min(stackToMerge.getCount(), stackToMergeInto.getMaxStackSize() - stackToMergeInto.getCount());
            stackToMerge.shrink(mergeAmount);
            stackToMergeInto.grow(mergeAmount);
        }
        return stackToMergeInto;
    }

    /**
     * This is a nice way of checking if two stacks are stackable, ignoring the creation date: copy both stacks, give them the same creation date, then check compatibility
     * This will also not stack items which have different traits, which is intended
     *
     * @return true if the stacks are otherwise stackable ignoring their creation date
     */
    public static boolean areStacksStackableExceptCreationDate(ItemStack stack1, ItemStack stack2)
    {
        // This is a nice way of checking if two stacks are stackable, ignoring the creation date: copy both stacks, give them the same creation date, then check compatibility
        // This will also not stack items which have different traits, which is intended
        final ItemStack stack1Copy = stack1.copy(), stack2Copy = stack2.copy();
        final long date = Calendars.get().getTicks();

        setCreationDate(stack1Copy, date);
        setCreationDate(stack2Copy, date);

        return ItemStack.isSameItemSameComponents(stack1Copy, stack2Copy);
    }

    /**
     * @return Gets the creation date to set a piece of food to, in order to stack items created nearby in time. Note that {@code getRoundedCreationDate(x) >= x} will always be true.
     */
    public static long getRoundedCreationDate()
    {
        return getRoundedCreationDate(Calendars.get().getTicks());
    }

    public static long getRoundedCreationDate(long tick)
    {
        final int window = TFCConfig.SERVER.foodDecayStackWindow.get() * ICalendar.TICKS_IN_HOUR;
        return ((tick - 1) / window + 1) * window;
    }

    /**
     * @return {@code true} if the food is rotten based on the internal (flagged) creation date, and the decay date modifier
     */
    public static boolean isRotten(long creationDate, float decayDateModifier)
    {
        if (creationDate == IFood.TRANSIENT_NEVER_DECAY_FLAG || creationDate == IFood.NEVER_DECAY_FLAG)
        {
            return false; // Food can never be rotten
        }
        if (creationDate == IFood.ROTTEN_FLAG)
        {
            return true; // Food is permanently rotten
        }
        if (decayDateModifier == Float.POSITIVE_INFINITY)
        {
            return false; // Food can never decay, because it's infinitely preserved
        }
        final long rottenDate = creationDate + (long) (decayDateModifier * DEFAULT_DECAY_TICKS);
        return rottenDate <= Calendars.get().getTicks(); // Otherwise, if it is before the rotten date, it is not rotten
    }

    /**
     * @return The date, given a {@code creationDate} and a {@code decayDateModifier} which is not positive infinity, that the food will rot.
     */
    public static long getRottenDate(long creationDate, float decayDateModifier)
    {
        return creationDate + (long) (decayDateModifier * DEFAULT_DECAY_TICKS);
    }

    private static long calculateNewRoundedCreationDate(long ci, float p)
    {
        return getRoundedCreationDate(calculateNewCreationDate(ci, p));
    }

    /**
     * T = current time, Ci / Cf = initial / final creation date, Ei / Ef = initial / final expiration date, d = decay time, p = preservation modifier.
     * To apply preservation p at time T: want remaining decay fraction to be invariant under preservation
     * <pre>
     * Let Ri = (T - Ci) / (Ei - Ci) = (T - Ci) / d, Rf = (T - Cf) / (d * p)
     * Then if Ri = Rf
     * -> d * p * (T - Ci) = d * (T - Cf)
     * -> Cf = (1 - p) * T + p * Ci
     * </pre>
     * In order to show that E > T is invariant under preservation: (i.e. see TerraFirmaCraft#352)
     * <pre>
     * Let T, Ci, Ei, d, p > 0 such that Ei > T (1.), and Ei = Ci + d
     * Cf = (1 - p) * T + p * Ci
     * -> Ef = Cf + p * d
     * -> (1 - p) * T + p * Ci + p * d
     * -> (1 - p) * T + p * (Ci + d)
     * via 1. > (1 - p) * T + p * T = T
     * QED
     * </pre>
     * @param ci The initial creation date
     * @param p  The decay date modifier (1 / standard decay modifier)
     * @return cf the final creation date, rounded to the nearest hour, for ease of stackability.
     */
    public static long calculateNewCreationDate(long ci, float p)
    {
        // Cf = (1 - p) * T + p * Ci
        return (long) ((1 - p) * Calendars.get().getTicks() + p * ci);
    }
}
