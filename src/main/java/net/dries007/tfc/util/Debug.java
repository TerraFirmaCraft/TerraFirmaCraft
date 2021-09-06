/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * A collection of debug-related features and conditions
 */
public final class Debug
{
    /**
     * Enable various debug features.
     * By default, this is only enabled when running in IDE.
     */
    public static final boolean DEBUG = !FMLEnvironment.production;

    /* Toggle to only generate biomes with normal/normal climates. This can assist when debugging specific biomes, as /locatebiome works much more readily. */
    public static final boolean ONLY_NORMAL_NORMAL_CLIMATES = false;

    /* Cover the world in a visualization of the slope, which is used to seed surface depth. */
    public static final boolean ENABLE_SLOPE_VISUALIZATION = false;

    /* Only generate a single biome in the world */
    public static final boolean SINGLE_BIOME = false;

    /* Generate biomes in stripes, showing all biomes */
    public static final boolean STRIPE_BIOMES = false;
}
