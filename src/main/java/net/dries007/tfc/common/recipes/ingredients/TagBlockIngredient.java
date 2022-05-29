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

import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

public record TagBlockIngredient(TagKey<Block> tag) implements BlockIngredient
{
    @Override
    public boolean test(BlockState state)
    {
        return Helpers.isBlock(state, tag);
    }

    @Override
    public Collection<Block> getValidBlocks()
    {
        return Helpers.getAllTagValues(tag, ForgeRegistries.BLOCKS);
    }

    @Override
    public BlockIngredient.Serializer<?> serializer()
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
