/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Optional;
import java.util.stream.Stream;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

/**
 * Optional field that will error if the field is present, but invalid, rather than silently accepting.
 * <p>
 * Borrowed from Cyanide, licensed under MIT, original author explicit permission blah blah I AM the original author heck.
 * todo: 1.21 remove since this got incorporated into DFU
 */
public final class StrictOptionalCodec<A> extends MapCodec<Optional<A>>
{
    private final String name;
    private final Codec<A> elementCodec;

    public StrictOptionalCodec(String name, Codec<A> elementCodec)
    {
        this.name = name;
        this.elementCodec = elementCodec;
    }

    @Override
    public <T> DataResult<Optional<A>> decode(DynamicOps<T> ops, MapLike<T> input)
    {
        final T value = input.get(name);
        if (value != null)
        {
            return elementCodec.parse(ops, value)
                .map(Optional::of)
                .mapError(e -> "Optional field \"" + name + "\" was invalid: " + e);
        }
        return DataResult.success(Optional.empty());
    }

    @Override
    public <T> RecordBuilder<T> encode(Optional<A> input, DynamicOps<T> ops, RecordBuilder<T> prefix)
    {
        if (input.isPresent())
        {
            return prefix.add(name, elementCodec.encodeStart(ops, input.get()));
        }
        return prefix;
    }

    @Override
    public <T> Stream<T> keys(final DynamicOps<T> ops)
    {
        return Stream.of(ops.createString(name));
    }
}
