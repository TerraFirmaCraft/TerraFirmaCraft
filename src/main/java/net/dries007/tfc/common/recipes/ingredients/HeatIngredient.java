/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;

public record HeatIngredient(float min, float max) implements PreciseIngredient
{
    public static final MapCodec<HeatIngredient> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Codec.FLOAT.optionalFieldOf("min", Float.MIN_VALUE).forGetter(c -> c.min),
        Codec.FLOAT.optionalFieldOf("max", Float.MAX_VALUE).forGetter(c -> c.max)
    ).apply(i, HeatIngredient::new));

    public static final StreamCodec<ByteBuf, HeatIngredient> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT, c -> c.min,
        ByteBufCodecs.FLOAT, c -> c.max,
        HeatIngredient::new
    );

    public static Ingredient min(float min)
    {
        return new HeatIngredient(min, Float.MAX_VALUE).toVanilla();
    }

    @Override
    public boolean test(ItemStack stack)
    {
        final float temperature = HeatCapability.getTemperature(stack);
        return temperature >= min && temperature <= max;
    }

    @Override
    public ItemStack modifyStackForDisplay(ItemStack stack)
    {
        HeatCapability.setTemperature(stack, min);
        return stack;
    }

    @Override
    public IngredientType<?> getType()
    {
        return TFCIngredients.HEAT.get();
    }
}
