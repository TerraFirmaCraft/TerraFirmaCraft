/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.function.Supplier;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.util.Lazy;

import net.dries007.tfc.util.Helpers;

public class TFCRecipeTypes
{
    public static final Supplier<RecipeType<CollapseRecipe>> COLLAPSE = register("collapse");
    public static final Supplier<RecipeType<LandslideRecipe>> LANDSLIDE = register("landslide");
    public static final Supplier<RecipeType<HeatingRecipe>> HEATING = register("heating");
    public static final Supplier<RecipeType<QuernRecipe>> QUERN = register("quern");
    public static final Supplier<RecipeType<PotRecipe>> POT = register("pot");
    public static final Supplier<RecipeType<ScrapingRecipe>> SCRAPING = register("scraping");
    public static final Supplier<RecipeType<KnappingRecipe>> CLAY_KNAPPING = register("clay_knapping");
    public static final Supplier<RecipeType<KnappingRecipe>> FIRE_CLAY_KNAPPING = register("fire_clay_knapping");
    public static final Supplier<RecipeType<KnappingRecipe>> LEATHER_KNAPPING = register("leather_knapping");
    public static final Supplier<RecipeType<RockKnappingRecipe>> ROCK_KNAPPING = register("rock_knapping");
    public static final Supplier<RecipeType<AlloyRecipe>> ALLOY = register("alloy");
    public static final Supplier<RecipeType<CastingRecipe>> CASTING = register("casting");
    public static final Supplier<RecipeType<BloomeryRecipe>> BLOOMERY = register("bloomery");
    public static final Supplier<RecipeType<LoomRecipe>> LOOM = register("loom");

    public static void registerRecipeTypes()
    {
        COLLAPSE.get();
        LANDSLIDE.get();
        HEATING.get();
        QUERN.get();
        POT.get();
        SCRAPING.get();
        CLAY_KNAPPING.get();
        FIRE_CLAY_KNAPPING.get();
        LEATHER_KNAPPING.get();
        ROCK_KNAPPING.get();
        ALLOY.get();
        CASTING.get();
        BLOOMERY.get();
        LOOM.get();
    }

    public static void registerPotRecipeOutputTypes()
    {
        PotRecipe.register(Helpers.identifier("soup"), SoupPotRecipe.OUTPUT_TYPE);
    }

    private static <R extends Recipe<?>> Supplier<RecipeType<R>> register(String name)
    {
        return Lazy.of(() -> RecipeType.register(Helpers.identifier(name).toString()));
    }
}