/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.container.SewingTableContainer;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

public class SewingRecipe implements ISimpleRecipe<SewingTableContainer.RecipeWrapper>
{
    private final ResourceLocation id;
    private final List<Integer> stitches;
    private final List<Integer> squares;
    private final ItemStack result;

    public SewingRecipe(ResourceLocation id, List<Integer> stitches, List<Integer> squares, ItemStack result)
    {
        this.id = id;
        this.stitches = stitches;
        this.squares = squares;
        this.result = result;
    }

    public List<Integer> getStitches()
    {
        return stitches;
    }

    public List<Integer> getSquares()
    {
        return squares;
    }

    @Override
    public boolean matches(SewingTableContainer.RecipeWrapper inventory, Level level)
    {
        return inventory.squaresMatch(squares) && inventory.stitchesMatch(stitches);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access)
    {
        return result.copy();
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.SEWING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.SEWING.get();
    }

    public static class Serializer extends RecipeSerializerImpl<SewingRecipe>
    {

        @Override
        public SewingRecipe fromJson(ResourceLocation id, JsonObject json)
        {
            final JsonArray stitchArray = json.getAsJsonArray("stitches");
            if (stitchArray.size() != SewingTableContainer.MAX_STITCHES)
                throw new JsonParseException("There must be exactly 45 stitches specified, was " + stitchArray.size());
            final List<Integer> stitches = new ArrayList<>(SewingTableContainer.MAX_STITCHES);
            for (JsonElement element : stitchArray)
            {
                stitches.add(element.getAsInt());
            }
            final JsonArray squareArray = json.getAsJsonArray("squares");
            if (squareArray.size() != SewingTableContainer.MAX_SQUARES)
                throw new JsonParseException("There must be exactly 32 squares specified, was " + squareArray.size());
            final List<Integer> squares = new ArrayList<>(SewingTableContainer.MAX_SQUARES);
            for (JsonElement element : squareArray)
            {
                squares.add(element.getAsInt());
            }
            final ItemStack result = JsonHelpers.getItemStack(json, "result");
            return new SewingRecipe(id, stitches, squares, result);
        }

        @Override
        public @Nullable SewingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
        {
            final List<Integer> stitches = Helpers.decodeAll(buffer, new ArrayList<>(SewingTableContainer.MAX_STITCHES), FriendlyByteBuf::readVarInt);
            final List<Integer> squares = Helpers.decodeAll(buffer, new ArrayList<>(SewingTableContainer.MAX_SQUARES), FriendlyByteBuf::readVarInt);
            final ItemStack item = buffer.readItem();
            return new SewingRecipe(id, stitches, squares, item);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SewingRecipe recipe)
        {
            Helpers.encodeAll(buffer, recipe.stitches, (i, buf) -> buf.writeVarInt(i));
            Helpers.encodeAll(buffer, recipe.squares, (i, buf) -> buf.writeVarInt(i));
            buffer.writeItem(recipe.result);
        }
    }

}
