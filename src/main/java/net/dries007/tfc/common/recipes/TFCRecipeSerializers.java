/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.function.Supplier;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
public class TFCRecipeSerializers
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);

    // Block Recipes

    public static final RegistryObject<SimpleBlockRecipe.Serializer<CollapseRecipe>> COLLAPSE = register("collapse", () -> new SimpleBlockRecipe.Serializer<>(CollapseRecipe::new));
    public static final RegistryObject<SimpleBlockRecipe.Serializer<LandslideRecipe>> LANDSLIDE = register("landslide", () -> new SimpleBlockRecipe.Serializer<>(LandslideRecipe::new));

    // Item Recipes

    public static final RegistryObject<HeatingRecipe.Serializer> HEATING = register("heating", HeatingRecipe.Serializer::new);
    public static final RegistryObject<SimpleItemRecipe.Serializer<QuernRecipe>> QUERN = register("quern", () -> new SimpleItemRecipe.Serializer<>(QuernRecipe::new));
    public static final RegistryObject<SimpleItemRecipe.Serializer<ScrapingRecipe>> SCRAPING = register("scraping", () -> new SimpleItemRecipe.Serializer<>(ScrapingRecipe::new));

    // Complex Recipes

    public static final RegistryObject<FluidPotRecipe.Serializer> POT_FLUID = register("pot_fluid", FluidPotRecipe.Serializer::new);
    public static final RegistryObject<SoupPotRecipe.Serializer> POT_SOUP = register("pot_soup", SoupPotRecipe.Serializer::new);
    public static final RegistryObject<KnappingRecipe.Serializer> CLAY_KNAPPING = register("clay_knapping", () -> new KnappingRecipe.Serializer(TFCRecipeTypes.CLAY_KNAPPING));
    public static final RegistryObject<KnappingRecipe.Serializer> FIRE_CLAY_KNAPPING = register("fire_clay_knapping", () -> new KnappingRecipe.Serializer(TFCRecipeTypes.FIRE_CLAY_KNAPPING));
    public static final RegistryObject<KnappingRecipe.Serializer> LEATHER_KNAPPING = register("leather_knapping", () -> new KnappingRecipe.Serializer(TFCRecipeTypes.LEATHER_KNAPPING));
    public static final RegistryObject<RockKnappingRecipe.RockSerializer> ROCK_KNAPPING = register("rock_knapping", RockKnappingRecipe.RockSerializer::new);
    public static final RegistryObject<AlloyRecipe.Serializer> ALLOY = register("alloy", AlloyRecipe.Serializer::new);
    public static final RegistryObject<CastingRecipe.Serializer> CASTING = register("casting", CastingRecipe.Serializer::new);
    public static final RegistryObject<BloomeryRecipe.Serializer> BLOOMERY = register("bloomery", BloomeryRecipe.Serializer::new);
    public static final RegistryObject<SealedBarrelRecipe.Serializer> SEALED_BARREL = register("barrel_sealed", SealedBarrelRecipe.Serializer::new);
    public static final RegistryObject<InstantBarrelRecipe.Serializer> INSTANT_BARREL = register("barrel_instant", InstantBarrelRecipe.Serializer::new);
    public static final RegistryObject<LoomRecipe.Serializer> LOOM = register("loom", LoomRecipe.Serializer::new);

    // Crafting

    public static final RegistryObject<DelegateRecipe.Serializer<CraftingContainer>> DAMAGE_INPUTS_SHAPELESS_CRAFTING = register("damage_inputs_shapeless_crafting", () -> DelegateRecipe.Serializer.shapeless(DamageInputsCraftingRecipe.Shapeless::new));
    public static final RegistryObject<DelegateRecipe.Serializer<CraftingContainer>> DAMAGE_INPUT_SHAPED_CRAFTING = register("damage_inputs_shaped_crafting", () -> DelegateRecipe.Serializer.shaped(DamageInputsCraftingRecipe.Shaped::new));
    public static final RegistryObject<ExtraProductsCraftingRecipe.ExtraProductsSerializer> EXTRA_PRODUCTS_SHAPELESS_CRAFTING = register("extra_products_shapeless_crafting", () -> ExtraProductsCraftingRecipe.ExtraProductsSerializer.shapeless(ExtraProductsCraftingRecipe.Shapeless::new));
    public static final RegistryObject<ExtraProductsCraftingRecipe.ExtraProductsSerializer> EXTRA_PRODUCTS_SHAPED_CRAFTING = register("extra_products_shaped_crafting", () -> ExtraProductsCraftingRecipe.ExtraProductsSerializer.shaped(ExtraProductsCraftingRecipe.Shaped::new));

    public static final RegistryObject<DelegateRecipe.Serializer<CraftingContainer>> SANDWICH = register("sandwich_crafting", () -> DelegateRecipe.Serializer.shaped(SandwichCraftingRecipe::new));

    public static final RegistryObject<CastingCraftingRecipe.Serializer> CASTING_CRAFTING = register("casting_crafting", CastingCraftingRecipe.Serializer::new);

    private static <S extends RecipeSerializer<?>> RegistryObject<S> register(String name, Supplier<S> factory)
    {
        return RECIPE_SERIALIZERS.register(name, factory);
    }
}