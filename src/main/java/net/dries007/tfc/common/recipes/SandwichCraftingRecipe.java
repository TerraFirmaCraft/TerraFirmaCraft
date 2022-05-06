/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.IShapedRecipe;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.food.*;
import net.dries007.tfc.util.Helpers;

public class SandwichCraftingRecipe extends DamageInputsCraftingRecipe<IShapedRecipe<CraftingContainer>>
{
    protected SandwichCraftingRecipe(ResourceLocation id, IShapedRecipe<CraftingContainer> recipe)
    {
        super(id, recipe);
    }

    @Override
    public ItemStack assemble(CraftingContainer inv)
    {
        ItemStack sandwich = super.assemble(inv);
        sandwich.getCapability(FoodCapability.CAPABILITY)
            .filter(food -> food instanceof FoodHandler.Dynamic)
            .ifPresent(food -> {
                FoodHandler.Dynamic sandwichHandler = (FoodHandler.Dynamic) food;
                food.setCreationDate(FoodCapability.getRoundedCreationDate());
                initFoodStats(inv, sandwichHandler);
            });
        return sandwich;
    }

    private void initFoodStats(CraftingContainer inv, FoodHandler.Dynamic handler)
    {
        List<FoodRecord> ingredients = new ArrayList<>(3);
        ItemStack breadItem1 = ItemStack.EMPTY;
        ItemStack breadItem2 = ItemStack.EMPTY;
        boolean checkBread = true;
        for (int index = 0; index < inv.getContainerSize(); index++)
        {
            ItemStack item = inv.getItem(index);
            if (checkBread)
            {
                if (Helpers.isItem(item, TFCTags.Items.SANDWICH_BREAD))
                {
                    if (breadItem1.isEmpty())
                    {
                        breadItem1 = item;
                        continue;
                    }
                    else if (breadItem2.isEmpty())
                    {
                        breadItem2 = item;
                        continue;
                    }
                    else
                    {
                       checkBread = false; // we found two bread
                    }
                }
            }
            item.getCapability(FoodCapability.CAPABILITY).map(IFood::getData).ifPresent(ingredients::add);
        }
        final FoodRecord bread1 = breadItem1.getCapability(FoodCapability.CAPABILITY).map(IFood::getData).orElse(FoodRecord.EMPTY);
        final FoodRecord bread2 = breadItem2.getCapability(FoodCapability.CAPABILITY).map(IFood::getData).orElse(FoodRecord.EMPTY);

        // Nutrition and saturation of sandwich is (average of breads) + 0.8f (sum of ingredients), +1 bonus saturation
        float[] nutrition = new float[Nutrient.TOTAL];
        float saturation = 1 + 0.5f * (bread1.getSaturation() + bread2.getSaturation());
        float water = 0.5f * (bread1.getWater() + bread2.getWater());
        for (int i = 0; i < nutrition.length; i++)
        {
            nutrition[i] = 0.5f * (bread1.getNutrients()[i] + bread2.getNutrients()[i]);
        }
        for (FoodRecord ingredient : ingredients)
        {
            for (int i = 0; i < nutrition.length; i++)
            {
                nutrition[i] += 0.8f * ingredient.getNutrients()[i];
            }
            saturation += 0.8f * ingredient.getSaturation();
            water += 0.8f * ingredient.getWater();
        }
        handler.setFood(new FoodRecord(4, water, saturation, nutrition, handler.getDecayDateModifier()));
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.SANDWICH.get();
    }
}
