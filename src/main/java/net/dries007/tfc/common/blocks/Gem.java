/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.dries007.tfc.common.blocks.rock.Ore;

public enum Gem
{
    AMETHYST,
    DIAMOND,
    EMERALD,
    LAPIS_LAZULI,
    OPAL,
    PYRITE,
    RUBY,
    SAPPHIRE,
    TOPAZ;

    public Ore ore()
    {
        return switch (this) {
            case AMETHYST -> Ore.AMETHYST;
            case DIAMOND -> Ore.DIAMOND;
            case EMERALD -> Ore.EMERALD;
            case LAPIS_LAZULI -> Ore.LAPIS_LAZULI;
            case OPAL -> Ore.OPAL;
            case PYRITE -> Ore.PYRITE;
            case RUBY -> Ore.RUBY;
            case SAPPHIRE -> Ore.SAPPHIRE;
            case TOPAZ -> Ore.TOPAZ;
        };
    }
}