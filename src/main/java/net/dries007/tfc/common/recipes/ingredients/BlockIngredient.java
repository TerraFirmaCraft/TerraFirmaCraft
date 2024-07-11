/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Codecs;

/**
 * An ingredient that can express either blocks, or tags of blocks, in a format similar to other ingredient types. This does not bother exposing
 * the degree of complexity as the NeoForge provided fluid ingredients / sized ingredients, as it's pretty unnecessary for our purposes.
 */
public record BlockIngredient(List<Entry> entries) implements Predicate<Block>
{
    private static final Codec<Entry> ENTRY_CODEC = NeoForgeExtraCodecs.xor(
        Codecs.BLOCK.fieldOf("block").xmap(BlockEntry::new, BlockEntry::block),
        TagKey.codec(Registries.BLOCK).fieldOf("tag").xmap(TagEntry::new, TagEntry::tag)
    ).codec().xmap(
        e -> e.map(l -> l, r -> r),
        e -> e instanceof BlockEntry l ? Either.left(l) : Either.right((TagEntry) e)
    );

    public static final Codec<BlockIngredient> CODEC = Codec.either(ENTRY_CODEC, ENTRY_CODEC.listOf()).xmap(
        e -> new BlockIngredient(e.map(List::of, r -> r)),
        e -> e.entries.size() == 1 ? Either.left(e.entries.getFirst()) : Either.right(e.entries)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockIngredient> STREAM_CODEC = ByteBufCodecs.registry(Registries.BLOCK)
        .apply(ByteBufCodecs.list())
        .map(e -> new BlockIngredient(e.stream().<Entry>map(BlockEntry::new).toList()), BlockIngredient::blocks);

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
        for (Entry entry : entries())
        {
            if (entry.test(block))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @return A stream of all the individual objects that may match this ingredient.
     */
    public Stream<Block> all()
    {
        return entries().stream().flatMap(Entry::stream);
    }

    public List<Block> blocks()
    {
        return all().toList();
    }

    sealed interface Entry
        extends Predicate<Block>
        permits BlockEntry, TagEntry
    {
        Stream<Block> stream();
    }

    record BlockEntry(Block block) implements Entry
    {
        @Override
        public boolean test(Block object)
        {
            return this.block == object;
        }

        @Override
        public Stream<Block> stream()
        {
            return Stream.of(block);
        }
    }

    record TagEntry(TagKey<Block> tag) implements Entry
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
