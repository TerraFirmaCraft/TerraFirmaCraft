/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.Collection;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

public class TagBlockIngredient implements BlockIngredient
{
    private final TagKey<Block> tag;

    private TagBlockIngredient(TagKey<Block> tag)
    {
        this.tag = tag;
    }

    @Override
    public boolean test(BlockState state)
    {
        return Helpers.isBlock(state, tag);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Collection<Block> getValidBlocks()
    {
        return Helpers.getAllTagValues(tag, Registry.BLOCK);
    }

    @Override
    public BlockIngredient.Serializer<?> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    public enum Serializer implements BlockIngredient.Serializer<TagBlockIngredient>
    {
        INSTANCE;

        @Override
        public TagBlockIngredient fromJson(JsonObject json)
        {
            return new TagBlockIngredient(JsonHelpers.getTag(json, "tag", Registry.BLOCK_REGISTRY));
        }

        @Override
        public TagBlockIngredient fromNetwork(FriendlyByteBuf buffer)
        {
            return new TagBlockIngredient(JsonHelpers.getTag(buffer.readResourceLocation().toString(), Registry.BLOCK_REGISTRY));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, TagBlockIngredient ingredient)
        {
            buffer.writeResourceLocation(ingredient.tag.location());
        }
    }
}
