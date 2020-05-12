/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class CapabilityFood
{
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "food");
    public static final Map<IIngredient<ItemStack>, Supplier<ICapabilityProvider>> CUSTOM_FOODS = new HashMap<>(); //Used inside CT, set custom IFood for food items outside TFC
    /**
     * Most TFC foods have decay modifiers in the range [1, 4] (high = faster decay)
     * That puts decay times at 25% - 100% of this value
     * So meat / fruit will decay in ~5 days, grains take ~20 days
     * Other modifiers are applied on top of that
     */
    public static final int DEFAULT_ROT_TICKS = ICalendar.TICKS_IN_DAY * 22;
    @CapabilityInject(IFood.class)
    public static Capability<IFood> CAPABILITY;

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IFood.class, new DumbStorage<>(), FoodHandler::new);
    }

    public static void init()
    {
        // Add custom vanilla food instances
        CUSTOM_FOODS.put(IIngredient.of(Items.ROTTEN_FLESH), () -> new FoodHandler(null, FoodData.ROTTEN_FLESH));
        CUSTOM_FOODS.put(IIngredient.of(Items.GOLDEN_APPLE), () -> new FoodHandler(null, FoodData.GOLDEN_APPLE));
        CUSTOM_FOODS.put(IIngredient.of(Items.GOLDEN_CARROT), () -> new FoodHandler(null, FoodData.GOLDEN_CARROT));
        CUSTOM_FOODS.put(IIngredient.of(Items.EGG), () -> new FoodHandler(null, FoodData.RAW_EGG));
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

    public static void applyTrait(ItemStack stack, FoodTrait trait)
    {
        IFood food = stack.getCapability(CAPABILITY, null);
        if (!stack.isEmpty() && food != null)
        {
            applyTrait(food, trait);
        }
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

    public static void removeTrait(ItemStack stack, FoodTrait trait)
    {
        IFood food = stack.getCapability(CAPABILITY, null);
        if (!stack.isEmpty() && food != null)
        {
            removeTrait(food, trait);
        }
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
        IFood oldCap = oldStack.getCapability(CapabilityFood.CAPABILITY, null);
        IFood newCap = newStack.getCapability(CapabilityFood.CAPABILITY, null);
        if (oldCap != null && newCap != null)
        {
            // Copy traits from old stack to new stack
            newCap.getTraits().addAll(oldCap.getTraits());
            // Applied trait decay DATE modifier = new / old
            float decayDelta = newCap.getDecayDateModifier() / oldCap.getDecayDateModifier();
            newCap.setCreationDate(calculateNewCreationDate(oldCap.getCreationDate(), decayDelta));
        }
        return newStack;
    }

    /**
     * Call this from any function that is meant to create a new item stack.
     * In MOST cases, you should use {@link CapabilityFood#updateFoodFromPrevious(ItemStack, ItemStack)}, as the decay should transfer from input -> output
     * This is only for where there is no input. (i.e. on a direct {@code stack.copy()} from non-food inputs
     *
     * @param stack the new stack
     * @return the input stack, for chaining
     */
    @SuppressWarnings("unused")
    public static ItemStack updateFoodDecayOnCreate(ItemStack stack)
    {
        IFood cap = stack.getCapability(CapabilityFood.CAPABILITY, null);
        if (cap != null)
        {
            cap.setCreationDate(CalendarTFC.PLAYER_TIME.getTicks());
        }
        return stack;
    }

    public static void setStackNonDecaying(ItemStack stack)
    {
        IFood cap = stack.getCapability(CapabilityFood.CAPABILITY, null);
        if (cap != null)
        {
            cap.setNonDecaying();
        }
    }

    @Nullable
    public static ICapabilityProvider getCustomFood(ItemStack stack)
    {
        Set<IIngredient<ItemStack>> itemFoodSet = CUSTOM_FOODS.keySet();
        for (IIngredient<ItemStack> ingredient : itemFoodSet)
        {
            if (ingredient.testIgnoreCount(stack))
            {
                return CUSTOM_FOODS.get(ingredient).get();
            }
        }
        return null;
    }

    /**
     * @param stackToMergeInto the stack that will grow.
     * @param stackToMerge     the stack that will shrink. Will be modified.
     * @return the result of stackToMergeInto.
     */
    public static ItemStack mergeItemStacksIgnoreCreationDate(ItemStack stackToMergeInto, ItemStack stackToMerge)
    {
        if (!stackToMerge.isEmpty())
        {
            if (!stackToMergeInto.isEmpty())
            {
                if (CapabilityFood.areStacksStackableExceptCreationDate(stackToMergeInto, stackToMerge))
                {
                    IFood mergeIntoFood = stackToMergeInto.getCapability(CapabilityFood.CAPABILITY, null);
                    IFood mergeFood = stackToMerge.getCapability(CapabilityFood.CAPABILITY, null);
                    if (mergeIntoFood != null && mergeFood != null)
                    {
                        int mergeAmount = Math.min(stackToMerge.getCount(), stackToMergeInto.getMaxStackSize() - stackToMergeInto.getCount());
                        if (mergeAmount > 0)
                        {
                            mergeIntoFood.setCreationDate(Math.min(mergeIntoFood.getCreationDate(), mergeFood.getCreationDate()));
                            stackToMerge.shrink(mergeAmount);
                            stackToMergeInto.grow(mergeAmount);
                        }
                    }
                }
            }
            else
            {
                ItemStack stackToMergeCopy = stackToMerge.copy();
                stackToMerge.setCount(0);
                return stackToMergeCopy;
            }
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
        ItemStack stack1Copy = stack1.copy();
        IFood food1 = stack1Copy.getCapability(CapabilityFood.CAPABILITY, null);
        if (food1 != null)
        {
            food1.setCreationDate(0);
        }
        ItemStack stack2Copy = stack2.copy();
        IFood food2 = stack2Copy.getCapability(CapabilityFood.CAPABILITY, null);
        if (food2 != null)
        {
            food2.setCreationDate(0);
        }
        return ItemHandlerHelper.canItemStacksStackRelaxed(stack1Copy, stack2Copy);
    }

    /**
     * @return Gets the creation date to set a piece of food to, in order to stack items created nearby in time
     */
    public static long getRoundedCreationDate()
    {
        return (CalendarTFC.PLAYER_TIME.getTotalHours() / ConfigTFC.General.FOOD.decayStackTime) * ICalendar.TICKS_IN_HOUR * ConfigTFC.General.FOOD.decayStackTime;
    }

    /**
     * T = current time, Ci / Cf = initial / final creation date, Ei / Ef = initial / final expiration date, d = decay time, p = preservation modifier
     *
     * To apply preservation p at time T: want remaining decay fraction to be invariant under preservation
     * Let Ri = (T - Ci) / (Ei - Ci) = (T - Ci) / d, Rf = (T - Cf) / (d * p)
     * Then if Ri = Rf
     * => d * p * (T - Ci) = d * (T - Cf)
     * => Cf = (1 - p) * T + p * Ci (affine combination)
     *
     * In order to show that E > T is invariant under preservation: (i.e. see TerraFirmaCraft#352)
     * Let T, Ci, Ei, d, p > 0 such that Ei > T (1.), and Ei = Ci + d
     * Cf = (1 - p) * T + p * Ci
     * => Ef = Cf + p * d
     * = (1 - p) * T + p * Ci + p * d
     * = (1 - p) * T + p * (Ci + d)
     * via 1. > (1 - p) * T + p * T = T
     * QED
     *
     * @param ci The initial creation date
     * @param p  The decay date modifier (1 / standard decay modifier)
     * @return cf the final creation date
     */
    private static long calculateNewCreationDate(long ci, float p)
    {
        // Cf = (1 - p) * T + p * Ci
        return (long) ((1 - p) * CalendarTFC.PLAYER_TIME.getTicks() + p * ci);
    }
}
