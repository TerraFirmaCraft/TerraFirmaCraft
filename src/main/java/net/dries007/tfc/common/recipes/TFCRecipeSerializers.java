/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.function.Supplier;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCRecipeSerializers
{
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);

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


    // Delegate Recipe Types

    public static final RegistryObject<DelegatingRecipe.Serializer<CraftingInventory, DamageInputsCraftingRecipe>> DAMAGE_INPUTS_CRAFTING = register("damage_inputs_crafting", () -> new DelegatingRecipe.Serializer<>(DamageInputsCraftingRecipe::new));

    private static <S extends IRecipeSerializer<?>> RegistryObject<S> register(String name, Supplier<S> factory)
    {
        return RECIPE_SERIALIZERS.register(name, factory);
    }
}