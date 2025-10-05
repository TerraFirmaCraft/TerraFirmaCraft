/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.food.FoodTrait;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.AdvancedShapedRecipe;
import net.dries007.tfc.common.recipes.AdvancedShapelessRecipe;
import net.dries007.tfc.common.recipes.outputs.AddBaitToRodModifier;
import net.dries007.tfc.common.recipes.outputs.AddGlassModifier;
import net.dries007.tfc.common.recipes.outputs.AddPowderModifier;
import net.dries007.tfc.common.recipes.outputs.AddTraitModifier;
import net.dries007.tfc.common.recipes.outputs.CopyFoodModifier;
import net.dries007.tfc.common.recipes.outputs.CopyForgingBonusModifier;
import net.dries007.tfc.common.recipes.outputs.CopyInputModifier;
import net.dries007.tfc.common.recipes.outputs.CopyOldestFoodModifier;
import net.dries007.tfc.common.recipes.outputs.DamageCraftingRemainderModifier;
import net.dries007.tfc.common.recipes.outputs.ExtraProductModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public interface DataGenerationHelpers
{
    /**
     * A recipe builder capable of building shaped, shapeless recipes, optionally with both output and remainder features
     * of advanced shaped / shapeless recipes. It has some preliminary validations to ensure legal recipes are built
     */
    class Builder
    {
        final BiConsumer<String, Recipe<?>> onFinish;
        @Nullable String name = null;

        final List<ItemStackModifier> remainder = new ArrayList<>(); // For advanced recipes, remainder modifiers
        final List<ItemStackModifier> outputs = new ArrayList<>(); // For advanced recipes, output modifiers
        final NonNullList<Ingredient> ingredients = NonNullList.create(); // Shapeless recipes only
        final List<String> pattern = new ArrayList<>(); // Shaped recipes only
        final ImmutableMap.Builder<Character, Ingredient> keys = ImmutableMap.builder();
        int inputRow = 0, inputCol = 0;
        @Nullable Ingredient primaryInput = null;
        boolean needsAdvInput = false, hasAdvInputShaped = false, hasAdvInputShapeless = false;

        public Builder(BiConsumer<String, Recipe<?>> onFinish)
        {
            this.onFinish = onFinish;
        }

        public void useTool(TagKey<Item> tool, ItemLike input, ItemLike output)
        {
            input(input).inputIsPrimary(tool).damageInputs().shapeless(output);
        }

        public void useTool(TagKey<Item> tool, Ingredient input, ItemLike output, int count)
        {
            input(input).inputIsPrimary(tool).damageInputs().shapeless(output, count);
        }

        public void bricksWithMortar(ItemLike brick, ItemLike bricks, int count)
        {
            input('Y', TFCItems.MORTAR).input('X', brick).pattern("XYX", "YXY", "XYX").shaped(bricks, count);
        }

        public void to3x3(Ingredient input, ItemLike storage)
        {
            input('X', input).pattern("XXX", "XXX", "XXX").shaped(storage);
        }

        public void from3x3(Ingredient input, ItemLike item)
        {
            input(input).shapeless(item, 9);
        }

        public void to2x2(ItemLike input, ItemLike output, int count)
        {
            input('X', input).pattern("XX", "XX").shaped(output, count);
        }

        public Builder damageInputs()
        {
            remainder.add(DamageCraftingRemainderModifier.INSTANCE);
            return this;
        }

        public Builder copyOldestFood()
        {
            outputs.add(CopyOldestFoodModifier.INSTANCE);
            return this;
        }

        public Builder copyFood()
        {
            outputs.add(CopyFoodModifier.INSTANCE);
            return this;
        }

        public Builder copyForging()
        {
            needsAdvInput = true;
            return addOutputModifier(CopyForgingBonusModifier.INSTANCE);
        }

        public Builder copyInput()
        {
            needsAdvInput = true;
            return addOutputModifier(CopyInputModifier.INSTANCE);
        }

        public Builder addGlass()
        {
            needsAdvInput = true;
            return addOutputModifier(AddGlassModifier.INSTANCE);
        }

        public Builder addPowder() {return addOutputModifier(AddPowderModifier.INSTANCE);}

        public Builder addBait() {return addOutputModifier(AddBaitToRodModifier.INSTANCE);}

        public Builder extraProduct(ItemLike item) {return extraProduct(item, 1);}

        public Builder extraProduct(ItemLike item, int count)
        {
            remainder.add(new ExtraProductModifier(new ItemStack(item, count)));
            return this;
        }

        public Builder addTrait(Holder<FoodTrait> trait) {return addOutputModifier(AddTraitModifier.of(trait));}

        public Builder addOutputModifier(ItemStackModifier modifier)
        {
            outputs.add(modifier);
            return this;
        }

        public Builder input(ItemLike item) {return input(item, 1);}

        public Builder input(ItemLike item, int count) {return input(Ingredient.of(item), count);}

        public Builder input(TagKey<Item> item) {return input(item, 1);}

        public Builder input(TagKey<Item> item, int count) {return input(Ingredient.of(item), count);}

        public Builder input(Ingredient item) {return input(item, 1);}

        public Builder input(Ingredient item, int count)
        {
            for (int n = 0; n < count; n++) ingredients.add(item);
            return this;
        }

        public Builder inputIsPrimary(ItemLike item) {return inputIsPrimary(Ingredient.of(item));}

        public Builder inputIsPrimary(TagKey<Item> item) {return inputIsPrimary(Ingredient.of(item));}

        public Builder inputIsPrimary(Ingredient item)
        {
            primaryInput = item;
            hasAdvInputShapeless = true;
            return input(item);
        }

        public Builder input(char key, TagKey<Item> input) {return input(key, Ingredient.of(input));}

        public Builder input(char key, ItemLike input) {return input(key, Ingredient.of(input));}

        public Builder input(char key, Ingredient input)
        {
            keys.put(key, input);
            return this;
        }

        public Builder source(int row, int col)
        {
            inputRow = row;
            inputCol = col;
            hasAdvInputShaped = true;
            return this;
        }

        public Builder pattern(String... pattern)
        {
            this.pattern.addAll(List.of(pattern));
            return this;
        }

        public void shapeless(String name)
        {
            this.name = name;
            shapeless(ItemStack.EMPTY);
        }

        public void shapeless(ItemLike output) {shapeless(output, 1);}

        public void shapeless(ItemLike output, int count) {shapeless(new ItemStack(output, count));}

        public void shapeless(ItemStack output)
        {
            assert pattern.isEmpty() && keys.build().isEmpty() : "Mixing shaped and shapeless recipes";
            assert hasAdvInputShapeless || !needsAdvInput : "Missing a .inputIsPrimary(Ingredient) for a recipe which depends on input";
            assert !outputs.isEmpty() || !output.isEmpty() : "Either non-empty output, or output modifiers must be present";

            onFinish.accept(name, isAdvanced()
                ? new AdvancedShapelessRecipe(ingredients, ItemStackProvider.of(output, outputs), remainder(), Optional.ofNullable(primaryInput))
                : new ShapelessRecipe("", CraftingBookCategory.MISC, output, ingredients));
        }

        public void shaped(String name)
        {
            this.name = name;
            shaped(ItemStack.EMPTY);
        }

        public void shaped(ItemLike output) {shaped(output, 1);}

        public void shaped(ItemLike output, int count) {shaped(new ItemStack(output, count));}

        public void shaped(ItemStack output)
        {
            assert ingredients.isEmpty() : "Mixing shaped and shapeless recipes";
            assert hasAdvInputShaped || !needsAdvInput : "Missing a .source(int, int) for a recipe which depends on input";
            assert !outputs.isEmpty() || !output.isEmpty() : "Either non-empty output, or output modifiers must be present";

            final ShapedRecipePattern pattern = ShapedRecipePattern.of(keys.build(), this.pattern);
            onFinish.accept(name, isAdvanced()
                ? new AdvancedShapedRecipe(pattern, true, ItemStackProvider.of(output, outputs), remainder(), inputRow, inputCol)
                : new ShapedRecipe("", CraftingBookCategory.MISC, pattern, output));
        }

        private Optional<ItemStackProvider> remainder()
        {
            return remainder.isEmpty() ? Optional.empty() : Optional.of(ItemStackProvider.of(ItemStack.EMPTY, remainder));
        }

        private boolean isAdvanced()
        {
            return !remainder.isEmpty() || !outputs.isEmpty();
        }
    }
}
