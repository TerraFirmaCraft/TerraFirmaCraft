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

    // Complex Recipes

    public static final RegistryObject<SimplePotRecipe.Serializer> SIMPLE_POT = register("simple_pot", () -> new SimplePotRecipe.Serializer(SimplePotRecipe::new));

    // Delegate Recipe Types

    public static final RegistryObject<DelegatingRecipe.Serializer<CraftingInventory, DamageInputsCraftingRecipe>> DAMAGE_INPUTS_CRAFTING = register("damage_inputs_crafting", () -> new DelegatingRecipe.Serializer<>(DamageInputsCraftingRecipe::new));

    private static <S extends IRecipeSerializer<?>> RegistryObject<S> register(String name, Supplier<S> factory)
    {
        return RECIPE_SERIALIZERS.register(name, factory);
    }
}