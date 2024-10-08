/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.recipes;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.component.food.FoodTraits;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.JamPotRecipe;
import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.common.recipes.SimplePotRecipe;
import net.dries007.tfc.common.recipes.SoupPotRecipe;
import net.dries007.tfc.common.recipes.ingredients.AndIngredient;
import net.dries007.tfc.common.recipes.ingredients.LacksTraitIngredient;
import net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient;
import net.dries007.tfc.common.recipes.outputs.AddTraitModifier;
import net.dries007.tfc.common.recipes.outputs.CopyInputModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;

public interface PotRecipes extends Recipes
{
    default void potRecipes()
    {
        for (DyeColor color : DyeColor.values())
            pot(
                dyeOf(color), 1,
                fluidOf(color),
                hours(2), 600f);

        pot(
            TFCItems.POWDERS.get(Powder.WOOD_ASH), 5,
            fluidOf(SimpleFluid.LYE),
            hours(2), 600f);
        pot(
            TFCItems.OLIVE_PASTE, 5,
            fluidOf(SimpleFluid.OLIVE_OIL_WATER),
            hours(2), 300f);
        pot(
            TFCItems.BLUBBER, 5,
            fluidOf(SimpleFluid.TALLOW),
            hours(2), 600f);

        potX5(AndIngredient.of(Ingredient.of(Items.EGG), NotRottenIngredient.INSTANCE), TFCItems.FOOD.get(Food.BOILED_EGG), hours(1));
        potX5(AndIngredient.of(Ingredient.of(TFCItems.FOOD.get(Food.RICE_GRAIN)), NotRottenIngredient.INSTANCE), TFCItems.FOOD.get(Food.COOKED_RICE), hours(1));

        soup(3);
        soup(4);
        soup(5);
        for (Food food : Food.values())
        {
            if (food.isFruit())
            {
                jam(food);
            }
        }
    }

    private void pot(ItemLike item, int count, Fluid outputFluid, int duration, float temperature)
    {
        add(nameOf(outputFluid), new SimplePotRecipe(new PotRecipe(
            Collections.nCopies(count, Ingredient.of(item)),
            SizedFluidIngredient.of(Fluids.WATER, 1000),
            duration, temperature),
            new FluidStack(outputFluid, 1000), List.of()));
    }

    private void potX5(Ingredient item, ItemLike output, int duration)
    {
        for (int n = 1; n <= 5; n++)
            add(nameOf(output) + "_" + n, new SimplePotRecipe(new PotRecipe(
                Collections.nCopies(n, item),
                SizedFluidIngredient.of(Fluids.WATER, 100),
                duration, 300f),
                FluidStack.EMPTY,
                Collections.nCopies(n, ItemStackProvider.of(output))));
    }

    private void jam(Food fruit)
    {
        final String name = fruit.name().toLowerCase(Locale.ROOT);
        for (int n = 2; n <= 4; n++)
            add("jam_" + name + "_" + n, new JamPotRecipe(new PotRecipe(
                Helpers.immutableAdd(Collections.nCopies(n,
                    AndIngredient.of(Ingredient.of(TFCItems.FOOD.get(fruit)), NotRottenIngredient.INSTANCE)),
                    Ingredient.of(TFCTags.Items.SWEETENERS)),
                SizedFluidIngredient.of(Fluids.WATER, 100),
                500, 300f
            ), new ItemStack(TFCItems.UNSEALED_FRUIT_PRESERVES.get(fruit), n), new ItemStack(TFCItems.FRUIT_PRESERVES.get(fruit), n), Helpers.identifier("block/jar/" + name)));
        for (int n = 1; n <= 5; n++)
        {
            final Ingredient ingredient = AndIngredient.of(Ingredient.of(new ItemStack(TFCItems.FRUIT_PRESERVES.get(fruit))), LacksTraitIngredient.of(FoodTraits.CANNED), NotRottenIngredient.INSTANCE);
            add("jam_" + name + "_canning_" + n, new SimplePotRecipe(new PotRecipe(
                Collections.nCopies(n, ingredient),
                SizedFluidIngredient.of(Fluids.WATER, 100),
                300, 300f),
                FluidStack.EMPTY,
                Collections.nCopies(n, ItemStackProvider.of(CopyInputModifier.INSTANCE, AddTraitModifier.of(FoodTraits.CANNED))
            )));
        }
    }

    private void soup(int count)
    {
        add("soup_" + count, new SoupPotRecipe(new PotRecipe(
            Collections.nCopies(count, AndIngredient.of(Ingredient.of(TFCTags.Items.USABLE_IN_SOUP), NotRottenIngredient.INSTANCE)),
            SizedFluidIngredient.of(Fluids.WATER, 100),
            hours(1), 300f)));
    }
}
