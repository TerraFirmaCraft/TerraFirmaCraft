/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import com.google.gson.JsonElement;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.Helpers;

public record BlockIngredient(List<IngredientType.Entry<Block>> entries) implements IngredientType<Block>
{
    private static final Factory<Block, BlockIngredient> FACTORY = new Factory<>("block", BuiltInRegistries.BLOCK, BlockTag::new, BlockIngredient::new);

    public static BlockIngredient fromJson(JsonElement json)
    {
        return IngredientType.fromJson(json, FACTORY);
    }

    public static BlockIngredient fromNetwork(FriendlyByteBuf buffer)
    {
        return IngredientType.fromNetwork(buffer, FACTORY);
    }

    /**
     * @return Overload for {@link #test(Object)} which takes a {@link BlockState} for convenience, even though the underlying ingredient will never be state-specific.
     */
    public boolean test(BlockState state)
    {
        return test(state.getBlock());
    }

    @Override
    public JsonElement toJson()
    {
        return IngredientType.toJson(this, FACTORY);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer)
    {
        IngredientType.toNetwork(buffer, this, FACTORY);
    }

    public Collection<Block> blocks()
    {
        return all().toList();
    }

    public record BlockTag(TagKey<Block> tag) implements TagEntry<Block>
    {
        @Override
        public Stream<Block> stream()
        {
            return Helpers.allBlocks(tag);
        }

        @Override
        public boolean test(Block block)
        {
            return Helpers.isBlock(block, tag);
        }
    }
}
