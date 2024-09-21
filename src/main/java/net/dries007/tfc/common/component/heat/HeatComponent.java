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


public final class HeatComponent implements IHeatView
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

    public static final HeatComponent EMPTY = new HeatComponent(null, 0f, 0f, 0L);

    /**
     * Create a new {@link HeatComponent} from a custom heat capacity value. The component will have no definition applied,
     * and should not be overwritten by external values.
     * @param heatCapacity The heat capacity
     */
    public static HeatComponent of(float heatCapacity)
    {
        return new HeatComponent(null, heatCapacity, 0f, 0L);
    }

    /**
     * Create a new {@link HeatComponent} from a custom heat capacity value. The component will have no definition applied,
     * and should not be overwritten by external values.
     * @param heatCapacity The heat capacity
     * @param temperatureNow The current temperature
     */
    public static HeatComponent of(float heatCapacity, float temperatureNow)
    {
        return new HeatComponent(null, heatCapacity, temperatureNow, Calendars.get().getTicks());
    }

    /**
     * Creates a new {@link HeatComponent} from a {@link HeatDefinition}. This should only be used to create default values
     * on creation of item stacks.
     * @param parent The definition used for this item
     */
    public static HeatComponent of(HeatDefinition parent)
    {
        return new HeatComponent(parent, 0f, 0f, 0L);
    }

    private @Nullable HeatDefinition parent;
    private final float heatCapacity;
    private float lastTemperature;
    private long lastTick;

    HeatComponent(float heatCapacity, float lastTemperature, long lastTick)
    {
        this(null, heatCapacity, lastTemperature, lastTick);
    }

    /**
     * @param parent The definition, which requires runtime knowledge of the unsealedStack we are attached to. It exposes interior mutability for a given unsealedStack
     * @param heatCapacity The custom heat capacity of this item, typically set by an external device or capability
     * @param lastTemperature The last recorded temperature, at {@code lastTick}
     * @param lastTick The tick timestamp of the last recorded temperature
     */
    HeatComponent(@Nullable HeatDefinition parent, float heatCapacity, float lastTemperature, long lastTick)
    {
        this.parent = parent;
        this.heatCapacity = heatCapacity;
        this.lastTemperature = lastTemperature;
        this.lastTick = lastTick;
    }

    public void capture(ItemStack stack)
    {
        if (parent == null)
        {
            parent = HeatCapability.getDefinition(stack);
            if (parent == null)
            {
                parent = HeatDefinition.DEFAULT;
            }
        }
    }

    /**
     * @return The current temperature, or an estimation of it
     */
    @Override
    public float getTemperature()
    {
        return sanitize().calculateTemperature();
    }

    private float calculateTemperature()
    {
        return HeatCapability.adjustTemp(lastTemperature, getHeatCapacity(), Calendars.get().getTicks() - lastTick);
    }

    /**
     * @return The current heat capacity, or an estimation of it
     */
    @Override
    public float getHeatCapacity()
    {
        return heatCapacity != 0f ? heatCapacity : parent == null ? Float.POSITIVE_INFINITY : parent.heatCapacity();
    }

    @Override
    public float getWorkingTemperature()
    {
        return parent == null ? 0f : parent.forgingTemperature();
    }

    @Override
    public float getWeldingTemperature()
    {
        return parent == null ? 0f : parent.weldingTemperature();
    }

    /**
     * Sanitizes this heat component, overwriting interior-mutable values with the most up-to-date values. This should be called upon any access
     * to the interior values of {@link #lastTemperature} and {@link #lastTick} as they may have reset since being updated, or are equal despite
     * being separate values.
     * <p>
     * Note that if we do not have a heat capacity - i.e. we don't have either a parent or a custom heat capacity, we refrain from updating any
     * values for the current temperature, as they may be invalid.
     *
     * @return This heat component
     */
    HeatComponent sanitize()
    {
        if ((parent != null || heatCapacity != 0f) && (lastTemperature != 0f || lastTick != 0L))
        {
            lastTemperature = Math.max(0f, calculateTemperature());
            lastTick = lastTemperature <= 0f ? 0L : Calendars.get().getTicks();
        }
        return this;
    }

    HeatComponent with(float temperature, long tick)
    {
        return new HeatComponent(parent, heatCapacity, temperature, tick);
    }

    HeatComponent withHeatCapacity(float heatCapacity)
    {
        // Calling getTemperature() and getTicks() perform sanitization effectively, before updating the heat capacity
        return new HeatComponent(parent, heatCapacity, getTemperature(), Calendars.get().getTicks());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj instanceof HeatComponent that)
        {
            // Sanitize before comparing directly
            this.sanitize();
            that.sanitize();
            return heatCapacity == that.heatCapacity
                && lastTick == that.lastTick
                && lastTemperature == that.lastTemperature;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        // Don't hash temperature or tick, as those are transient values and may change
        // The only values we can accurately hash are the heat capacity (final).
        // Everything else exposes interior mutability! Luckily, these don't need to be hashed often, because this is 99% useless
        return Objects.hash(heatCapacity);
    }

    @Override
    public String toString()
    {
        return "Heat[parent=%s%s,lastTick=%s,lastTemperature=%5.0f,temperature=%5.0f]".formatted(
            parent != null ? Objects.requireNonNullElse(HeatCapability.MANAGER.getId(parent), "<custom>") : "<null>",
            heatCapacity == 0f ? "" : ",heatCapacity=" + heatCapacity,
            lastTick == Calendars.get().getTicks() ? "<now>" : "%8d".formatted(lastTick),
            lastTemperature,
            getTemperature());
    }
}
