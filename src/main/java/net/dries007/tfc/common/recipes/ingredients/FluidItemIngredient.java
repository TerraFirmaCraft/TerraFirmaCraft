/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.Objects;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import org.jetbrains.annotations.Nullable;

public class FluidItemIngredient extends DelegateIngredient
{
    private final FluidStackIngredient fluid;

    public FluidItemIngredient(@Nullable Ingredient delegate, FluidStackIngredient fluid)
    {
        super(delegate);
        this.fluid = fluid;
    }

    @Override
    public boolean test(@Nullable ItemStack stack)
    {
        if (super.test(stack) && stack != null && !stack.isEmpty())
        {
            return stack.getCapability(Capabilities.FLUID_ITEM)
                .map(cap -> cap.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE))
                .filter(fluid)
                .isPresent();
        }
        return false;
    }

    @Override
    public IIngredientSerializer<? extends DelegateIngredient> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    @Override
    protected ItemStack[] getDefaultItems()
    {
        return fluid.ingredient()
            .getMatchingFluids()
            .stream()
            .flatMap(fluid -> Helpers.streamAllTagValues(TFCTags.Items.FLUID_ITEM_INGREDIENT_EMPTY_CONTAINERS, ForgeRegistries.ITEMS)
                .map(item -> {
                    final ItemStack stack = new ItemStack(item);
                    final IFluidHandlerItem fluidHandler = Helpers.getCapability(stack, Capabilities.FLUID_ITEM);
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
            .filter(Objects::nonNull)
            .toArray(ItemStack[]::new);
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject json = super.toJson();
        json.add("fluid_ingredient", fluid.toJson());
        return json;
    }

    public enum Serializer implements IIngredientSerializer<FluidItemIngredient>
    {
        INSTANCE;

        @Override
        public FluidItemIngredient parse(FriendlyByteBuf buffer)
        {
            final Ingredient internal = Helpers.decodeNullable(buffer, Ingredient::fromNetwork);
            final FluidStackIngredient fluid = FluidStackIngredient.fromNetwork(buffer);
            return new FluidItemIngredient(internal, fluid);
        }

        @Override
        public FluidItemIngredient parse(JsonObject json)
        {
            final Ingredient internal = json.has("ingredient") ? Ingredient.fromJson(JsonHelpers.get(json, "ingredient")) : null;
            final FluidStackIngredient fluid = FluidStackIngredient.fromJson(json.getAsJsonObject("fluid_ingredient")); // avoid name conflict with ingredient field
            return new FluidItemIngredient(internal, fluid);
        }

        @Override
        public void write(FriendlyByteBuf buffer, FluidItemIngredient ingredient)
        {
            Helpers.encodeNullable(ingredient.delegate, buffer, Ingredient::toNetwork);
            ingredient.fluid.toNetwork(buffer);
        }
    }
}
