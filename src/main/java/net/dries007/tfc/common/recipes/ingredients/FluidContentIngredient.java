/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.fluids.FluidHelpers;

// todo 1.21 implement the getItems() with tag values
public record FluidContentIngredient(SizedFluidIngredient fluid) implements PreciseIngredient
{
    public static final MapCodec<FluidContentIngredient> CODEC = SizedFluidIngredient.FLAT_CODEC.fieldOf("fluid").xmap(FluidContentIngredient::new, FluidContentIngredient::fluid);
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidContentIngredient> STREAM_CODEC = SizedFluidIngredient.STREAM_CODEC.map(FluidContentIngredient::new, FluidContentIngredient::fluid);

    @Override
    public boolean test(ItemStack stack)
    {
        return fluid.test(FluidHelpers.getContainedFluid(stack));
    }

    @Override
    public IngredientType<?> getType()
    {
        return TFCIngredients.FLUID_CONTENT.get();
    }
}
