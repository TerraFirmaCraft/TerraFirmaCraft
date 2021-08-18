/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class FoodTrait
{
    public static final Map<String, FoodTrait> TRAITS = new HashMap<>();

    public static final FoodTrait SALTED = register("salted", 0.5f);
    public static final FoodTrait BRINED = register("brined", 1.0f); // No decay modifier, required to pickle foods
    public static final FoodTrait PICKLED = register("pickled", 0.5f);
    public static final FoodTrait PRESERVED = register("preserved", 0.5f); // Large / Small vessels
    public static final FoodTrait VINEGAR = register("vinegar", 0.1f); // Used for the state of being sealed in vinegar
    public static final FoodTrait CHARCOAL_GRILLED = register("charcoal_grilled", 1.25f); // Slight debuff from cooking in a charcoal forge
    public static final FoodTrait WOOD_GRILLED = register("wood_grilled", 0.8f); // Slight buff when cooking in a grill
    public static final FoodTrait BURNT_TO_A_CRISP = register("burnt_to_a_crisp", 2.5f); // Cooking food in something that's WAY TOO HOT too cook food in you fool!

    public static FoodTrait register(String name, float decayModifier)
    {
        return TRAITS.computeIfAbsent(name, key -> new FoodTrait(name, decayModifier));
    }

    public static Map<String, FoodTrait> getTraits()
    {
        return TRAITS;
    }

    private final String name;
    private final float decayModifier;
    @Nullable private final String translationKey;

    private FoodTrait(String name, float decayModifier)
    {
        this(name, decayModifier, "tfc.tooltip.food_trait." + name);
    }

    public FoodTrait(String name, float decayModifier, @Nullable String translationKey)
    {
        this.name = name;
        this.decayModifier = decayModifier;
        this.translationKey = translationKey;

        // Require a unique trait
        if (TRAITS.containsKey(name))
        {
            throw new IllegalStateException("There is already a trait with the name '" + name + "'");
        }
        TRAITS.put(name, this);
    }

    public float getDecayModifier()
    {
        return decayModifier;
    }

    public String getName()
    {
        return name;
    }

    /**
     * Adds information about the trait to the food stack
     *
     * @param stack The stack
     * @param text  The tooltip strings
     */
    public void addTraitInfo(ItemStack stack, List<ITextComponent> text)
    {
        if (translationKey != null)
        {
            text.add(new TranslationTextComponent(translationKey));
        }
    }
}
