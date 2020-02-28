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

import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class CapabilityFood
{
    @CapabilityInject(IFood.class)
    public static final Capability<IFood> CAPABILITY = Helpers.getNull();
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "food");

    public static final Map<IIngredient<ItemStack>, Supplier<ICapabilityProvider>> CUSTOM_FOODS = new HashMap<>(); //Used inside CT, set custom IFood for food items outside TFC

    /**
     * Most TFC foods have decay modifiers in the range [1, 4] (high = faster decay)
     * That puts decay times at 25% - 100% of this value
     * So meat / fruit will decay in ~5 days, grains take ~20 days
     * Other modifiers are applied on top of that
     */
    public static final int DEFAULT_ROT_TICKS = ICalendar.TICKS_IN_DAY * 22;

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IFood.class, new DumbStorage<>(), FoodHandler::new);
    }

    public static void init()
    {
        // Add custom vanilla food instances
        CUSTOM_FOODS.put(IIngredient.of(Items.ROTTEN_FLESH), () -> new FoodHandler(null, new float[] {0, 0, 0, 0, 0}, 0, 0, Float.POSITIVE_INFINITY));
        CUSTOM_FOODS.put(IIngredient.of(Items.GOLDEN_APPLE), () -> new FoodHandler(null, new float[] {0, 0, 2, 2, 0}, 3, 12, 0));
        CUSTOM_FOODS.put(IIngredient.of(Items.GOLDEN_CARROT), () -> new FoodHandler(null, new float[] {0.5f, 0, 1.2f, 1.2f, 0}, 2, 5, 0));
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

    public static ItemStack setStackNonDecaying(ItemStack stack)
    {
        IFood cap = stack.getCapability(CapabilityFood.CAPABILITY, null);
        if (cap != null)
        {
            cap.setNonDecaying();
        }
        return stack;
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
     * Merge two food itemstacks into one, if possible
     *
     * @param inputStack the input stack to be merged
     * @param mergeStack the output stack that will receive the merging operation
     * @return ItemStack.EMPTY if everything was merged or leftover inputStack
     */
    public static ItemStack mergeStack(ItemStack inputStack, ItemStack mergeStack)
    {
        if (!inputStack.isEmpty() && !mergeStack.isEmpty()
            && mergeStack.getCount() < mergeStack.getMaxStackSize()
            && mergeStack.isItemEqual(inputStack))
        {
            IFood food1Cap = inputStack.getCapability(CapabilityFood.CAPABILITY, null);
            IFood food2Cap = mergeStack.getCapability(CapabilityFood.CAPABILITY, null);
            if (food1Cap != null && food2Cap != null)
            {
                long fallbackDate1 = food1Cap.getCreationDate();
                long fallbackDate2 = food2Cap.getCreationDate();
                long earliest = Math.min(fallbackDate1, fallbackDate2);
                food1Cap.setCreationDate(earliest);
                food2Cap.setCreationDate(earliest);

                // Tried using ItemHandlerHelper#canItemStacksStack(mergeStack, inputStack)
                // but for some reason, most of the times ItemStack#areCapsCompatible returned false
                // Even when both had the exactly same capability serialization (eg: the bellow function returns true).
                // For now, the bellow check works as intended.
                if (food1Cap.serializeNBT().equals(food2Cap.serializeNBT()))
                {
                    int merge = Math.min(mergeStack.getMaxStackSize(), mergeStack.getCount() + inputStack.getCount());
                    inputStack.shrink(merge - mergeStack.getCount());
                    mergeStack.setCount(merge);

                    if (inputStack.isEmpty())
                    {
                        // Successfully merged into mergeStack
                        return ItemStack.EMPTY;
                    }
                    else
                    {
                        // Could merge partially, reverting only the remainder inputStack
                        food1Cap.setCreationDate(fallbackDate1);
                    }
                }
                else
                {
                    // Can't stack even after creation date update, reverting
                    food1Cap.setCreationDate(fallbackDate1);
                    food2Cap.setCreationDate(fallbackDate2);
                }
            }
        }
        return inputStack;
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
