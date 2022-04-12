/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import org.jetbrains.annotations.Nullable;

public class HeatableIngredient extends DelegateIngredient
{
    private final int minTemp;
    private final int maxTemp;

    public HeatableIngredient(@Nullable Ingredient delegate, int minTemp, int maxTemp)
    {
        super(delegate);
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    @Override
    public boolean test(@Nullable ItemStack stack)
    {
        if (super.test(stack) && stack != null)
        {
            return stack.getCapability(HeatCapability.CAPABILITY)
                .map(IHeat::getTemperature)
                .map(temp -> temp > minTemp && temp < maxTemp)
                .orElse(false);
        }
        return false;
    }

    public enum Serializer implements IIngredientSerializer<HeatableIngredient>
    {
        INSTANCE;

        @Override
        public HeatableIngredient parse(FriendlyByteBuf buffer)
        {
            final Ingredient internal = Helpers.decodeNullable(buffer, Ingredient::fromNetwork);
            final int min = buffer.readVarInt();
            final int max = buffer.readVarInt();
            return new HeatableIngredient(internal, min, max);
        }

        @Override
        public HeatableIngredient parse(JsonObject json)
        {
            final Ingredient internal = json.has("ingredient") ? Ingredient.fromJson(JsonHelpers.get(json, "ingredient")) : null;
            final int min = JsonHelpers.getAsInt(json, "min_temp", Integer.MIN_VALUE);
            final int max = JsonHelpers.getAsInt(json, "max_temp", Integer.MAX_VALUE);
            return new HeatableIngredient(internal, min, max);
        }

        @Override
        public void write(FriendlyByteBuf buffer, HeatableIngredient ingredient)
        {
            encodeNullable(ingredient, buffer);
            buffer.writeVarInt(ingredient.minTemp);
            buffer.writeVarInt(ingredient.maxTemp);
        }
    }
}
