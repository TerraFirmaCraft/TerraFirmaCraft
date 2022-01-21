package net.dries007.tfc.common.recipes;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.blockentities.BloomeryBlockEntity;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.JsonHelpers;

public class BloomeryRecipe implements ISimpleRecipe<BloomeryBlockEntity.BloomeryInventory>
{
    //this is here so that it compiles :)
    @Nullable
    public static BloomeryRecipe getRecipe(ItemStack inputStack)
    {
        return getRecipe(new ItemStackInventory(inputStack));
    }

    //todo - how?
    @Nullable
    public static BloomeryRecipe getRecipe(ItemStackInventory wrapper)
    {
        return null;
    }

    private final ResourceLocation id;
    private final FluidStackIngredient fluid;
    private final ItemStackIngredient catalyst;
    private final ItemStack result;
    private final int time;

    public BloomeryRecipe(ResourceLocation id, FluidStackIngredient fluid, ItemStackIngredient catalyst, ItemStack result, int time)
    {
        this.id = id;
        this.fluid = fluid;
        this.catalyst = catalyst;
        this.result = result;
        this.time = time;
    }

    public int getTime()
    {
        return time;
    }

    public ItemStackIngredient getCatalyst()
    {
        return catalyst;
    }

    public FluidStackIngredient getFluidIngredient()
    {
        return fluid;
    }

    //todo
    @Override
    public boolean matches(BloomeryBlockEntity.BloomeryInventory inv, Level level)
    {
        return false;
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

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.BLOOMERY.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.BLOOMERY.get();
    }

    //todo
    public boolean isValidInput(ItemStack stack)
    {
        return false;
    }

    //todo
    public boolean isValidCatalyst(ItemStack stack)
    {
        return false;
    }

    public static class Serializer extends RecipeSerializerImpl<BloomeryRecipe>
    {
        @Override
        public BloomeryRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final FluidStackIngredient fluid = FluidStackIngredient.fromJson(JsonHelpers.getAsJsonObject(json, "fluid"));
            final ItemStackIngredient catalyst = ItemStackIngredient.fromJson(JsonHelpers.getAsJsonObject(json, "catalyst"));
            final ItemStack result = JsonHelpers.getItemStack(json, "result");
            final int time = JsonHelpers.getAsInt(json, "time");
            return new BloomeryRecipe(recipeId, fluid, catalyst, result, time);
        }

        @Nullable
        @Override
        public BloomeryRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final FluidStackIngredient fluid = FluidStackIngredient.fromNetwork(buffer);
            final ItemStackIngredient catalyst = ItemStackIngredient.fromNetwork(buffer);
            final ItemStack result = buffer.readItem();
            final int time = buffer.readInt();
            return new BloomeryRecipe(recipeId, fluid, catalyst, result, time);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, BloomeryRecipe recipe)
        {
            FluidStackIngredient.toNetwork(buffer, recipe.fluid);
            recipe.catalyst.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeInt(recipe.time);
        }
    }
}
