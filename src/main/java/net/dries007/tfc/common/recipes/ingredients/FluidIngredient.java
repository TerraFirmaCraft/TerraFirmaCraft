/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.Collection;
import java.util.function.Predicate;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.util.Helpers;

public interface FluidIngredient extends Predicate<FluidStack>
{
    SimpleFluidIngredient.Serializer FLUID = FluidIngredients.register(Helpers.identifier("fluid"), new SimpleFluidIngredient.Serializer());
    TagFluidIngredient.Serializer TAG = FluidIngredients.register(Helpers.identifier("tag"), new TagFluidIngredient.Serializer());

    /**
     * Test the ingredient against the provided fluid stack, including amounts.
     */
    @Override
    boolean test(FluidStack fluidStack);

    /**
     * Test the ingredient against the provided fluid stack, ignoring amounts.
     */
    boolean testIgnoreAmount(Fluid fluid);

    /**
     * Get all possible fluids that can matching this ingredient
     */
    Collection<Fluid> getMatchingFluids();

    FluidIngredient.Serializer<?> getSerializer();

    interface Serializer<T extends FluidIngredient>
    {
        T fromJson(JsonObject json);

        T fromNetwork(FriendlyByteBuf buffer);

        void toNetwork(FriendlyByteBuf buffer, T ingredient);
    }
}
