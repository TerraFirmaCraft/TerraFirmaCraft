/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

public interface ITFCBiome
{
    default LargeGroup getLargeGroup()
    {
        return LargeGroup.LAND;
    }

    default SmallGroup getMediumGroup()
    {
        return SmallGroup.BODY;
    }

    enum LargeGroup
    {
        LAND, OCEAN, RIVER, LAKE;

        public static final int SIZE = LargeGroup.values().length;
    }

    enum SmallGroup
    {
        BODY, RIVER;

        public static final int SIZE = SmallGroup.values().length;
    }
}