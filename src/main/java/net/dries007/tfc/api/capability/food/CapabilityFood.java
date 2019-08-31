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

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class CapabilityFood
{
    @CapabilityInject(IFood.class)
    public static final Capability<IFood> CAPABILITY = Helpers.getNull();
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "food");

    public static final Map<IIngredient<ItemStack>, Supplier<ICapabilityProvider>> CUSTOM_FOODS = new HashMap<>(); //Used inside CT, set custom IFood for food items outside TFC

    /**
     * Most TFC foods have decay modifiers in the range [1, 4] (high = faster decay)
     * That puts decay times at 25% - 100% of this value
     * So meat / fruit will decay in ~4 days, grains take ~16 days
     * Other modifiers are applied on top of that
     */
    public static final int DEFAULT_ROT_TICKS = ICalendar.TICKS_IN_DAY * 16;

    public static final IFoodTrait.Impl SMOKED = new IFoodTrait.Impl("smoked", 0.5f);
    public static final IFoodTrait.Impl BRINED = new IFoodTrait.Impl("brined", 0.5f);
    public static final IFoodTrait.Impl SALTED = new IFoodTrait.Impl("salted", 0.75f);
    public static final IFoodTrait.Impl PICKLED = new IFoodTrait.Impl("pickled", 0.75f);
    public static final IFoodTrait.Impl PRESERVED = new IFoodTrait.Impl("preserved", 0.5f);

    private static final Map<String, IFoodTrait> TRAITS = new HashMap<>();

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IFood.class, new DumbStorage<>(), FoodHandler::new);

        TRAITS.put("smoked", SMOKED);
        TRAITS.put("brined", BRINED);
        TRAITS.put("salted", SALTED); // todo: In 1.7.10 this was 0.5 for uncooked meat, 0.75 for cooked. Requires a custom class.
        TRAITS.put("pickled", PICKLED); // todo: same as above
        TRAITS.put("preserved", PRESERVED);
    }

    public static void init()
    {
        // Add custom vanilla food instances

        CUSTOM_FOODS.put(IIngredient.of(Items.ROTTEN_FLESH), () -> new FoodHandler(null, new float[] {0, 0, 0, 0, 0}, 0, 0, Float.POSITIVE_INFINITY));
    }

    public static Map<String, IFoodTrait> getTraits()
    {
        return TRAITS;
    }

    /**
     * Helper method to handle applying a trait to a food item.
     * Do NOT just directly apply the trait, as that can lead to strange interactions with decay dates / creation dates
     * This calculates a creation date that interpolates between no preservation (if the food is rotten), to full preservation (if the food is new)
     * Trust me, this works. (Thank CtrlAltDavid for figuring it out)
     */
    public static void applyTrait(IFood instance, IFoodTrait trait)
    {
        if (!instance.getTraits().contains(trait))
        {
            if (!instance.isRotten())
            {
                instance.setCreationDate(calculateNewCreationDate(instance.getCreationDate(), 1f / trait.getDecayModifier()));
            }
            instance.getTraits().add(trait);
        }
    }

    /**
     * Helper method to handle removing a trait to a food item.
     * Do NOT just directly remove the trait, as that can lead to strange interactions with decay dates / creation dates
     */
    public static void removeTrait(IFood instance, IFoodTrait trait)
    {
        if (instance.getTraits().contains(trait))
        {
            if (!instance.isRotten())
            {
                instance.setCreationDate(calculateNewCreationDate(instance.getCreationDate(), trait.getDecayModifier()));
            }
            instance.getTraits().remove(trait);
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
    public static ItemStack updateFoodDecay(ItemStack oldStack, ItemStack newStack)
    {
        IFood oldCap = oldStack.getCapability(CapabilityFood.CAPABILITY, null);
        IFood newCap = newStack.getCapability(CapabilityFood.CAPABILITY, null);
        if (oldCap != null && newCap != null)
        {
            // This is similar to the trait applied, except it's the inverse, since decay mod performs a 1 / x
            float decayDelta = oldCap.getDecayModifier() / newCap.getDecayModifier();
            newCap.setCreationDate(calculateNewCreationDate(oldCap.getCreationDate(), decayDelta));
        }
        return newStack;
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
     */
    private static long calculateNewCreationDate(long ci, float p)
    {
        // Cf = (1 - p) * T + p * Ci
        return (long) ((1 - p) * CalendarTFC.PLAYER_TIME.getTicks() + p * ci);
    }
}
