/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

public record MealModifier(FoodData baseFood, List<MealPortion> portions) implements ItemStackModifier
{
    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        final CraftingContainer inv = RecipeHelpers.getCraftingContainer();
        if (inv != null)
        {
            stack.getCapability(FoodCapability.CAPABILITY).ifPresent(food -> {
                if (food instanceof FoodHandler.Dynamic dynamic)
                {
                    initFoodStats(inv, dynamic);
                }
            });
            return stack;
        }
        return stack;
    }

    private void initFoodStats(CraftingContainer inv, FoodHandler.Dynamic handler)
    {
        final List<ItemStack> itemIngredients = new ArrayList<>();

        for (int i = 0; i < inv.getContainerSize(); i++)
        {
            final ItemStack item = inv.getItem(i);
            item.getCapability(FoodCapability.CAPABILITY).ifPresent(cap -> {
                itemIngredients.add(Helpers.copyWithSize(item, 1));
            });
        }
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
            final IFood food = Helpers.getCapability(item, FoodCapability.CAPABILITY);
            if (food != null)
            {
                final var data = food.getData();
                for (Nutrient nutrient : Nutrient.VALUES)
                {
                    nutrition[nutrient.ordinal()] += data.nutrient(nutrient) * portion.nutrientModifier;
                }
                water += data.water() * portion.waterModifier;
                saturation += data.saturation() * portion.saturationModifier;
            }
        }


        handler.setFood(FoodData.create(baseFood.hunger(), water, saturation, nutrition, baseFood.decayModifier()));
        handler.setIngredients(itemIngredients);
        handler.setCreationDate(FoodCapability.getRoundedCreationDate());
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
