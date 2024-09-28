/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;


import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import net.dries007.tfc.common.Lore;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.tooltip.Tooltips;
import net.dries007.tfc.world.Codecs;

public record HeatIngredient(float min, float max) implements PreciseIngredient
{
    private static final float MIN = Float.NEGATIVE_INFINITY;
    private static final float MAX = Float.POSITIVE_INFINITY;

    public static final MapCodec<HeatIngredient> CODEC = RecordCodecBuilder.<HeatIngredient>mapCodec(i -> i.group(
        Codecs.POSITIVE_FLOAT.optionalFieldOf("min", MIN).forGetter(c -> c.min),
        Codecs.POSITIVE_FLOAT.optionalFieldOf("max", MAX).forGetter(c -> c.max)
    ).apply(i, HeatIngredient::new)).flatXmap(
        c -> c.min == MIN && c.max == MAX ? DataResult.error(() -> "Must have one of min or max") : DataResult.success(c),
        DataResult::success
    );

    public static final StreamCodec<ByteBuf, HeatIngredient> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT, c -> c.min,
        ByteBufCodecs.FLOAT, c -> c.max,
        HeatIngredient::new
    );

    public static Ingredient min(float min)
    {
        return new HeatIngredient(min, MAX).toVanilla();
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
        HeatCapability.setStaticTemperature(stack, min != MIN ? min : max);
        Lore.append(stack, Tooltips.require(
            min == MIN ? null : TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(min),
            max == MAX ? null : TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(max)
        ));
        return stack;
    }

    @Override
    public IngredientType<?> getType()
    {
        return TFCIngredients.HEAT.get();
    }
}
