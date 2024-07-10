/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ClimateModelType(
    ResourceLocation id,
    Supplier<ClimateModel> factory,
    StreamCodec<ByteBuf, ClimateModel> codec
) {
    public ClimateModel create()
    {
        return factory.get();
    }
}
