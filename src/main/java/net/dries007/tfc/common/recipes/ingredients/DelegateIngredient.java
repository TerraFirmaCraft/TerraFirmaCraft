package net.dries007.tfc.common.recipes.ingredients;

import java.util.stream.Stream;
import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import it.unimi.dsi.fastutil.ints.IntList;
import net.dries007.tfc.mixin.item.crafting.IngredientAccessor;

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
        ((IngredientAccessor) delegate).invoke$invalidate();
    }

    @Override
    public boolean isSimple()
    {
        return delegate.isSimple();
    }

    @Override
    public abstract IIngredientSerializer<? extends Ingredient> getSerializer();
}
