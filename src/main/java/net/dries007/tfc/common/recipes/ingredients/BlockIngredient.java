/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.Collection;
import java.util.function.Predicate;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This is a simple predicate wrapper for block states.
 * It can compare a single or multiple blocks, or a tag.
 */
public interface BlockIngredient extends Predicate<BlockState>
{
    /**
     * Test if the specified block state is accepted by the ingredient
     */
    @Override
    boolean test(BlockState state);

    /**
     * Return a list of all possible blocks that can be accepted by the ingredient.
     * This is mostly for populating visual lists of recipes and does not obey the exact nature of the ingredient.
     */
    Collection<Block> getValidBlocks();

    BlockIngredient.Serializer<?> serializer();

    @SuppressWarnings({"unchecked", "rawtypes"})
    default void toNetwork(FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(BlockIngredients.getId(serializer()));
        ((BlockIngredient.Serializer) serializer()).toNetwork(buffer, this);
    }

    interface Serializer<T extends BlockIngredient>
    {
        T fromJson(JsonObject json);

        T fromNetwork(FriendlyByteBuf buffer);

        void toNetwork(FriendlyByteBuf buffer, T ingredient);
    }
}