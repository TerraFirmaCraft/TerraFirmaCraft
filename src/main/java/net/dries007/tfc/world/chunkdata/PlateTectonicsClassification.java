/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;

import net.dries007.tfc.world.layer.TFCLayers;

/**
 * Values for plate tectonics
 *
 * These must match the compile time constants in {@link TFCLayers}
 */
public enum PlateTectonicsClassification implements StringRepresentable
{
    OCEANIC,
    CONTINENTAL_LOW,
    CONTINENTAL_MID,
    CONTINENTAL_HIGH,
    OCEAN_OCEAN_DIVERGING,
    OCEAN_OCEAN_CONVERGING,
    OCEAN_CONTINENT_DIVERGING,
    OCEAN_CONTINENT_CONVERGING,
    CONTINENT_CONTINENT_DIVERGING,
    CONTINENT_CONTINENT_CONVERGING;

    private static final PlateTectonicsClassification[] VALUES = values();

    public static PlateTectonicsClassification valueOf(int i)
    {
        return i >= 0 && i < VALUES.length ? VALUES[i] : OCEANIC;
    }

    private final String serializedName;

    PlateTectonicsClassification()
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }
}
