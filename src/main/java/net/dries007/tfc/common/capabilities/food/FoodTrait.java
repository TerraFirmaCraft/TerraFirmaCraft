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
    public static final FoodTrait SALTED;
    public static final FoodTrait BRINED; // No decay modifier, required to pickle foods
    public static final FoodTrait PICKLED;
    public static final FoodTrait PRESERVED; // Large / Small vessels
    public static final FoodTrait VINEGAR; // Used for the state of being sealed in vinegar
    public static final FoodTrait CHARCOAL_GRILLED; // Slight debuff from cooking in a charcoal forge
    public static final FoodTrait WOOD_GRILLED; // Slight buff when cooking in a grill
    public static final FoodTrait BURNT_TO_A_CRISP; // Cooking food in something that's WAY TOO HOT too cook food in you fool!

    private static final Map<String, FoodTrait> TRAITS = new HashMap<>();

    static
    {
        // These must be initialized after TRAITS is, to avoid NPE

        BRINED = new FoodTrait("brined", 1.0f);
        SALTED = new FoodTrait("salted", 0.5f);
        PICKLED = new FoodTrait("pickled", 0.5f);
        PRESERVED = new FoodTrait("preserved", 0.5f);
        VINEGAR = new FoodTrait("vinegar", 0.1f);
        CHARCOAL_GRILLED = new FoodTrait("charcoal_grilled", 1.25f);
        WOOD_GRILLED = new FoodTrait("wood_grilled", 0.8f);
        BURNT_TO_A_CRISP = new FoodTrait("burnt_to_a_crisp", 2.5f); // This one is so high as it is meant to be > the existing gain from cooking meat.
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
