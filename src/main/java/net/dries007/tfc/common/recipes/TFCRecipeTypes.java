/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCRecipeTypes
{
    public static final RecipeType<CollapseRecipe> COLLAPSE = register("collapse");
    public static final RecipeType<LandslideRecipe> LANDSLIDE = register("landslide");
    public static final RecipeType<HeatingRecipe> HEATING = register("heating");
    public static final RecipeType<QuernRecipe> QUERN = register("quern");
    public static final RecipeType<PotRecipe> POT = register("pot");
    public static final RecipeType<ScrapingRecipe> SCRAPING = register("scraping");
    public static final RecipeType<KnappingRecipe> CLAY_KNAPPING = register("clay_knapping");
    public static final RecipeType<KnappingRecipe> FIRE_CLAY_KNAPPING = register("fire_clay_knapping");
    public static final RecipeType<KnappingRecipe> LEATHER_KNAPPING = register("leather_knapping");
    public static final RecipeType<RockKnappingRecipe> ROCK_KNAPPING = register("rock_knapping");

    public static void registerPotRecipeOutputTypes()
    {
        PotRecipe.register(Helpers.identifier("soup"), SoupPotRecipe.OUTPUT_TYPE);
    }

    private static <R extends Recipe<?>> RecipeType<R> register(String name)
    {
        return RecipeType.register(MOD_ID + ":" + name);
    }
}