/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FoodTrait
{
    private static final BiMap<ResourceLocation, FoodTrait> REGISTRY = HashBiMap.create();

    /**
     * Register a food trait.
     * This method is safe to call during parallel mod loading.
     */
    public static synchronized FoodTrait register(ResourceLocation id, FoodTrait trait)
    {
        if (REGISTRY.containsKey(id))
        {
            throw new IllegalArgumentException("Duplicate key: " + id);
        }
        REGISTRY.put(id, trait);
        return trait;
    }

    @Nullable
    public static FoodTrait getTrait(ResourceLocation key)
    {
        return REGISTRY.get(key);
    }

    public static FoodTrait getTraitOrThrow(ResourceLocation key)
    {
        return Objects.requireNonNull(getTrait(key), "No food trait named: " + key);
    }

    public static ResourceLocation getId(FoodTrait trait)
    {
        return REGISTRY.inverse().get(trait);
    }

    private final Supplier<Float> decayModifier;
    @Nullable private final String translationKey;

    public FoodTrait(float decayModifier, @Nullable String translationKey)
    {
        this.decayModifier = () -> decayModifier;
        this.translationKey = translationKey;
    }

    public FoodTrait(Supplier<Float> decayModifier, @Nullable String translationKey)
    {
        this.decayModifier = decayModifier;
        this.translationKey = translationKey;
    }

    public float getDecayModifier()
    {
        return decayModifier.get();
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
            final MutableComponent component = Component.translatable(translationKey);
            if (decayModifier.get() > 1f)
            {
                component.withStyle(ChatFormatting.RED);
            }
            text.add(component);
        }
    }
}
