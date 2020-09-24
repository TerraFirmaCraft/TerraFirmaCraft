/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.types;

public class Ore
{
    /**
     * Default ores used for block registration calls
     * Not extensible
     *
     * todo: re-evaluate if there is any data driven behavior that needs a json driven ore
     *
     * @see Ore instead and register via json
     */
    public enum Default
    {
        NATIVE_COPPER(true, false),
        NATIVE_GOLD(true, false),
        HEMATITE(true, false),
        NATIVE_SILVER(true, false),
        CASSITERITE(true, false),
        BISMUTHINITE(true, false),
        GARNIERITE(true, false),
        MALACHITE(true, false),
        MAGNETITE(true, false),
        LIMONITE(true, false),
        SPHALERITE(true, false),
        TETRAHEDRITE(true, false),
        BITUMINOUS_COAL(false, false),
        LIGNITE(false, false),
        KAOLINITE(false, false),
        GYPSUM(false, false),
        GRAPHITE(false, false),
        SULFUR(false, false),
        CINNABAR(false, false),
        CRYOLITE(false, false),
        SALTPETER(false, false),
        SYLVITE(false, false),
        BORAX(false, false),
        HALITE(false, false),
        // gem ores
        AMETHYST(false, true),
        DIAMOND(false, true),
        EMERALD(false, true),
        LAPIS_LAZULI(false, true),
        OPAL(false, true),
        PYRITE(false, true),
        RUBY(false, true),
        SAPPHIRE(false, true),
        TOPAZ(false, true);

        private final boolean graded;
        private final boolean gem;

        Default(boolean graded, boolean gem)
        {
            this.graded = graded;
            this.gem = gem;
        }

        public boolean isGraded()
        {
            return graded;
        }
        public boolean isGem() { return gem; }
        public boolean isNotGem() { return !gem; }
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
<<<<<<< HEAD
}
=======

    public enum ItemGrade
    {
        SMALL, POOR, RICH;

        private static final ItemGrade[] VALUES = values();

        public static ItemGrade valueOf(int i)
        {
            return i < 0 || i >= VALUES.length ? SMALL : VALUES[i];
        }
    }
}
>>>>>>> 1.15.x
