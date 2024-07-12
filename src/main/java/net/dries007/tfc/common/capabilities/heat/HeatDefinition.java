/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.heat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * This is a definition (reloaded via {@link HeatCapability}) of a heat that is applied to an item stack.
 */
public record HeatDefinition(
    Ingredient ingredient,
    float heatCapacity,
    float forgingTemperature,
    float weldingTemperature
)
{
    public static final Codec<HeatDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        Codec.FLOAT.fieldOf("heat_capacity").forGetter(c -> c.heatCapacity),
        Codec.FLOAT.optionalFieldOf("forging_temperature", 0f).forGetter(c -> c.forgingTemperature),
        Codec.FLOAT.optionalFieldOf("welding_temperature", 0f).forGetter(c -> c.weldingTemperature)
    ).apply(i, HeatDefinition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, HeatDefinition> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.ingredient,
        ByteBufCodecs.FLOAT, c -> c.heatCapacity,
        ByteBufCodecs.FLOAT, c -> c.forgingTemperature,
        ByteBufCodecs.FLOAT, c -> c.weldingTemperature,
        HeatDefinition::new
    );

    public static final HeatDefinition DEFAULT = new HeatDefinition(Ingredient.EMPTY, 0f, 0f, 0f);
}
