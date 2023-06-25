/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import org.apache.commons.lang3.function.TriFunction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

import org.jetbrains.annotations.Nullable;

public abstract class ExtraProductsCraftingRecipe<R extends Recipe<CraftingContainer>> extends DelegateRecipe<R, CraftingContainer> implements CraftingRecipe
{
    private final CraftingBookCategory category;
    private final List<ItemStack> extraProducts;

    protected ExtraProductsCraftingRecipe(ResourceLocation id, CraftingBookCategory category, R recipe, List<ItemStack> extraProducts)
    {
        super(id, recipe);
        this.category = category;
        this.extraProducts = extraProducts;
    }

    @Override
    public CraftingBookCategory category()
    {
        return category;
    }

    public List<ItemStack> getExtraProducts()
    {
        return extraProducts;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv)
    {
        Player player = ForgeHooks.getCraftingPlayer();
        if (player != null)
        {
            extraProducts.forEach(item -> ItemHandlerHelper.giveItemToPlayer(player, item.copy()));
        }
        return super.getRemainingItems(inv);
    }

    public static class Shapeless extends ExtraProductsCraftingRecipe<Recipe<CraftingContainer>>
    {
        public Shapeless(ResourceLocation id, CraftingBookCategory category, Recipe<CraftingContainer> recipe, List<ItemStack> extraProducts)
        {
            super(id, category, recipe, extraProducts);
        }

        @Override
        public RecipeSerializer<?> getSerializer()
        {
            return TFCRecipeSerializers.EXTRA_PRODUCTS_SHAPELESS_CRAFTING.get();
        }
    }

    public static class Shaped extends ExtraProductsCraftingRecipe<IShapedRecipe<CraftingContainer>> implements IRecipeDelegate.Shaped<CraftingContainer>
    {
        public Shaped(ResourceLocation id, CraftingBookCategory category, Recipe<CraftingContainer> recipe, List<ItemStack> extraProducts)
        {
            super(id, category, (IShapedRecipe<CraftingContainer>) recipe, extraProducts);
        }

        @Override
        public RecipeSerializer<?> getSerializer()
        {
            return TFCRecipeSerializers.EXTRA_PRODUCTS_SHAPED_CRAFTING.get();
        }
    }

    public static class ExtraProductsSerializer extends RecipeSerializerImpl<ExtraProductsCraftingRecipe<?>>
    {
        public interface Factory
        {
            ExtraProductsCraftingRecipe<?> apply(ResourceLocation id, CraftingBookCategory category, Recipe<CraftingContainer> recipe, List<ItemStack> list);
        }

        public static ExtraProductsSerializer shapeless(Factory factory)
        {
            return new ExtraProductsSerializer((id, category, delegate, list) -> {
                if (delegate instanceof IShapedRecipe)
                {
                    throw new JsonParseException("Mixing shapeless delegate recipe type with shaped delegate, not allowed!");
                }
                return factory.apply(id, category, delegate, list);
            });
        }

        public static ExtraProductsSerializer shaped(Factory factory)
        {
            return new ExtraProductsSerializer((id, category, delegate, list) -> {
                if (!(delegate instanceof IShapedRecipe))
                {
                    throw new JsonParseException("Mixing shaped delegate recipe type with shapeless delegate, not allowed!");
                }
                return factory.apply(id, category, delegate, list);
            });
        }
        private final Factory factory;

        public ExtraProductsSerializer(Factory factory)
        {
            this.factory = factory;
        }

        @Override
        public ExtraProductsCraftingRecipe<?> fromJson(ResourceLocation recipeID, JsonObject json)
        {
            return fromJson(recipeID, json, ICondition.IContext.EMPTY);
        }

        @Override
        @SuppressWarnings("unchecked")
        public ExtraProductsCraftingRecipe<?> fromJson(ResourceLocation recipeID, JsonObject json, ICondition.IContext context)
        {
            List<ItemStack> items = new ArrayList<>();
            for (JsonElement element : json.getAsJsonArray("extra_products"))
            {
                items.add(CraftingHelper.getItemStack(element.getAsJsonObject(), true));
            }
            Recipe<CraftingContainer> internal = (Recipe<CraftingContainer>) RecipeManager.fromJson(DELEGATE, GsonHelper.getAsJsonObject(json, "recipe"), context);
            return factory.apply(recipeID, JsonHelpers.getCraftingCategory(json), internal, items);
        }

        @Nullable
        @SuppressWarnings("unchecked")
        @Override
        public ExtraProductsCraftingRecipe<?> fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buffer)
        {
            CraftingBookCategory cat = buffer.readEnum(CraftingBookCategory.class);
            List<ItemStack> items = new ArrayList<>();
            Helpers.decodeAll(buffer, items, FriendlyByteBuf::readItem);
            Recipe<CraftingContainer> internal = (Recipe<CraftingContainer>) ClientboundUpdateRecipesPacket.fromNetwork(buffer);
            return factory.apply(recipeID, cat, internal, items);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ExtraProductsCraftingRecipe<?> recipe)
        {
            buffer.writeEnum(recipe.category);
            Helpers.encodeAll(buffer, recipe.extraProducts, (item, buf) -> buf.writeItem(item));
            ClientboundUpdateRecipesPacket.toNetwork(buffer, recipe.getDelegate());
        }
    }
}
