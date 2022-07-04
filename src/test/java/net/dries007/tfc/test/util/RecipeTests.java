/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.util;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.TestHelper;
import net.dries007.tfc.common.recipes.*;
import net.dries007.tfc.common.recipes.ingredients.FluidIngredient;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
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

    @Test
    public void testBarrelInstantRecipe()
    {
        final InstantBarrelRecipe before = new InstantBarrelRecipe(Helpers.identifier("barrel"), new BarrelRecipe.Builder(new ItemStackIngredient(Ingredient.of(Items.ACACIA_BOAT), 4), new FluidStackIngredient(FluidIngredient.of(Fluids.LAVA, Fluids.FLOWING_WATER), 150), ItemStackProvider.of(new ItemStack(Blocks.OBSIDIAN, 4)), FluidStack.EMPTY, SoundEvents.BREWING_STAND_BREW));
        final InstantBarrelRecipe after = encodeAndDecode(before, new InstantBarrelRecipe.Serializer());
        assertRecipeEquals(before, after);
    }

    @Test
    public void testBarrelInstantFluidRecipe()
    {
        final InstantFluidBarrelRecipe before = new InstantFluidBarrelRecipe(Helpers.identifier("barrel"), new BarrelRecipe.Builder(ItemStackIngredient.EMPTY, new FluidStackIngredient(FluidIngredient.of(Fluids.WATER), 100), ItemStackProvider.empty(), new FluidStack(Fluids.LAVA, 50), SoundEvents.AMBIENT_BASALT_DELTAS_ADDITIONS), new FluidStackIngredient(FluidIngredient.of(Fluids.FLOWING_LAVA), 10));
        final InstantFluidBarrelRecipe after = encodeAndDecode(before, new InstantFluidBarrelRecipe.Serializer());
        assertRecipeEquals(before, after);
    }

    @Test
    public void testBarrelSealedRecipe()
    {
        final SealedBarrelRecipe before = new SealedBarrelRecipe(Helpers.identifier("barrel"), new BarrelRecipe.Builder(new ItemStackIngredient(Ingredient.of(Items.GOLD_INGOT), 10), new FluidStackIngredient(FluidIngredient.of(Fluids.WATER), 100), ItemStackProvider.empty(), new FluidStack(Fluids.LAVA, 150), SoundEvents.AMBIENT_CAVE), 15000, null, ItemStackProvider.copyInput());
        final SealedBarrelRecipe after = encodeAndDecode(before, new SealedBarrelRecipe.Serializer());
        assertRecipeEquals(before, after);
    }
}
