/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import java.util.Locale;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.network.StreamCodecs;

public record FoodDefinition(
    Ingredient ingredient,
    FoodData food,
    HandlerType type
)
{
    public static final Codec<FoodDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        FoodData.MAP_CODEC.forGetter(c -> c.food),
        StringRepresentable.fromValues(HandlerType::values).fieldOf("type").forGetter(c -> c.type)
    ).apply(i, FoodDefinition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FoodDefinition> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.ingredient,
        FoodData.STREAM_CODEC, c -> c.food,
        StreamCodecs.forEnum(HandlerType::values), c -> c.type,
        FoodDefinition::new
    );

    enum HandlerType implements StringRepresentable
    {
        STATIC,
        DYNAMIC,
        DYNAMIC_BOWL;

        final String serializedName;

        HandlerType()
        {
            this.serializedName = name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String getSerializedName()
        {
            return serializedName;
        }
    }
}
