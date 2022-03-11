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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

public class TagBlockIngredient implements BlockIngredient
{
    private final Tag<Block> tag;

    private TagBlockIngredient(Tag<Block> tag)
    {
        this.tag = tag;
    }

    @Override
    public boolean test(BlockState state)
    {
        return Helpers.isBlock(state, tag);
    }

    @Override
    public Collection<Block> getValidBlocks()
    {
        return tag.getValues();
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
            return new TagBlockIngredient(JsonHelpers.getTag(json, "tag", SerializationTags.getInstance().getOrEmpty(Registry.BLOCK_REGISTRY)));
        }

        @Override
        public TagBlockIngredient fromNetwork(FriendlyByteBuf buffer)
        {
            final Tag<Block> tag = SerializationTags.getInstance().getOrEmpty(Registry.BLOCK_REGISTRY).getTagOrEmpty(buffer.readResourceLocation());
            return new TagBlockIngredient(tag);
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        public void toNetwork(FriendlyByteBuf buffer, TagBlockIngredient ingredient)
        {
            buffer.writeResourceLocation(BlockTags.getAllTags().getId(ingredient.tag));
        }
    }
}
