/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import java.util.Collection;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.util.DataManager;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public final class FoodCapability
{
    public static final Capability<IFood> CAPABILITY = Helpers.capability(new CapabilityToken<>() {});
    public static final ResourceLocation KEY = Helpers.identifier("food");
    public static final DataManager<FoodDefinition> MANAGER = new DataManager<>(Helpers.identifier("food_items"), "food", FoodDefinition::new, FoodDefinition::new, FoodDefinition::encode, Packet::new);
    public static final IndirectHashCollection<Item, FoodDefinition> CACHE = IndirectHashCollection.create(FoodDefinition::getValidItems, MANAGER::getValues);

    @Nullable
    public static FoodDefinition get(ItemStack stack)
    {
        for (FoodDefinition def : CACHE.getAll(stack.getItem()))
        {
            if (def.matches(stack))
            {
                return def;
            }
        }
        return null;
    }

    /**
     * Helper method to handle applying a trait to a food item.
     * Do NOT just directly apply the trait, as that can lead to strange interactions with decay dates / creation dates
     * This calculates a creation date that interpolates between no preservation (if the food is rotten), to full preservation (if the food is new)
     */
    public static void applyTrait(IFood instance, FoodTrait trait)
    {
        if (!instance.getTraits().contains(trait))
        {
            if (!instance.isRotten())
            {
                // Applied decay DATE modifier = 1 / decay mod
                instance.setCreationDate(calculateNewCreationDate(instance.getCreationDate(), 1f / trait.getDecayModifier()));
            }
            instance.getTraits().add(trait);
        }
    }

    public static ItemStack applyTrait(ItemStack stack, FoodTrait trait)
    {
        stack.getCapability(FoodCapability.CAPABILITY).ifPresent(food -> applyTrait(food, trait));
        return stack;
    }

    /**
     * Helper method to handle removing a trait to a food item.
     * Do NOT just directly remove the trait, as that can lead to strange interactions with decay dates / creation dates
     */
    public static void removeTrait(IFood instance, FoodTrait trait)
    {
        if (instance.getTraits().contains(trait))
        {
            if (!instance.isRotten())
            {
                // Removed trait = 1 / apply trait
                instance.setCreationDate(calculateNewCreationDate(instance.getCreationDate(), trait.getDecayModifier()));
            }
            instance.getTraits().remove(trait);
        }
    }

    public static ItemStack removeTrait(ItemStack stack, FoodTrait trait)
    {
        stack.getCapability(FoodCapability.CAPABILITY).ifPresent(food -> removeTrait(food, trait));
        return stack;
    }

    /**
     * Sets the given item stack to rotten, if possible.
     */
    public static ItemStack setRotten(ItemStack stack)
    {
        stack.getCapability(FoodCapability.CAPABILITY).ifPresent(food -> food.setCreationDate(FoodHandler.ROTTEN_DATE));
        return stack;
    }

    /**
     * This is used to update a stack from an old stack, in the case where a food is created from another
     * Any method that creates derivative food should call this, as it avoids extending the decay of the item
     * If called with non food items, nothing happens
     *
     * @param oldStack the old stack
     * @param newStack the new stack
     * @return the modified stack, for chaining
     */
    public static ItemStack updateFoodFromPrevious(ItemStack oldStack, ItemStack newStack)
    {
        oldStack.getCapability(FoodCapability.CAPABILITY).ifPresent(oldFood ->
            newStack.getCapability(FoodCapability.CAPABILITY).ifPresent(newFood -> {
                // Copy traits from old stack to new stack
                newFood.getTraits().addAll(oldFood.getTraits());
                // Applied trait decay DATE modifier = new / old
                float decayDelta = newFood.getDecayDateModifier() / oldFood.getDecayDateModifier();
                newFood.setCreationDate(calculateNewRoundedCreationDate(oldFood.getCreationDate(), decayDelta));
            }));

        return newStack;
    }

    /**
     * Like {@link #updateFoodFromPrevious(ItemStack, ItemStack)}, however this method has two key differences.
     * First, it updates the food from a collection of previous stacks, not just a single stack.
     * Second, it only updates the creation date, and takes the oldest of all possible creation dates and averages the decay modifiers. It does not copy traits from any of the old stacks to the new stack.
     * It should generally only be used in a situation where all the old stacks are of the same type.
     */
    public static ItemStack updateFoodFromAllPrevious(Collection<ItemStack> oldStacks, ItemStack newStack)
    {
        newStack.getCapability(FoodCapability.CAPABILITY).ifPresent(newFood -> {
            float decayDateModifier = 0;
            long oldCreationDate = Long.MAX_VALUE;
            int oldFoodCount = 0;
            for (ItemStack oldStack : oldStacks)
            {
                final IFood oldFood = Helpers.getCapability(oldStack, FoodCapability.CAPABILITY);
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
                newFood.setCreationDate(calculateNewCreationDate(oldCreationDate, decayDelta));
            }
        });
        return newStack;
    }

    /**
     * Call this from any function that is meant to create a new item stack.
     * In MOST cases, you should use {@link FoodCapability#updateFoodFromPrevious(ItemStack, ItemStack)}, as the decay should transfer from input -> output
     * This is only for where there is no input. (i.e. on a direct {@code stack.copy()} from non-food inputs
     *
     * @param stack the new stack
     * @return the input stack, for chaining
     */
    @SuppressWarnings("unused")
    public static ItemStack updateFoodDecayOnCreate(ItemStack stack)
    {
        stack.getCapability(FoodCapability.CAPABILITY).ifPresent(food -> food.setCreationDate(getRoundedCreationDate()));
        return stack;
    }

    public static void setCreativeTabsNonDecaying()
    {
        for (CreativeModeTab tab : CreativeModeTab.TABS)
        {
            final ItemStack stack;
            try
            {
                stack = tab.getIconItem();
            }
            catch (AbstractMethodError e)
            {
                TerraFirmaCraft.LOGGER.error("I cannot believe I am having to catch this error.");
                TerraFirmaCraft.LOGGER.error("Another mod seemingly annotated makeIcon() as @OnlyIn(Dist.CLIENT)");
                TerraFirmaCraft.LOGGER.error("THIS IS A BUG IN {}! I'm just the unlucky sod to discover it.", tab.getRecipeFolderName());
                TerraFirmaCraft.LOGGER.error("This is the problem:", e);
                continue;
            }
            setStackNonDecaying(stack);
        }
    }

    public static ItemStack setStackNonDecaying(ItemStack stack)
    {
        stack.getCapability(FoodCapability.CAPABILITY).ifPresent(IFood::setNonDecaying);
        return stack;
    }

    /**
     * Creates a box of an item stack, that we want to always not decay.
     * The reason we can't evaluate this immediately is because the stack may be created at a time when tags (and as a result, capabilities) don't exist yet
     */
    public static Supplier<ItemStack> createNonDecayingStack(ItemStack stack)
    {
        return new NonDecayingItemStack(stack);
    }

    public static void markRecipeOutputsAsNonDecaying()
    {
        for (Recipe<?> recipe : Helpers.getUnsafeRecipeManager().getRecipes())
        {
            FoodCapability.setStackNonDecaying(recipe.getResultItem());
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
            final IFood mergeIntoFood = stackToMergeInto.getCapability(FoodCapability.CAPABILITY).resolve().orElse(null);
            final IFood mergeFood = stackToMerge.getCapability(FoodCapability.CAPABILITY).resolve().orElse(null);
            if (mergeIntoFood != null && mergeFood != null)
            {
                mergeIntoFood.setCreationDate(Math.min(mergeIntoFood.getCreationDate(), mergeFood.getCreationDate()));
            }

            final int mergeAmount = Math.min(stackToMerge.getCount(), stackToMergeInto.getMaxStackSize() - stackToMergeInto.getCount());
            stackToMerge.shrink(mergeAmount);
            stackToMergeInto.grow(mergeAmount);
        }
        return stackToMergeInto;
    }

    /**
     * This is a nice way of checking if two stacks are stackable, ignoring the creation date: copy both stacks, give them the same creation date, then check compatibility
     * This will also not stack stacks which have different traits, which is intended
     *
     * @return true if the stacks are otherwise stackable ignoring their creation date
     */
    public static boolean areStacksStackableExceptCreationDate(ItemStack stack1, ItemStack stack2)
    {
        // This is a nice way of checking if two stacks are stackable, ignoring the creation date: copy both stacks, give them the same creation date, then check compatibility
        // This will also not stack stacks which have different traits, which is intended
        final ItemStack stack1Copy = stack1.copy(), stack2Copy = stack2.copy();
        final long date = Calendars.get().getTicks();
        stack1Copy.getCapability(FoodCapability.CAPABILITY).ifPresent(food -> food.setCreationDate(date));
        stack2Copy.getCapability(FoodCapability.CAPABILITY).ifPresent(food -> food.setCreationDate(date));
        return ItemHandlerHelper.canItemStacksStack(stack1Copy, stack2Copy);
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
    private static long calculateNewCreationDate(long ci, float p)
    {
        // Cf = (1 - p) * T + p * Ci
        return (long) ((1 - p) * Calendars.get().getTicks() + p * ci);
    }

    public static class Packet extends DataManagerSyncPacket<FoodDefinition> {}

    private record NonDecayingItemStack(ItemStack internal) implements Supplier<ItemStack>
    {
        @Override
        public ItemStack get()
        {
            return FoodCapability.setStackNonDecaying(internal);
        }
    }
}
