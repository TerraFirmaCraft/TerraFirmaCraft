/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;

public record AlloyRange(Fluid fluid, double min, double max)
{
    public static final Codec<AlloyRange> CODEC = RecordCodecBuilder.create(i -> i.group(
        BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(c -> c.fluid),
        Codec.DOUBLE.fieldOf("min").forGetter(c -> c.min),
        Codec.DOUBLE.fieldOf("max").forGetter(c -> c.max)
    ).apply(i, AlloyRange::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlloyRange> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.registry(Registries.FLUID), c -> c.fluid,
        ByteBufCodecs.DOUBLE, c -> c.min,
        ByteBufCodecs.DOUBLE, c -> c.max,
        AlloyRange::new
    );

    /**
     * @return {@code true} if the value is within the range {@code [min, max]} within the tolerance of alloys
     */
    public boolean isIn(double value)
    {
        return min - FluidAlloy.EPSILON <= value && value <= max + FluidAlloy.EPSILON;
    }
}
