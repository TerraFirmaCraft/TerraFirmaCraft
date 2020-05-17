/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This is a trait that can be applied to a food to modify it's decay date.
 * To add new traits, simply create new instances of this class, and assign a unique name
 */
public class FoodTrait
{
    public static final FoodTrait SALTED;
    public static final FoodTrait BRINED; // No decay modifier, required to pickle foods
    public static final FoodTrait PICKLED;
    public static final FoodTrait PRESERVED; // Large / Small vessels
    public static final FoodTrait VINEGAR; // Used for the state of being sealed in vinegar
    public static final FoodTrait CHARCOAL_GRILLED;
    public static final FoodTrait WOOD_GRILLED;

    private static final Map<String, FoodTrait> TRAITS = new HashMap<>();

    static
    {
        // These must be initialized after TRAITS is, to avoid NPE

        BRINED = new FoodTrait("brined", 1.0f);
        SALTED = new FoodTrait("salted", 0.5f);
        PICKLED = new FoodTrait("pickled", 0.5f);
        PRESERVED = new FoodTrait("preserved", 0.5f);
        VINEGAR = new FoodTrait("vinegar", 0.1f);
        CHARCOAL_GRILLED = new FoodTrait("charcoal_grilled", 1.2f);
        WOOD_GRILLED = new FoodTrait("wood_grilled", 0.8f);
    }

    public static Map<String, FoodTrait> getTraits()
    {
        return TRAITS;
    }

    private final String name;
    private final float decayModifier;
    private final boolean hasTooltip;

    public FoodTrait(@Nonnull String name, float decayModifier)
    {
        this(name, decayModifier, true);
    }

    public FoodTrait(@Nonnull String name, float decayModifier, boolean hasTooltip)
    {
        this.name = name;
        this.decayModifier = decayModifier;
        this.hasTooltip = hasTooltip;

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

    @Nonnull
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
    @SideOnly(Side.CLIENT)
    public void addTraitInfo(@Nonnull ItemStack stack, @Nonnull List<String> text)
    {
        if (hasTooltip)
        {
            text.add(I18n.format("tfc.food_traits." + getName()));
        }
    }
}
