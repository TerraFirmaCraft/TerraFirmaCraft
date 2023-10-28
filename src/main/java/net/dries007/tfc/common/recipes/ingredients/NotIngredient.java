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

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import org.jetbrains.annotations.Nullable;

public class NotIngredient extends DelegateIngredient
{
    private static final NotIngredient ALWAYS_TRUE = new NotIngredient(null);

    public static NotIngredient of(Ingredient ingredient)
    {
        return new NotIngredient(ingredient);
    }

    public static NotIngredient alwaysTrue()
    {
        return ALWAYS_TRUE;
    }

    protected NotIngredient(@Nullable Ingredient delegate)
    {
        super(delegate);
    }

    @Override
    public boolean test(@Nullable ItemStack stack)
    {
        return !super.test(stack);
    }

    @Nullable
    @Override
    protected ItemStack testDefaultItem(ItemStack stack)
    {
        return test(stack) ? stack : null;
    }

    @Override
    public IIngredientSerializer<? extends DelegateIngredient> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    public enum Serializer implements IIngredientSerializer<NotIngredient>
    {
        INSTANCE;

        @Override
        public NotIngredient parse(FriendlyByteBuf buffer)
        {
            final Ingredient internal = Helpers.decodeNullable(buffer, Ingredient::fromNetwork);
            return new NotIngredient(internal);
        }

        @Override
        public NotIngredient parse(JsonObject json)
        {
            final Ingredient internal = json.has("ingredient") ? Ingredient.fromJson(JsonHelpers.get(json, "ingredient")) : null;
            return new NotIngredient(internal);
        }

        @Override
        public void write(FriendlyByteBuf buffer, NotIngredient ingredient)
        {
            Helpers.encodeNullable(ingredient.delegate, buffer, Ingredient::toNetwork);
        }
    }
}
