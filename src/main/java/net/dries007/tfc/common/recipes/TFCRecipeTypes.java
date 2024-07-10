/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryHolder;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class TFCRecipeTypes
{
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MOD_ID);

    public static final Id<CollapseRecipe> COLLAPSE = register("collapse");
    public static final Id<LandslideRecipe> LANDSLIDE = register("landslide");
    public static final Id<ChiselRecipe> CHISEL = register("chisel");
    public static final Id<HeatingRecipe> HEATING = register("heating");
    public static final Id<QuernRecipe> QUERN = register("quern");
    public static final Id<PotRecipe> POT = register("pot");
    public static final Id<ScrapingRecipe> SCRAPING = register("scraping");
    public static final Id<KnappingRecipe> KNAPPING = register("knapping");
    public static final Id<AlloyRecipe> ALLOY = register("alloy");
    public static final Id<CastingRecipe> CASTING = register("casting");
    public static final Id<LoomRecipe> LOOM = register("loom");
    public static final Id<SealedBarrelRecipe> BARREL_SEALED = register("barrel_sealed");
    public static final Id<InstantBarrelRecipe> BARREL_INSTANT = register("barrel_instant");
    public static final Id<InstantFluidBarrelRecipe> BARREL_INSTANT_FLUID = register("barrel_instant_fluid");
    public static final Id<BloomeryRecipe> BLOOMERY = register("bloomery");
    public static final Id<AnvilRecipe> ANVIL = register("anvil");
    public static final Id<WeldingRecipe> WELDING = register("welding");
    public static final Id<BlastFurnaceRecipe> BLAST_FURNACE = register("blast_furnace");
    public static final Id<GlassworkingRecipe> GLASSWORKING = register("glassworking");
    public static final Id<SewingRecipe> SEWING = register("sewing");

    private static <R extends Recipe<?>> Id<R> register(String name)
    {
        return new Id<>(RECIPE_TYPES.register(name, () -> new RecipeType<>() {
            @Override
            public String toString()
            {
                return name;
            }
        }));
    }

    public record Id<T extends Recipe<?>>(DeferredHolder<RecipeType<?>, RecipeType<T>> holder)
        implements RegistryHolder<RecipeType<?>, RecipeType<T>> {}
}