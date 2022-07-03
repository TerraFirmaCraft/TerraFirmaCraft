/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCRecipeTypes
{
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, MOD_ID);

    public static final RegistryObject<RecipeType<CollapseRecipe>> COLLAPSE = register("collapse");
    public static final RegistryObject<RecipeType<LandslideRecipe>> LANDSLIDE = register("landslide");
    public static final RegistryObject<RecipeType<ChiselRecipe>> CHISEL = register("chisel");
    public static final RegistryObject<RecipeType<HeatingRecipe>> HEATING = register("heating");
    public static final RegistryObject<RecipeType<QuernRecipe>> QUERN = register("quern");
    public static final RegistryObject<RecipeType<PotRecipe>> POT = register("pot");
    public static final RegistryObject<RecipeType<ScrapingRecipe>> SCRAPING = register("scraping");
    public static final RegistryObject<RecipeType<KnappingRecipe>> CLAY_KNAPPING = register("clay_knapping");
    public static final RegistryObject<RecipeType<KnappingRecipe>> FIRE_CLAY_KNAPPING = register("fire_clay_knapping");
    public static final RegistryObject<RecipeType<KnappingRecipe>> LEATHER_KNAPPING = register("leather_knapping");
    public static final RegistryObject<RecipeType<RockKnappingRecipe>> ROCK_KNAPPING = register("rock_knapping");
    public static final RegistryObject<RecipeType<AlloyRecipe>> ALLOY = register("alloy");
    public static final RegistryObject<RecipeType<CastingRecipe>> CASTING = register("casting");
    public static final RegistryObject<RecipeType<LoomRecipe>> LOOM = register("loom");
    public static final RegistryObject<RecipeType<SealedBarrelRecipe>> BARREL_SEALED = register("barrel_sealed");
    public static final RegistryObject<RecipeType<InstantBarrelRecipe>> BARREL_INSTANT = register("barrel_instant");
    public static final RegistryObject<RecipeType<InstantFluidBarrelRecipe>> BARREL_INSTANT_FLUID = register("barrel_instant_fluid");
    public static final RegistryObject<RecipeType<BloomeryRecipe>> BLOOMERY = register("bloomery");
    public static final RegistryObject<RecipeType<AnvilRecipe>> ANVIL = register("anvil");
    public static final RegistryObject<RecipeType<WeldingRecipe>> WELDING = register("welding");
    public static final RegistryObject<RecipeType<BlastFurnaceRecipe>> BLAST_FURNACE = register("blast_furnace");

    public static void registerPotRecipeOutputTypes()
    {
        PotRecipe.register(Helpers.identifier("soup"), SoupPotRecipe.OUTPUT_TYPE);
    }

    private static <R extends Recipe<?>> RegistryObject<RecipeType<R>> register(String name)
    {
        return RECIPE_TYPES.register(name, () -> new RecipeType<>() {
            @Override
            public String toString()
            {
                return name;
            }
        });
    }
}