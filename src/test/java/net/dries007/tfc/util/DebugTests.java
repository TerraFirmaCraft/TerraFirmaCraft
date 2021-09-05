package net.dries007.tfc.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class DebugTests
{
    // Debug settings should not be enabled!
    @Test
    public void testDebugOptionsAreDisabledInProduction()
    {
        assertFalse(Debug.ONLY_NORMAL_NORMAL_CLIMATES, "ONLY_NORMAL_NORMAL_CLIMATES");
        assertFalse(Debug.ENABLE_SLOPE_VISUALIZATION, "ENABLE_SLOPE_VISUALIZATION");
        assertFalse(Debug.SINGLE_BIOME, "SINGLE_BIOME");
        assertFalse(Debug.STRIPE_BIOMES, "STRIPE_BIOMES");
    }
}
