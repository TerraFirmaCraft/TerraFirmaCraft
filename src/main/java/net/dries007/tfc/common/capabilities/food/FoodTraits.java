package net.dries007.tfc.common.capabilities.food;

@SuppressWarnings("unused")
public class FoodTraits
{
    public static final FoodTrait SALTED = FoodTrait.register("salted", 0.5f);
    public static final FoodTrait BRINED = FoodTrait.register("brined", 1.0f); // No decay modifier, required to pickle foods
    public static final FoodTrait PICKLED = FoodTrait.register("pickled", 0.5f);
    public static final FoodTrait PRESERVED = FoodTrait.register("preserved", 0.5f); // Large / Small vessels
    public static final FoodTrait VINEGAR = FoodTrait.register("vinegar", 0.1f); // Used for the state of being sealed in vinegar
    public static final FoodTrait CHARCOAL_GRILLED = FoodTrait.register("charcoal_grilled", 1.25f); // Slight debuff from cooking in a charcoal forge
    public static final FoodTrait WOOD_GRILLED = FoodTrait.register("wood_grilled", 0.8f); // Slight buff when cooking in a grill
    public static final FoodTrait BURNT_TO_A_CRISP = FoodTrait.register("burnt_to_a_crisp", 2.5f); // Cooking food in something that's WAY TOO HOT too cook food in you fool!
}
