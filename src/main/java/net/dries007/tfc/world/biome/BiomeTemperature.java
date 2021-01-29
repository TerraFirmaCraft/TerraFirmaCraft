/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

/**
 * These are biome temperatures. They mimic the vanilla ocean variants, except we apply them to all biomes, based on our temperature layer generation.
 */
public enum BiomeTemperature
{
    FROZEN,
    COLD,
    NORMAL,
    LUKEWARM,
    WARM
}