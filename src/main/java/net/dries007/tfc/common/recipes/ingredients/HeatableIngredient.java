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
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

public class HeatableIngredient extends DelegateIngredient
{
    public static HeatableIngredient of(int minTemp, int maxTemp)
    {
        return of(null, minTemp, maxTemp);
    }

    public static HeatableIngredient of(@Nullable Ingredient ingredient, int minTemp, int maxTemp)
    {
        return new HeatableIngredient(ingredient, minTemp, maxTemp);
    }

    private final int minTemp;
    private final int maxTemp;

    protected HeatableIngredient(@Nullable Ingredient delegate, int minTemp, int maxTemp)
    {
        super(delegate);
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }

    @Override
    public IIngredientSerializer<? extends DelegateIngredient> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    @Override
    @Nullable
    protected ItemStack testDefaultItem(ItemStack stack)
    {
        final @Nullable IHeat heat = HeatCapability.get(stack);
        if (heat != null)
        {
            heat.setTemperature(minTemp);
            return stack;
        }
        return null;
    }

    @Override
    public boolean test(@Nullable ItemStack stack)
    {
        if (super.test(stack) && stack != null && !stack.isEmpty())
        {
            final @Nullable IHeat heat = HeatCapability.get(stack);
            return heat != null && heat.getTemperature() >= minTemp && heat.getTemperature() <= maxTemp;
        }
        return false;
    }

    @Override
    public JsonObject toJson()
    {
        final JsonObject json = super.toJson();
        if (minTemp != Integer.MIN_VALUE)
        {
            json.addProperty("min_temp", minTemp);
        }
        if (maxTemp != Integer.MAX_VALUE)
        {
            json.addProperty("max_temp", maxTemp);
        }
        return json;
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
            Helpers.encodeNullable(ingredient.delegate, buffer, Ingredient::toNetwork);
            buffer.writeVarInt(ingredient.minTemp);
            buffer.writeVarInt(ingredient.maxTemp);
        }
    }
}
