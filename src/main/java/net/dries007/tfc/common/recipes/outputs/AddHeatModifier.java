/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;

public record AddHeatModifier(float temperature) implements ItemStackModifier
{
    public static final MapCodec<AddHeatModifier> CODEC = Codec.FLOAT.fieldOf("temperature").xmap(AddHeatModifier::new, AddHeatModifier::temperature);
    public static final StreamCodec<RegistryFriendlyByteBuf, AddHeatModifier> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT, c -> c.temperature,
        AddHeatModifier::new
    );

    public static AddHeatModifier of(float temperature)
    {
        return new AddHeatModifier(temperature);
    }

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input, Context context)
    {
        final @Nullable IHeat heat = HeatCapability.get(stack);
        if (heat != null)
        {
            heat.setTemperature(heat.getTemperature() + temperature);
        }
        return stack;
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return ItemStackModifiers.ADD_HEAT.get();
    }
}
