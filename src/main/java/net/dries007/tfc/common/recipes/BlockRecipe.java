/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Optional;
import java.util.function.BiFunction;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.network.StreamCodecs;
import net.dries007.tfc.world.Codecs;

/**
 * Generic class for single block -> block based in-world crafting recipes.
 */
public abstract class BlockRecipe implements INoopInputRecipe, IRecipePredicate<BlockState>
{
    public static <R extends BlockRecipe> RecipeSerializer<R> serializer(BiFunction<BlockIngredient, Optional<BlockState>, R> factory)
    {
        return new RecipeSerializerImpl<>(codec(factory), streamCodec(factory));
    }

    public static <R extends BlockRecipe> MapCodec<R> codec(BiFunction<BlockIngredient, Optional<BlockState>, R> factory)
    {
        return RecordCodecBuilder.mapCodec(i -> i.group(
            BlockIngredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
            Codec.mapEither(
                Codec.BOOL.fieldOf("copy_input"),
                Codecs.BLOCK_STATE.fieldOf("result")
            ).<Optional<BlockState>>flatXmap(
                e -> e.map(
                    l -> l ? DataResult.success(Optional.empty()) : DataResult.error(() -> "Must specify result if copy_input is false"),
                    r -> DataResult.success(Optional.of(r))
                ),
                e -> DataResult.success(e.isPresent() ? Either.right(e.get()) : Either.left(true))
            ).forGetter(c -> c.output)
        ).apply(i, factory));
    }

    public static <R extends BlockRecipe> StreamCodec<RegistryFriendlyByteBuf, R> streamCodec(BiFunction<BlockIngredient, Optional<BlockState>, R> factory)
    {
        return StreamCodec.composite(
            BlockIngredient.STREAM_CODEC, c -> c.ingredient,
            ByteBufCodecs.optional(StreamCodecs.BLOCK_STATE), c -> c.output,
            factory
        );
    }

    protected final BlockIngredient ingredient;
    protected final Optional<BlockState> output; // If empty, then copy the input state

    protected BlockRecipe(BlockIngredient ingredient, Optional<BlockState> output)
    {
        this.ingredient = ingredient;
        this.output = output;
    }

    @Override
    public boolean matches(BlockState state)
    {
        return ingredient.test(state);
    }

    /**
     * @param input The input to this recipe
     * @return The output of this recipe, given the input.
     */
    public BlockState assembleBlock(BlockState input)
    {
        return output.orElse(input);
    }

    public BlockIngredient getBlockIngredient()
    {
        return ingredient;
    }
}