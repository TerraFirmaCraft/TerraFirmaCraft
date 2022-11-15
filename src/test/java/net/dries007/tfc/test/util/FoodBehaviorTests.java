/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.util;

import java.util.Collection;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.gametest.GameTestHolder;

import net.dries007.tfc.MyTest;
import net.dries007.tfc.TestAssertions;
import net.dries007.tfc.common.capabilities.VesselLike;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.mock.MockCraftingContainer;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTransaction;
import net.dries007.tfc.util.calendar.Calendars;

import static org.junit.jupiter.api.Assertions.*;

@GameTestHolder
public class FoodBehaviorTests
{
    @GameTestGenerator
    public Collection<TestFunction> generator()
    {
        return TestAssertions.testGenerator();
    }

    @MyTest(unitTest = true)
    public void testCreatingAlmostExpiredFlourIntoDoughDoesNotExpire(GameTestHelper helper)
    {
        final ItemStack grainStack = new ItemStack(TFCItems.FOOD.get(Food.WHEAT_FLOUR).get());
        final ItemStack waterStack = new ItemStack(Items.WATER_BUCKET);
        final IFood grainFood = Helpers.getCapability(grainStack, FoodCapability.CAPABILITY);

        assertNotNull(grainFood);
        assertFalse(grainFood.isRotten());

        try (final CalendarTransaction tr = Calendars.SERVER.transaction())
        {
            // Jump to when the grain is 'almost' expired
            tr.add(grainFood.getRottenDate() - Calendars.SERVER.getTicks() - 10);

            // Should be 10 ticks from rotten
            assertEquals(10, grainFood.getRottenDate() - Calendars.SERVER.getTicks());
            assertFalse(grainFood.isRotten());

            final CraftingContainer container = new MockCraftingContainer(3, 3);
            container.setItem(0, grainStack);
            container.setItem(1, waterStack);

            final CraftingRecipe recipe = helper.getLevel()
                .getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, container, helper.getLevel())
                .orElse(null);

            assertNotNull(recipe);

            final ItemStack doughStack = recipe.assemble(container);
            final IFood doughFood = Helpers.getCapability(doughStack, FoodCapability.CAPABILITY);

            assertFalse(doughStack.isEmpty());
            assertNotNull(doughFood);
            assertFalse(doughFood.isRotten());
        }
    }

    @MyTest(unitTest = true)
    public void testMovingInAndOutOfVesselDoesNotChangeDecayDate(GameTestHelper helper)
    {
        final ItemStack foodStack = new ItemStack(TFCItems.FOOD.get(Food.BANANA).get());
        final ItemStack vesselStack = new ItemStack(TFCItems.VESSEL.get());
        final VesselLike vessel = VesselLike.get(vesselStack);
        final IFood initialFood = Helpers.getCapability(foodStack, FoodCapability.CAPABILITY);

        assertNotNull(vessel);
        assertNotNull(initialFood);

        final long initialExpiryDate = initialFood.getRottenDate();
        final ItemStack excess = vessel.insertItem(0, foodStack, false);

        assertTrue(excess.isEmpty());

        final ItemStack insideFoodStack = vessel.getStackInSlot(0);
        final IFood insideFood = Helpers.getCapability(insideFoodStack, FoodCapability.CAPABILITY);

        assertNotNull(insideFood);

        final long insideExpiryDate = insideFood.getRottenDate();

        assertTrue(insideExpiryDate > initialExpiryDate);

        final ItemStack afterExtractFoodStack = vessel.extractItem(0, 64, false);
        final IFood afterExtractFood = Helpers.getCapability(afterExtractFoodStack, FoodCapability.CAPABILITY);

        assertTrue(vessel.getStackInSlot(0).isEmpty());
        assertNotNull(afterExtractFood);

        final long afterExtractExpiryDate = afterExtractFood.getRottenDate();

        assertEquals(initialExpiryDate, afterExtractExpiryDate);
    }
}
