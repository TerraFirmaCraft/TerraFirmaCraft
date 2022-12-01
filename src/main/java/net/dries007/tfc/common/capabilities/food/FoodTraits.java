/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import net.dries007.tfc.util.Helpers;

@SuppressWarnings("unused")
public class FoodTraits
{
    public static final FoodTrait SALTED = register("salted", 0.5f);
    public static final FoodTrait BRINED = register("brined", 1.0f); // No decay modifier, required to pickle foods
    public static final FoodTrait PICKLED = register("pickled", 0.5f);
    public static final FoodTrait PRESERVED = register("preserved", 0.5f); // Large / Small vessels
    public static final FoodTrait VINEGAR = register("vinegar", 0.1f); // Used for the state of being sealed in vinegar
    public static final FoodTrait CHARCOAL_GRILLED = register("charcoal_grilled", 1.25f); // Slight debuff from cooking in a charcoal forge
    public static final FoodTrait WOOD_GRILLED = register("wood_grilled", 0.8f); // Slight buff when cooking in a grill
    public static final FoodTrait BURNT_TO_A_CRISP = register("burnt_to_a_crisp", 2.5f); // Cooking food in something that's WAY TOO HOT too cook food in you fool!
    public static final FoodTrait WILD = register("wild", 0.5f); // wild pumpkins last a bit longer, just in case you don't see them right away.

    public static void registerFoodTraits() { }

    private static FoodTrait register(String name, float decayModifier)
    {
        return FoodTrait.register(Helpers.identifier(name), new FoodTrait(decayModifier, "tfc.tooltip.food_trait." + name));
    }
}
