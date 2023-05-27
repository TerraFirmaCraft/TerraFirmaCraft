/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import net.minecraftforge.common.ForgeConfigSpec;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

@SuppressWarnings("unused")
public class FoodTraits
{
    public static final FoodTrait SALTED = register("salted", TFCConfig.SERVER.traitSaltedModifier);
    public static final FoodTrait BRINED = register("brined", TFCConfig.SERVER.traitBrinedModifier); // No decay modifier, required to pickle foods
    public static final FoodTrait PICKLED = register("pickled", TFCConfig.SERVER.traitPickledModifier);
    public static final FoodTrait PRESERVED = register("preserved", TFCConfig.SERVER.traitPreservedModifier); // Large / Small vessels
    public static final FoodTrait VINEGAR = register("vinegar", TFCConfig.SERVER.traitVinegarModifier); // Used for the state of being sealed in vinegar
    public static final FoodTrait CHARCOAL_GRILLED = register("charcoal_grilled", TFCConfig.SERVER.traitCharcoalGrilledModifier); // Slight debuff from cooking in a charcoal forge
    public static final FoodTrait WOOD_GRILLED = register("wood_grilled", TFCConfig.SERVER.traitWoodGrilledModifier); // Slight buff when cooking in a grill
    public static final FoodTrait BURNT_TO_A_CRISP = register("burnt_to_a_crisp", TFCConfig.SERVER.traitBurntToACrispModifier); // Cooking food in something that's WAY TOO HOT too cook food in you fool!
    public static final FoodTrait WILD = register("wild", TFCConfig.SERVER.traitWildModifier); // wild pumpkins last a bit longer, just in case you don't see them right away.

    public static void registerFoodTraits() { }

    private static FoodTrait register(String name, ForgeConfigSpec.DoubleValue decayModifier)
    {
        return FoodTrait.register(Helpers.identifier(name), new FoodTrait(() -> decayModifier.get().floatValue(), "tfc.tooltip.food_trait." + name));
    }
}
