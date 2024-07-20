/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.heat;

import java.util.Objects;
import java.util.function.Function;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.calendar.Calendars;

/**
 * The component for recording a heat value on an item.
 * @param holder The definition, which requires runtime knowledge of the stack we are attached to. It exposes interior mutability for a given stack
 * @param lastTemperature The last recorded temperature, at {@code lastTick}
 * @param lastTick The tick timestamp of the last recorded temperature
 */
public record HeatComponent(
    ParentHolder holder,
    float heatCapacity,
    float lastTemperature,
    long lastTick
)
{
    public static final Codec<HeatComponent> CODEC = RecordCodecBuilder.<HeatComponent>create(i -> i.group(
        Codec.FLOAT.optionalFieldOf("capacity", 0f).forGetter(c -> c.heatCapacity),
        Codec.FLOAT.optionalFieldOf("temperature", 0f).forGetter(c -> c.lastTemperature),
        Codec.LONG.optionalFieldOf("tick", 0L).forGetter(c -> c.lastTick)
    ).apply(i, HeatComponent::new)).xmap(Function.identity(), HeatComponent::sanitize);

    public static final StreamCodec<ByteBuf, HeatComponent> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT, c -> c.heatCapacity,
        ByteBufCodecs.FLOAT, c -> c.lastTemperature,
        ByteBufCodecs.VAR_LONG, c -> c.lastTick,
        HeatComponent::new
    );

    public static final HeatComponent EMPTY = new HeatComponent(ParentHolder.EMPTY, 0f, 0f, 0L);

    public HeatComponent(HeatDefinition parent)
    {
        this(new ParentHolder(parent), 0f, 0f, 0L);
    }

    HeatComponent(float heatCapacity, float lastTemperature, long lastTick)
    {
        this(new ParentHolder(null), heatCapacity, lastTemperature, lastTick);
    }

    public void capture(ItemStack stack)
    {
        if (holder.value == null)
        {
            holder.value = HeatCapability.getDefinition(stack);
            if (holder.value == null)
            {
                holder.value = HeatDefinition.DEFAULT;
            }
        }
    }

    float getTemperature()
    {
        return HeatCapability.adjustTemp(lastTemperature, getHeatCapacity(), Calendars.get().getTicks() - lastTick);
    }

    float getHeatCapacity()
    {
        return heatCapacity != 0f ? heatCapacity : parent().heatCapacity();
    }

    HeatComponent sanitize()
    {
        return holder.value != null && getTemperature() <= 0f
            ? heatCapacity != 0f
                ? new HeatComponent(ParentHolder.EMPTY, heatCapacity, 0f, 0L)
                : EMPTY
            : this;
    }

    HeatComponent with(float temperature, long tick)
    {
        return new HeatComponent(holder, heatCapacity, temperature, tick);
    }

    HeatDefinition parent()
    {
        return Objects.requireNonNull(holder.value);
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj == this || (obj instanceof HeatComponent that
            && lastTick == that.lastTick
            && heatCapacity == heatCapacity
            && lastTemperature == that.lastTemperature);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(heatCapacity, lastTemperature, lastTick);
    }

    @Override
    public String toString()
    {
        return "Heat[lastTemperature=%s,lastTick=%s,heatCapacity=%s,temperature=%s]".formatted(lastTemperature, lastTemperature, heatCapacity, getTemperature());
    }

    static class ParentHolder
    {
        static final ParentHolder EMPTY = new ParentHolder(null);

        @Nullable HeatDefinition value;

        ParentHolder(@Nullable HeatDefinition value)
        {
            this.value = value;
        }
    }
}
