/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import org.jetbrains.annotations.Nullable;

public abstract class DelegateIngredient extends Ingredient
{
    @Nullable protected final Ingredient delegate;

    private ItemStack @Nullable [] cachedItemStacks;
    @Nullable private IntList stackingIds;

    public DelegateIngredient(@Nullable Ingredient delegate)
    {
        super(Stream.empty());
        this.delegate = delegate;
        this.cachedItemStacks = null;
        this.stackingIds = null;
    }

    @Override
    public final ItemStack[] getItems()
    {
        if (cachedItemStacks == null || checkInvalidation())
        {
            if (delegate != null)
            {
                cachedItemStacks = Arrays.stream(delegate.getItems())
                    // When these item stacks get initialized, they tend to lack capabilities, since they are created during resource loading
                    // We force them to copy here which *should*, if this is called late enough, cause the capabilities to be present
                    // This is needed for almost all delegate ingredients, which are querying a capability underneath the hood.
                    .map(ItemStack::copy)
                    .map(this::testDefaultItem)
                    .filter(Objects::nonNull)
                    .toArray(ItemStack[]::new);
            }
            else
            {
                cachedItemStacks = getDefaultItems();
            }
        }
        return cachedItemStacks;
    }

    /**
     * Implementors should override this <strong>and</strong> call {@code super.test(stack)} in their implementation.
     */
    @Override
    public boolean test(@Nullable ItemStack stack)
    {
        return delegate == null || delegate.test(stack);
    }

    @Override
    public final IntList getStackingIds()
    {
        if (stackingIds == null || checkInvalidation())
        {
            final ItemStack[] itemStacks = getItems();
            stackingIds = new IntArrayList(itemStacks.length);
            for (ItemStack stack : itemStacks)
            {
                stackingIds.add(StackedContents.getStackingIndex(stack));
            }
            stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
        }
        return stackingIds;
    }

    /**
     * Only used for data generation purposes.
     */
    @Override
    public JsonObject toJson()
    {
        final JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(getSerializer()).toString());
        if (delegate != null)
        {
            json.add("ingredient", delegate.toJson());
        }
        return json;
    }

    @Override
    public boolean isEmpty()
    {
        return delegate != null && delegate.isEmpty();
    }

    @Override
    protected void invalidate()
    {
        cachedItemStacks = null;
        stackingIds = null;
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    @Override
    public abstract IIngredientSerializer<? extends DelegateIngredient> getSerializer();

    /**
     * @return The default items that this ingredient matches when there is no delegate. In order to respect item based caches this <strong>must</strong> return all possible items that could match this ingredient.
     */
    protected ItemStack[] getDefaultItems()
    {
        return ForgeRegistries.ITEMS.getValues()
            .stream()
            .map(item -> {
                final ItemStack stack = new ItemStack(item);
                return testDefaultItem(stack);
            })
            .filter(Objects::nonNull)
            .toArray(ItemStack[]::new);
    }

    /**
     * Tests if an item stack is valid for the default items, and applies specific traits (usually desirable to show in JEI) if possible.
     *
     * @return {@code null} if the item is not valid for this ingredient, otherwise return the stack possibly with modifications.
     */
    @Nullable
    protected ItemStack testDefaultItem(ItemStack stack)
    {
        return stack;
    }
}