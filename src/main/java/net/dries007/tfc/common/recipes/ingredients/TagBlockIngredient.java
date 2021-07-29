package net.dries007.tfc.common.recipes.ingredients;

import java.util.Collection;
import java.util.Objects;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.util.Helpers;

public class TagBlockIngredient implements BlockIngredient
{
    private final ITag<Block> tag;

    private TagBlockIngredient(ITag<Block> tag)
    {
        this.tag = tag;
    }

    @Override
    public boolean test(BlockState state)
    {
        return state.is(tag);
    }

    @Override
    public Collection<Block> getValidBlocks()
    {
        return tag.getValues();
    }

    @Override
    public BlockIngredient.Serializer<?> getSerializer()
    {
        return BlockIngredient.TAG;
    }

    public static class Serializer implements BlockIngredient.Serializer<TagBlockIngredient>
    {
        @Override
        public TagBlockIngredient fromJson(JsonObject json)
        {
            final ResourceLocation tagName = new ResourceLocation(JSONUtils.getAsString(json, "tag"));
            final ITag<Block> tag = Helpers.nonNullOrJsonError(BlockTags.getAllTags().getTag(tagName), "No block tag: " + tagName);
            return new TagBlockIngredient(tag);
        }

        @Override
        public TagBlockIngredient fromNetwork(PacketBuffer buffer)
        {
            final ITag<Block> tag = Objects.requireNonNull(BlockTags.getAllTags().getTag(buffer.readResourceLocation()));
            return new TagBlockIngredient(tag);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, TagBlockIngredient ingredient)
        {
            buffer.writeResourceLocation(Objects.requireNonNull(BlockTags.getAllTags().getId(ingredient.tag)));
        }
    }
}
