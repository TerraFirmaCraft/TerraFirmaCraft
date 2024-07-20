/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.component.IngredientsComponent;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.recipes.RecipeHelpers;

public record MealModifier(FoodData baseFood, List<MealPortion> portions) implements ItemStackModifier
{
    public static final MapCodec<MealModifier> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        FoodData.CODEC.fieldOf("food").forGetter(c -> c.baseFood),
        RecordCodecBuilder.<MealPortion>create(j -> j.group(
            Ingredient.CODEC.optionalFieldOf("ingredient").forGetter(c -> c.ingredient),
            Codec.FLOAT.optionalFieldOf("nutrient_modifier", 1f).forGetter(c -> c.nutrientModifier),
            Codec.FLOAT.optionalFieldOf("water_modifier", 1f).forGetter(c -> c.waterModifier),
            Codec.FLOAT.optionalFieldOf("saturation_modifier", 1f).forGetter(c -> c.saturationModifier)
        ).apply(j, MealPortion::new)).listOf().fieldOf("portions").forGetter(c -> c.portions)
    ).apply(i, MealModifier::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MealModifier> STREAM_CODEC = StreamCodec.composite(
        FoodData.STREAM_CODEC, c -> c.baseFood,
        StreamCodec.composite(
            ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC), c -> c.ingredient,
            ByteBufCodecs.FLOAT, c -> c.nutrientModifier,
            ByteBufCodecs.FLOAT, c -> c.waterModifier,
            ByteBufCodecs.FLOAT, c -> c.saturationModifier,
            MealPortion::new
        ).apply(ByteBufCodecs.list()), c -> c.portions,
        MealModifier::new
    );

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input, Context context)
    {
        final @Nullable IFood inputFood = FoodCapability.get(stack);
        if (inputFood == null)
        {
            return stack;
        }

        final List<ItemStack> itemIngredients = new ArrayList<>();
        for (final ItemStack item : RecipeHelpers.getCraftingInput())
        {
            if (FoodCapability.has(item))
            {
                boolean alreadyAdded = false;
                for (ItemStack existing : itemIngredients)
                {
                    if (existing.getItem() == item.getItem())
                    {
                        existing.grow(1);
                        alreadyAdded = true;
                        break;
                    }
                }
                if (!alreadyAdded)
                {
                    final ItemStack tooltipItem = item.copyWithCount(1);

                    // Clear any transient data that doesn't display, so we don't create weird stackability issues
                    FoodCapability.setNeverExpires(tooltipItem);
                    HeatCapability.setTemperature(tooltipItem, 0);

                    itemIngredients.add(tooltipItem);
                }
            }
        }

        if (itemIngredients.isEmpty())
        {
            return stack;
        }

        // Sort, so tooltips appear in consistent order, and also to prevent stackability issues
        itemIngredients.sort(Comparator.comparing(ItemStack::getCount)
            .thenComparing(item -> BuiltInRegistries.ITEM.getKey(item.getItem())));

        float[] nutrition = baseFood.nutrients();
        float saturation = baseFood.saturation();
        float water = baseFood.water();

        final Map<ItemStack, MealPortion> map = new HashMap<>();
        // stuff we can match to portions
        for (ItemStack ingredient : itemIngredients)
        {
            MealPortion selected = null;
            for (MealPortion portion : portions)
            {
                if (portion.test(ingredient))
                {
                    selected = portion;
                    break;
                }
            }
            if (selected != null)
                map.put(ingredient, selected);
        }

        for (Map.Entry<ItemStack, MealPortion> entry : map.entrySet())
        {
            final ItemStack item = entry.getKey();
            final MealPortion portion = entry.getValue();
            final @Nullable IFood food = FoodCapability.get(item);
            if (food != null)
            {
                final var data = food.getData();
                for (Nutrient nutrient : Nutrient.VALUES)
                {
                    nutrition[nutrient.ordinal()] += data.nutrient(nutrient) * portion.nutrientModifier * item.getCount();
                }
                water += data.water() * portion.waterModifier * item.getCount();
                saturation += data.saturation() * portion.saturationModifier * item.getCount();
            }
        }

        stack.set(TFCComponents.INGREDIENTS, IngredientsComponent.of(itemIngredients));
        FoodCapability.setFoodForDynamicItemOnCreate(
            stack,
            FoodData.of(baseFood.hunger(), water, saturation, nutrition, baseFood.decayModifier()));

        return stack;
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return ItemStackModifiers.MEAL.get();
    }

    record MealPortion(
        Optional<Ingredient> ingredient,
        float nutrientModifier,
        float waterModifier,
        float saturationModifier
    ) {
        boolean test(ItemStack stack)
        {
            return ingredient.isEmpty() || ingredient.get().test(stack);
        }
    }
}
