/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.material.Fluid;

import net.dries007.tfc.util.StrictOptionalCodec;
import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.util.collections.Weighted;

/**
 * A collection of common codecs that reference vanilla code. Extends {@link ExtraCodecs} for access of codecs there all through {@link Codecs}
 */
public final class Codecs extends ExtraCodecs
{
    public static final Codec<Float> UNIT_FLOAT = Codec.floatRange(0, 1);

    @SuppressWarnings("deprecation") public static final Codec<Block> BLOCK = nonDefaultedRegistryCodec(BuiltInRegistries.BLOCK);
    @SuppressWarnings("deprecation") public static final Codec<Fluid> FLUID = nonDefaultedRegistryCodec(BuiltInRegistries.FLUID);

    /**
     * A block state which either will accept a simple block state name, or the more complex {"Name": "", "Properties": {}} declaration.
     * In the former case, the default state will be used.
     * When serializing, this will always use the right side, which serializes to the state based codec.
     */
    public static final Codec<BlockState> BLOCK_STATE = Codec.either(
        BLOCK.xmap(Block::defaultBlockState, BlockState::getBlock),
        BlockState.CODEC
    ).xmap(e -> e.map(e1 -> e1, e1 -> e1), e -> e == e.getBlock().defaultBlockState() ? Either.left(e) : Either.right(e));

    /**
     * A codec for a mapping from blocks -> {weighted block states}.
     * Represented as an internal codec of replace, with, and block keys
     */
    public static final Codec<Map<Block, IWeighted<BlockState>>> BLOCK_TO_WEIGHTED_BLOCKSTATE = Codecs.mapKeyListCodec(Codec.mapPair(
        BLOCK.listOf().fieldOf("replace"),
        Codecs.weightedCodec(Codecs.BLOCK_STATE, "block").fieldOf("with")
    ).codec());

    public static final Codec<BlockStateConfiguration> BLOCK_STATE_CONFIG = BLOCK_STATE.fieldOf("state").xmap(BlockStateConfiguration::new, c -> c.state).codec();

    /**
     * Creates a codec for a given registry which does not default
     */
    public static <R> Codec<R> nonDefaultedRegistryCodec(Registry<R> registry)
    {
        return ResourceLocation.CODEC.flatXmap(
            id -> registry.getOptional(id).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown registry entry: " + id + " for registry: " + registry.key())),
            value -> DataResult.success(registry.getKey(value))
        );
    }

    /**
     * Creates a codec for a pair, expressed as a record with two keys
     */
    public static <F, S> Codec<Pair<F, S>> recordPairCodec(Codec<F> first, String firstKey, Codec<S> second, String secondKey)
    {
        return Codec.mapPair(first.fieldOf(firstKey), second.fieldOf(secondKey)).codec();
    }

    /**
     * Creates a codec for an optimized weighted list, using {@link IWeighted}. The representation is a list of elements with a weight and an element key.
     */
    public static <E> Codec<IWeighted<E>> weightedCodec(Codec<E> elementCodec, String elementKey)
    {
        return Codec.mapPair(
            elementCodec.fieldOf(elementKey),
            Codec.DOUBLE.optionalFieldOf("weight", 1d)
        ).codec().listOf().xmap((list -> {
            if (list.isEmpty())
            {
                return IWeighted.empty();
            }
            else if (list.size() == 1)
            {
                return IWeighted.singleton(list.get(0).getFirst());
            }
            else
            {
                return new Weighted<E>(list);
            }
        }), IWeighted::weightedValues);
    }

    /**
     * Creates a codec for a optimized map from k -> v, from a representation of the inverse mapping as a v -> {k}, represented as a list of elements, each with a list of keys, and a singular value.
     *
     * @param codec A codec for each element with a list of keys and value.
     */
    public static <K, V> Codec<Map<K, V>> mapKeyListCodec(Codec<Pair<List<K>, V>> codec)
    {
        return codec.listOf().xmap(list -> {
            Map<K, V> map = new HashMap<>();
            for (Pair<List<K>, V> pair : list)
            {
                for (K key : pair.getFirst())
                {
                    map.put(key, pair.getSecond());
                }
            }
            return map;
        }, map -> {
            Map<V, List<K>> inverseMap = new HashMap<>();
            for (Map.Entry<K, V> entry : map.entrySet())
            {
                inverseMap.computeIfAbsent(entry.getValue(), v -> new ArrayList<>()).add(entry.getKey());
            }
            return inverseMap.entrySet().stream().map(e -> Pair.of(e.getValue(), e.getKey())).collect(Collectors.toList());
        });
    }

    /**
     * Like {@link Codecs#mapKeyListCodec(Codec)}} but for a injective map k -> v
     *
     * @param codec A codec for each key, value element.
     */
    public static <K, V> Codec<Map<K, V>> mapListCodec(Codec<Pair<K, V>> codec)
    {
        return codec.listOf().xmap(
            list -> list.stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)),
            map -> map.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue())).collect(Collectors.toList())
        );
    }

    public static <T> Codec<T> presetIdOrDirectCodec(Codec<T> directCodec, Map<ResourceLocation, T> presets)
    {
        return Codec.either(ResourceLocation.CODEC, directCodec).comapFlatMap(
            e -> e.map(
                id -> {
                    final T element = presets.get(id);
                    return element == null ? DataResult.error(() -> "No element with id: " + id) : DataResult.success(element);
                },
                DataResult::success
            ),
            Either::right
        );
    }

    /**
     * Variant of {@link Codec#optionalFieldOf(String)} which will error if the field is present but invalid.
     * <ul>
     *     <li>For simple integer valued or other codecs, vanilla's optional is generally fine, but prefer using non-optional codecs where possible, as optional codecs still help to obscure errors i.e. if the field is misnamed in JSON.</li>
     *     <li>For more complex fields, prefer using this over the default method as it helps detect errors</li>
     * </ul>
     */
    public static <T> StrictOptionalCodec<T> optionalFieldOf(Codec<T> codec, String field)
    {
        return new StrictOptionalCodec<>(field, codec);
    }

    public static <T> MapCodec<T> optionalFieldOf(Codec<T> codec, String field, T defaultValue)
    {
        return optionalFieldOf(codec, field).xmap(
            o -> o.orElse(defaultValue),
            a -> Objects.equals(a, defaultValue) ? Optional.empty() : Optional.of(a)
        );
    }
}
