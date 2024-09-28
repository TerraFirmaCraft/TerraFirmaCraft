/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.Objects;
import java.util.stream.Stream;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.util.Helpers;

public record FluidContentIngredient(SizedFluidIngredient fluid) implements ICustomIngredient
{
    public static final MapCodec<FluidContentIngredient> CODEC = SizedFluidIngredient.FLAT_CODEC.fieldOf("fluid").xmap(FluidContentIngredient::new, FluidContentIngredient::fluid);
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidContentIngredient> STREAM_CODEC = SizedFluidIngredient.STREAM_CODEC.map(FluidContentIngredient::new, FluidContentIngredient::fluid);

    public static Ingredient of(Fluid fluid, int amount)
    {
        return new FluidContentIngredient(SizedFluidIngredient.of(fluid, amount)).toVanilla();
    }

    @Override
    public boolean test(ItemStack stack)
    {
        return fluid.test(FluidHelpers.getContainedFluid(stack));
    }

    @Override
    public Stream<ItemStack> getItems()
    {
        return RecipeHelpers.stream(fluid)
            .flatMap(fluid -> Helpers.allItems(TFCTags.Items.FLUID_ITEM_INGREDIENT_EMPTY_CONTAINERS)
                .map(item -> {
                    final ItemStack stack = new ItemStack(item);
                    final IFluidHandlerItem fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
                    if (fluidHandler != null)
                    {
                        // Attempt to fill with the current fluid
                        fluidHandler.fill(new FluidStack(fluid, Integer.MAX_VALUE), IFluidHandler.FluidAction.EXECUTE);

                        // Then attempt to drain, and ensure the content matches the filled fluid, and is of amount > the required amount.
                        final FluidStack content = fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
                        if (content.getFluid() == fluid && content.getAmount() >= this.fluid.amount())
                        {
                            return fluidHandler.getContainer();
                        }
                    }
                    return null;
                }))
            .filter(Objects::nonNull);
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    @Override
    public IngredientType<?> getType()
    {
        return TFCIngredients.FLUID_CONTENT.get();
    }
}
