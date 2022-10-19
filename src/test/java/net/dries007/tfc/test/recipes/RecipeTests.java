/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.recipes;

import java.util.Collection;
import io.netty.buffer.Unpooled;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.gametest.GameTestHolder;
import org.junit.jupiter.api.Assertions;

import net.dries007.tfc.MyTest;
import net.dries007.tfc.TestAssertions;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.mock.MockCraftingContainer;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTransaction;
import net.dries007.tfc.util.calendar.Calendars;

import static net.dries007.tfc.TestAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

@GameTestHolder
public class RecipeTests
{
    @GameTestGenerator
    public Collection<TestFunction> generator()
    {
        return TestAssertions.testGenerator();
    }

    @MyTest(unitTest = true)
    public void testNoRecipeMatchesEmptyGrid(GameTestHelper helper)
    {
        for (CraftingRecipe recipe : helper.getLevel().getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING))
        {
            final MockCraftingContainer container = new MockCraftingContainer(3, 3);
            assertFalse(recipe.matches(container, helper.getLevel()), "Recipe: " + recipe.getId() + " of type " + recipe.getType() + " and serializer " + recipe.getSerializer().getRegistryName() + " matches an empty grid");
        }
    }

    @MyTest(unitTest = true)
    public void testNoRecipeProducesRottenOutput(GameTestHelper helper)
    {
        for (Recipe<?> recipe : helper.getLevel().getRecipeManager().getRecipes())
        {
            // If the output is non-empty and non-rotten, then we should assert that it doesn't become rotten if we jump ahead
            final ItemStack output = getOutputOfRecipe(recipe);
            final IFood food = Helpers.getCapability(output, FoodCapability.CAPABILITY);
            if (!output.isEmpty() && food != null && !food.isRotten())
            {
                try (CalendarTransaction tr = Calendars.SERVER.transaction())
                {
                    tr.add(1_000_000_000);

                    final ItemStack oldOutput = getOutputOfRecipe(recipe);
                    final IFood oldFood = Helpers.getCapability(oldOutput, FoodCapability.CAPABILITY);

                    assertNotNull(oldFood);
                    assertFalse(oldFood.isRotten(), "Recipe: " + recipe.getId() + " of type " + recipe.getType() + " and serializer " + recipe.getSerializer().getRegistryName() + " produced rotten output");
                }
            }
        }
    }

    @MyTest(unitTest = true)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void testAllRecipesEncodeAndDecode(GameTestHelper helper)
    {
        for (Recipe<?> before : helper.getLevel().getRecipeManager().getRecipes())
        {
            final FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            ((RecipeSerializer) before.getSerializer()).toNetwork(buffer, before);
            final Recipe<?> after = before.getSerializer().fromNetwork(before.getId(), buffer);

            assertEquals(0, buffer.readableBytes(), "Buffer has " + buffer.readableBytes() + " remaining bytes after reading recipe: " + before.getId());
            assertEquals(before, after);
        }
    }

    @SuppressWarnings("unchecked")
    private ItemStack getOutputOfRecipe(Recipe<?> recipe)
    {
        try
        {
            return ((Recipe<MockCraftingContainer>) recipe).assemble(new MockCraftingContainer(1, 1));
        }
        catch (Throwable t) { /* Ignore */ }
        return recipe.getResultItem();
    }
}
