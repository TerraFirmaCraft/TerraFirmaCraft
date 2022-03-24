/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import it.unimi.dsi.fastutil.ints.IntList;

public abstract class DelegateIngredient extends Ingredient
{
    protected final Ingredient delegate;

    protected DelegateIngredient(Ingredient delegate)
    {
        super(Stream.empty());
        this.delegate = delegate;
    }

    @Override
    public ItemStack[] getItems()
    {
        return delegate.getItems();
    }

    @Override
    public boolean test(@Nullable ItemStack stack)
    {
        return delegate.test(stack);
    }

    @Override
    public IntList getStackingIds()
    {
        return delegate.getStackingIds();
    }

    @Override
    public JsonElement toJson()
    {
        return delegate.toJson();
    }

    @Override
    public boolean isEmpty()
    {
        return delegate.isEmpty();
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
        return delegate.isSimple();
    }

    @Override
    public abstract IIngredientSerializer<? extends Ingredient> getSerializer();
}
