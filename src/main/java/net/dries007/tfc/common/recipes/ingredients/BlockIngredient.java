/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.network.StreamCodecs;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Codecs;

/**
 * An ingredient that can express either blocks, or tags of blocks, in a format similar to other ingredient types. This does not bother exposing
 * the degree of complexity as the NeoForge provided fluid ingredients / sized ingredients, as it's pretty unnecessary for our purposes.
 */
public record BlockIngredient(Either<Set<Block>, TagKey<Block>> either) implements Predicate<Block>
{
    public static final Codec<BlockIngredient> CODEC = Codec.either(
        Codecs.BLOCK.listOf().<Set<Block>>xmap(ImmutableSet::copyOf, List::copyOf),
        TagKey.hashedCodec(Registries.BLOCK)
    ).xmap(BlockIngredient::new, BlockIngredient::either);

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockIngredient> STREAM_CODEC = ByteBufCodecs.either(
        StreamCodecs.BLOCK.apply(ByteBufCodecs.list()).<Set<Block>>map(ImmutableSet::copyOf, List::copyOf),
        ResourceLocation.STREAM_CODEC.map(k -> TagKey.create(Registries.BLOCK, k), TagKey::location)
    ).map(BlockIngredient::new, BlockIngredient::either);

    public static BlockIngredient of(TagKey<Block> tag)
    {
        return new BlockIngredient(Either.right(tag));
    }

    public static BlockIngredient of(Block block)
    {
        return new BlockIngredient(Either.left(Set.of(block)));
    }

    public static BlockIngredient of(Stream<Block> blocks)
    {
        return new BlockIngredient(Either.left(ImmutableSet.copyOf(blocks.toList())));
    }

    /**
     * @return Overload for {@link #test(Block)} which takes a {@link BlockState} for convenience, even though the underlying ingredient will never be state-specific.
     */
    public boolean test(BlockState state)
    {
        return test(state.getBlock());
    }

    @Override
    public boolean test(Block block)
    {
        return either.map(s -> s.contains(block), t -> Helpers.isBlock(block, t));
    }

    /**
     * @return A stream of all the individual objects that may match this ingredient.
     */
    public Stream<Block> all()
    {
        return either.map(Collection::stream, Helpers::allBlocks);
    }

    public List<Block> blocks()
    {
        return all().toList();
    }
}
