/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.types;

/**
 * Default ores used for block registration calls
 */
public enum Ore
{
    NATIVE_COPPER(true),
    NATIVE_GOLD(true),
    HEMATITE(true),
    NATIVE_SILVER(true),
    CASSITERITE(true),
    BISMUTHINITE(true),
    GARNIERITE(true),
    MALACHITE(true),
    MAGNETITE(true),
    LIMONITE(true),
    SPHALERITE(true),
    TETRAHEDRITE(true),
    BITUMINOUS_COAL(false),
    LIGNITE(false),
    KAOLINITE(false),
    GYPSUM(false),
    GRAPHITE(false),
    SULFUR(false),
    CINNABAR(false),
    CRYOLITE(false),
    SALTPETER(false),
    SYLVITE(false),
    BORAX(false),
    HALITE(false),
    AMETHYST(false),
    DIAMOND(false),
    EMERALD(false),
    LAPIS_LAZULI(false),
    OPAL(false),
    PYRITE(false),
    RUBY(false),
    SAPPHIRE(false),
    TOPAZ(false);

    private final boolean graded;

    Ore(boolean graded)
    {
        this.graded = graded;
    }

    public boolean isGraded()
    {
        return graded;
    }

    public enum Grade
    {
        NORMAL, POOR, RICH;

        private static final Grade[] VALUES = values();

        public static Grade valueOf(int i)
        {
            return i < 0 || i >= VALUES.length ? NORMAL : VALUES[i];
        }
    }
}
