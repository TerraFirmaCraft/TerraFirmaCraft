package net.dries007.tfc.common.recipes;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Handling for simple Ingredient -> ItemStack recipes
 */
public abstract class SimpleItemRecipe implements ISimpleRecipe<ItemStackRecipeWrapper>
{
    protected final ResourceLocation id;
    protected final Ingredient ingredient;
    protected final ItemStack result;

    public SimpleItemRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result)
    {
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
    }

    @Override
    public boolean matches(ItemStackRecipeWrapper wrapper, World worldIn)
    {
        return this.ingredient.test(wrapper.getStack());
    }

    @Override
    public ItemStack assemble(ItemStackRecipeWrapper wrapper)
    {
        return result.copy();
    }

    @Override
    public ItemStack getResultItem()
    {
        return result;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    public static class Serializer<R extends SimpleItemRecipe> extends RecipeSerializer<R>
    {
        private final SimpleItemRecipe.Serializer.Factory<R> factory;

        public Serializer(SimpleItemRecipe.Serializer.Factory<R> factory)
        {
            this.factory = factory;
        }

        @Override
        public R fromJson(ResourceLocation recipeId, JsonObject json)
        {
            Ingredient ingredient = Ingredient.fromJson(json);
            ItemStack stack = ShapedRecipe.itemFromJson(json);
            return factory.create(recipeId, ingredient, stack);
        }

        @Nullable
        @Override
        public R fromNetwork(ResourceLocation recipeId, PacketBuffer buffer)
        {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack stack = buffer.readItem();
            return factory.create(recipeId, ingredient, stack);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, R recipe)
        {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.getResultItem());
        }

        protected interface Factory<R extends SimpleItemRecipe>
        {
            R create(ResourceLocation id, Ingredient ingredient, ItemStack stack);
        }
    }
}
