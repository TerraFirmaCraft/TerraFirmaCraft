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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

public record MealModifier(FoodData baseFood, List<MealPortion> portions) implements ItemStackModifier
{
    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        final @Nullable IFood inputFood = FoodCapability.get(stack);
        if (!(inputFood instanceof FoodHandler.Dynamic handler))
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


        handler.setFood(FoodData.create(baseFood.hunger(), water, saturation, nutrition, baseFood.decayModifier()));
        handler.setIngredients(itemIngredients);
        handler.setCreationDate(FoodCapability.getRoundedCreationDate());
        return stack;
    }

    @Override
    public Serializer serializer()
    {
        return Serializer.INSTANCE;
    }

    public enum Serializer implements ItemStackModifier.Serializer<MealModifier>
    {
        INSTANCE;

        @Override
        public MealModifier fromJson(JsonObject json)
        {
            final var food = FoodData.read(json.getAsJsonObject("food"));
            final List<MealPortion> portions = new ArrayList<>();
            final var array = JsonHelpers.getAsJsonArray(json, "portions", new JsonArray());
            if (!array.isEmpty())
            {
                for (JsonElement element : array)
                {
                    portions.add(MealPortion.fromJson(element.getAsJsonObject()));
                }
            }
            return new MealModifier(food, portions);
        }

        @Override
        public MealModifier fromNetwork(FriendlyByteBuf buffer)
        {
            final var food = FoodData.decode(buffer);
            final List<MealPortion> portions = Helpers.decodeAll(buffer, new ArrayList<>(), MealPortion::fromNetwork);
            return new MealModifier(food, portions);
        }

        @Override
        public void toNetwork(MealModifier modifier, FriendlyByteBuf buffer)
        {
            modifier.baseFood.encode(buffer);
            Helpers.encodeAll(buffer, modifier.portions, (por, buf) -> por.toNetwork(buffer));
        }
    }

    public record MealPortion(@Nullable Ingredient ingredient, float nutrientModifier, float waterModifier, float saturationModifier)
    {
        public boolean test(ItemStack stack)
        {
            return ingredient == null || ingredient.test(stack);
        }

        private static MealPortion fromJson(JsonObject json)
        {
            return new MealPortion(
                json.has("ingredient") ? Ingredient.fromJson(json.get("ingredient")) : null,
                JsonHelpers.getAsFloat(json, "nutrient_modifier", 1f),
                JsonHelpers.getAsFloat(json, "water_modifier", 1f),
                JsonHelpers.getAsFloat(json, "saturation_modifier", 1f)
            );
        }

        private static MealPortion fromNetwork(FriendlyByteBuf buffer)
        {
            return new MealPortion(
                Helpers.decodeNullable(buffer, Ingredient::fromNetwork),
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readFloat()
            );
        }

        private void toNetwork(FriendlyByteBuf buffer)
        {
            Helpers.encodeNullable(ingredient, buffer, (i, buf) -> i.toNetwork(buffer));
            buffer.writeFloat(nutrientModifier);
            buffer.writeFloat(waterModifier);
            buffer.writeFloat(saturationModifier);
        }
    }
}
