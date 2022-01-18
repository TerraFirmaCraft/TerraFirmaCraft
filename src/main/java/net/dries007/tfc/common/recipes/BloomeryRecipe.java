package net.dries007.tfc.common.recipes;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;

//todo: ItemStackInventory or something else entirely??
public class BloomeryRecipe implements ISimpleRecipe<ItemStackInventory>
{

    @Nullable
    public static BloomeryRecipe getRecipe(ItemStack stack)
    {
        return getRecipe(new ItemStackInventory(stack));
    }

    //todo
    @Nullable
    public static BloomeryRecipe getRecipe(ItemStackInventory wrapper)
    {
        return null;
    }

    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final ItemStack outputItem;

    public BloomeryRecipe(ResourceLocation id, Ingredient ingredient, ItemStack outputItem)
    {
        this.id = id;
        this.ingredient = ingredient;
        this.outputItem = outputItem;
    }

    //todo
    @Override
    public boolean matches(ItemStackInventory inv, Level level)
    {
        return false;
    }

    @Override
    public ItemStack getResultItem()
    {
        return outputItem;
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

    public static class Serializer extends RecipeSerializerImpl<BloomeryRecipe>
    {
        //todo: I must determine what a bloomery recipe json looks like before proceeding
        @Override
        public BloomeryRecipe fromJson(ResourceLocation p_44103_, JsonObject p_44104_)
        {
            return null;
        }

        @org.jetbrains.annotations.Nullable
        @Override
        public BloomeryRecipe fromNetwork(ResourceLocation p_44105_, FriendlyByteBuf p_44106_)
        {
            return null;
        }

        @Override
        public void toNetwork(FriendlyByteBuf p_44101_, BloomeryRecipe p_44102_)
        {

        }
    }
}
