/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import java.util.function.Supplier;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ClimateModelType<T extends ClimateModel>(
    Supplier<T> factory,
    StreamCodec<ByteBuf, T> codec
) {
    public T create()
    {
        return factory.get();
    }
}
