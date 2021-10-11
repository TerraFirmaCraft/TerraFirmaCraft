/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.capabilities.MoldLike;

public class CastingCraftingRecipe implements CraftingRecipe, ISimpleRecipe<CraftingContainer>
{
    private final ResourceLocation id;

    public CastingCraftingRecipe(ResourceLocation id)
    {
        this.id = id;
    }

    @Override
    public boolean matches(CraftingContainer inventory, @Nullable Level level)
    {
        final MoldLike mold = getMold(inventory);
        return mold != null && CastingRecipe.get(mold) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer inventory)
    {
        final MoldLike mold = getMold(inventory);
        if (mold != null)
        {
            final CastingRecipe recipe = CastingRecipe.get(mold);
            if (recipe != null)
            {
                return recipe.assemble(mold);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.CASTING_CRAFTING.get();
    }

    /**
     * @return The single mold in the crafting container, if one and only exactly one can be found, otherwise null.
     */
    @Nullable
    private MoldLike getMold(CraftingContainer inventory)
    {
        MoldLike mold = null;
        for (int i = 0; i < inventory.getContainerSize(); i++)
        {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty())
            {
                if (mold == null)
                {
                    mold = MoldLike.get(stack);
                    if (mold == null)
                    {
                        return null; // stack that's not a mold
                    }
                }
                else
                {
                    return null; // more than one non-empty stack
                }
            }
        }
        return mold;
    }

    public static class Serializer extends RecipeSerializerImpl<CastingCraftingRecipe>
    {
        @Override
        public CastingCraftingRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            return new CastingCraftingRecipe(recipeId);
        }

        @Nullable
        @Override
        public CastingCraftingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            return new CastingCraftingRecipe(recipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CastingCraftingRecipe recipe) {}
    }
}
