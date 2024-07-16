/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test;

import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import net.dries007.tfc.TestHelper;
import net.dries007.tfc.common.recipes.AdvancedShapedRecipe;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;

import static org.junit.jupiter.api.Assertions.*;

public class AdvancedShapedRecipeTests extends TestHelper
{
    /**
     * Test that the input stack used in {@link ItemStackProvider} is correctly located when using {@link AdvancedShapedRecipe}
     */
    @TestFactory
    @SuppressWarnings("ConstantConditions")
    public Stream<DynamicTest> testInputStackIsCorrectlyLocated()
    {
        return IntStream.range(0, 9)
            .mapToObj(i -> IntStream.range(0, 9)
                .filter(j -> i != j)
                .mapToObj(j -> DynamicTest.dynamicTest("apple = " + i + ", gold = " + j, () -> {
                    final AdvancedShapedRecipe recipe = exampleRecipe();

                    // Assemble the recipe
                    final CraftingContainer inventory = TestHelper.mock(3, 3);
                    inventory.setItem(i, new ItemStack(Items.APPLE));
                    inventory.setItem(j, new ItemStack(Items.GOLD_INGOT));

                    if (recipe.matches(inventory, null))
                    {
                        assertEquals(i, RecipeHelpers.translateMatch(recipe, 1, inventory));
                        assertEquals(new ItemStack(Items.APPLE), recipe.assemble(inventory, null));
                    }
                    else
                    {
                        assertEquals(-1, RecipeHelpers.translateMatch(recipe, 1, inventory));
                    }
                }))
            )
            .flatMap(Function.identity());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testStackedInputsProduceCorrectAmounts()
    {
        final AdvancedShapedRecipe recipe = exampleRecipe();
        final CraftingContainer inventory = TestHelper.mock(3, 3);
        inventory.setItem(0, new ItemStack(Items.APPLE, 16));
        inventory.setItem(1, new ItemStack(Items.GOLD_INGOT, 16));

        assertTrue(recipe.matches(inventory, null));
        assertEquals(recipe.assemble(inventory, null), new ItemStack(Items.APPLE));
    }

    private AdvancedShapedRecipe exampleRecipe()
    {
        final ResourceLocation id = Helpers.identifierMC("recipe");
        final Ingredient apple = Ingredient.of(Items.APPLE);
        final Ingredient gold = Ingredient.of(Items.GOLD_INGOT);
        final NonNullList<Ingredient> items = NonNullList.of(Ingredient.EMPTY, gold, apple);
        final ItemStackProvider result = ItemStackProvider.copyInput();
        return new AdvancedShapedRecipe(id, "", 2, 1, items, result, 1);
    }
}
