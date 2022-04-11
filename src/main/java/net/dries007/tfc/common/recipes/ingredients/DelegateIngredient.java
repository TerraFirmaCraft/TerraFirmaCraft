/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.stream.Stream;

import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import it.unimi.dsi.fastutil.ints.IntList;

public abstract class DelegateIngredient extends Ingredient
{
    public static void encodeNullable(DelegateIngredient ingredient, FriendlyByteBuf buffer)
    {
        Helpers.encodeNullable(ingredient, buffer, (ing, buf) -> {
            // we null-checked in the Helpers call
            assert ing.delegate != null;
            ing.delegate.toNetwork(buf);
        });
    }

    @Nullable
    protected final Ingredient delegate;

    public DelegateIngredient(@Nullable Ingredient delegate)
    {
        super(Stream.empty());
        this.delegate = delegate;
    }

    @Override
    public ItemStack[] getItems()
    {
        return delegate != null ? delegate.getItems() : new ItemStack[] {};
    }

    @Override
    public boolean test(@Nullable ItemStack stack)
    {
        return delegate == null || delegate.test(stack);
    }

    @Override
    public IntList getStackingIds()
    {
        return delegate != null ? delegate.getStackingIds() : IntList.of();
    }

    @Override
    public JsonElement toJson()
    {
        return delegate != null ? delegate.toJson() : new JsonArray();
    }

    @Override
    public boolean isEmpty()
    {
        return delegate == null || delegate.isEmpty();
    }

    @Override
    protected void invalidate()
    {
        // todo: mixin
        // this is forge added so we can't AT it.
        // even though forge *literally doesn't use it* though *technically it could cause a bug* but do I care? nah.
        // delegate.invalidate(); // ((IngredientAccessor) delegate).invoke$invalidate();
    }

    @Override
    public boolean isSimple()
    {
        return delegate == null || delegate.isSimple();
    }

    @Override
    public abstract IIngredientSerializer<? extends Ingredient> getSerializer();
}
