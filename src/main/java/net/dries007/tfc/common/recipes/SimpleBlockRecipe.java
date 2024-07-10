/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Optional;
import java.util.function.BiFunction;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.network.PacketCodecs;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.world.Codecs;

/**
 * Generic class for single block -> block based in-world crafting recipes.
 */
public abstract class SimpleBlockRecipe implements IBlockRecipe
{
    public static <R extends SimpleBlockRecipe> RecipeSerializer<R> serializer(BiFunction<BlockIngredient, Optional<BlockState>, R> factory)
    {
        return new RecipeSerializerImpl<>(codec(factory), streamCodec(factory));
    }

    public static <R extends SimpleBlockRecipe> MapCodec<R> codec(BiFunction<BlockIngredient, Optional<BlockState>, R> factory)
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

    public static <R extends SimpleBlockRecipe> StreamCodec<RegistryFriendlyByteBuf, R> streamCodec(BiFunction<BlockIngredient, Optional<BlockState>, R> factory)
    {
        return StreamCodec.composite(
            BlockIngredient.STREAM_CODEC, c -> c.ingredient,
            ByteBufCodecs.optional(PacketCodecs.BLOCK_STATE), c -> c.output,
            factory
        );
    }

    protected final BlockIngredient ingredient;
    protected final Optional<BlockState> output; // If empty, then copy the input state

    protected SimpleBlockRecipe(BlockIngredient ingredient, Optional<BlockState> output)
    {
        this.ingredient = ingredient;
        this.output = output;
    }

    @Override
    public boolean matches(BlockState state)
    {
        return ingredient.test(state);
    }

    @Override
    public BlockState assembleBlock(BlockState input)
    {
        return output.orElse(input);
    }

    public BlockIngredient getBlockIngredient()
    {
        return ingredient;
    }
}