/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.data;

import java.util.IdentityHashMap;
import java.util.Map;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Codecs;


/**
 * @param fluid The fluid corresponding to this fluid heat
 * @param meltTemperature The temperature at which this fluid melts
 * @param specificHeatCapacity The Specific Heat Capacity of the metal. Units of Energy / (°C * mB)
 */
public record FluidHeat(
    Fluid fluid,
    float meltTemperature,
    float specificHeatCapacity
) {
    public static final Codec<FluidHeat> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codecs.FLUID.fieldOf("fluid").forGetter(c -> c.fluid),
        Codec.FLOAT.fieldOf("melt_temperature").forGetter(c -> c.meltTemperature),
        Codec.FLOAT.fieldOf("specific_heat_capacity").forGetter(c -> c.specificHeatCapacity)
    ).apply(i, FluidHeat::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidHeat> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.registry(Registries.FLUID), c -> c.fluid,
        ByteBufCodecs.FLOAT, c -> c.meltTemperature,
        ByteBufCodecs.FLOAT, c -> c.specificHeatCapacity,
        FluidHeat::new
    );

    public static final ResourceLocation UNKNOWN_ID = Helpers.identifier("unknown");

    public static final DataManager<FluidHeat> MANAGER = new DataManager<>(Helpers.identifier("fluid_heat"), CODEC, STREAM_CODEC);
    private static final Map<Fluid, FluidHeat> BY_FLUID = new IdentityHashMap<>();

    /**
     * Reverse lookup for metals attached to fluids.
     * For the other direction, see {@link FluidHeat#fluid()}.
     *
     * @param fluid The fluid, can be empty.
     * @return A metal if it exists, and null if it doesn't.
     */
    @Nullable
    public static FluidHeat get(Fluid fluid)
    {
        return BY_FLUID.get(fluid);
    }

    public static FluidHeat getOrUnknown(FluidStack fluid)
    {
        return BY_FLUID.getOrDefault(fluid.getFluid(), unknown());
    }

    /**
     * Get the 'unknown' metal. This is the only metal that any assurances are made that it exists.
     */
    public static FluidHeat unknown()
    {
        return MANAGER.getOrThrow(UNKNOWN_ID);
    }

    public static void updateCache()
    {
        // Ensure 'unknown' metal exists
        unknown();

        // Reload fluid -> metal map
        BY_FLUID.clear();
        for (FluidHeat heat : MANAGER.getValues())
        {
            BY_FLUID.put(heat.fluid(), heat);
        }
    }

    /**
     * <strong>Not for general purpose use!</strong> Explicitly creates unregistered metals outside the system, which are able to act as rendering stubs.
     */
    public FluidHeat(Fluid fluid)
    {
        this(fluid, 0, 0);
    }

    /**
     * @return The Specific Heat Capacity of the metal. Units of Energy / °C
     * @see IHeat#getHeatCapacity()
     */
    public float heatCapacity(float mB)
    {
        return specificHeatCapacity() * mB;
    }
}