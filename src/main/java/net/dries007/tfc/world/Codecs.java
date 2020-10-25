package net.dries007.tfc.world;

import java.util.*;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IWeighted;

/**
 * A collection of common codecs that reference vanilla code
 */
public final class Codecs
{
    public static final Codec<Integer> POSITIVE_INT = Codec.intRange(1, Integer.MAX_VALUE);
    public static final Codec<Integer> NONNEGATIVE_INT = Codec.intRange(0, Integer.MAX_VALUE);
    public static final Codec<Float> NONNEGATIVE_FLOAT = Codec.floatRange(0, Float.MAX_VALUE);

    /**
     * A block state which either will accept a simple block state name, or the more complex {"Name": "", "Properties": {}} declaration.
     * In the former case, the default state will be used.
     * When serializing, this will always use the right side, which serializes to the state based codec.
     */
    @SuppressWarnings("deprecation")
    public static final Codec<BlockState> LENIENT_BLOCKSTATE = Codec.either(
        nonDefaultedRegistryCodec(Registry.BLOCK).xmap(Block::defaultBlockState, BlockState::getBlock),
        BlockState.CODEC
    ).xmap(Helpers::resolveEither, Either::right);

    /**
     * Creates a codec for a given registry which does not default
     */
    public static <R> Codec<R> nonDefaultedRegistryCodec(Registry<R> registry)
    {
        return ResourceLocation.CODEC.flatXmap(
            id -> registry.getOptional(id).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown registry entry: " + id + " for registry: " + registry.key())),
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
        return IWeighted.codec(Codec.mapPair(elementCodec.fieldOf(elementKey), Codec.DOUBLE.optionalFieldOf("weight", 1d)).codec().listOf());
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
     * Like {@link Helpers#mapKeyListCodec(Codec)} but for a injective map k -> v
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

    /**
     * Creates a codec for a set for fast block lookups
     */
    public static <E> Codec<Set<E>> setCodec(Codec<E> elementCodec)
    {
        return elementCodec.listOf().xmap(HashSet::new, ArrayList::new);
    }
}
