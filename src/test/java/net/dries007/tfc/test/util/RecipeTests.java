/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.util;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.TestHelper;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;
import org.junit.jupiter.api.Test;

public class RecipeTests extends TestHelper
{
    @Test
    public void testHeatingRecipe()
    {
        final HeatingRecipe before = new HeatingRecipe(Helpers.identifier("heating"), Ingredient.of(Items.CLAY), ItemStackProvider.copyInput(), new FluidStack(Fluids.WATER, 100), 1000);
        final HeatingRecipe after = encodeAndDecode(before, new HeatingRecipe.Serializer());
        assertRecipeEquals(before, after);
    }
}
