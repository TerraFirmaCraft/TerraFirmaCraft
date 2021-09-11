/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

public class FoodTrait
{
    private static final Map<String, FoodTrait> TRAITS = new ConcurrentHashMap<>();

    /**
     * Register a food trait.
     * This method is safe to call during parallel mod loading.
     */
    public static FoodTrait register(String name, float decayModifier)
    {
        return TRAITS.computeIfAbsent(name, key -> new FoodTrait(name, decayModifier));
    }

    @Nullable
    public static FoodTrait getTrait(String key)
    {
        return TRAITS.get(key);
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
    public void addTooltipInfo(ItemStack stack, List<Component> text)
    {
        if (translationKey != null)
        {
            text.add(new TranslatableComponent(translationKey));
        }
    }
}
