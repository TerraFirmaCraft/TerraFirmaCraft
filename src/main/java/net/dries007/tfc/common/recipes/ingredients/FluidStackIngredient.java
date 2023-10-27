/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.function.Predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.JsonHelpers;

/**
 * An ingredient for a (fluid, amount) pair.
 * Used in conjunction with recipes that accept {@link FluidStack}s.
 */
public record FluidStackIngredient(FluidIngredient ingredient, int amount) implements Predicate<FluidStack>
{
    public static final FluidStackIngredient EMPTY = new FluidStackIngredient(FluidIngredient.of(Fluids.EMPTY), 0);

    public static FluidStackIngredient fromJson(JsonObject json)
    {
        final FluidIngredient fluid = FluidIngredient.fromJson(JsonHelpers.get(json, "ingredient"));
        final int amount = JsonHelpers.getAsInt(json, "amount", FluidHelpers.BUCKET_VOLUME);
        if (amount <= 0)
        {
            throw new JsonParseException("FluidStackIngredient 'amount' must be positive.");
        }
        return new FluidStackIngredient(fluid, amount);
    }

    public static FluidStackIngredient fromNetwork(FriendlyByteBuf buffer)
    {
        final FluidIngredient fluid = FluidIngredient.fromNetwork(buffer);
        final int amount = buffer.readVarInt();
        return new FluidStackIngredient(fluid, amount);
    }

    public void toNetwork(FriendlyByteBuf buffer)
    {
        FluidIngredient.toNetwork(buffer, ingredient);
        buffer.writeVarInt(amount);
    }

    @Override
    public boolean test(FluidStack stack)
    {
        return stack.getAmount() >= amount && ingredient.test(stack.getFluid());
    }

    public JsonElement toJson()
    {
        JsonObject json = new JsonObject();
        json.add("ingredient", ingredient.toJson());
        json.addProperty("amount", amount);
        return json;
    }
}
