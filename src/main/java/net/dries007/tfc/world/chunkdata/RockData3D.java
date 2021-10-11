/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import net.minecraft.world.level.LevelHeightAccessor;

import net.dries007.tfc.world.settings.RockSettings;

// todo: implement, along with skewed rock layer boundaries
public class RockData3D
{
    public static final int WIDTH_BITS = 4;
    public static final int WIDTH = (1 << WIDTH_BITS);
    public static final int WIDTH_MASK = WIDTH - 1;

    private final RockSettings[] rocks;

    public RockData3D(LevelHeightAccessor level)
    {
        this.rocks = new RockSettings[WIDTH * WIDTH * level.getHeight()];
    }

    public RockSettings getRock(int x, int y, int z)
    {
        return rocks[(x & WIDTH_MASK) | ((z & WIDTH_MASK) << WIDTH_BITS) | (y << (WIDTH_BITS + WIDTH_BITS))];
    }
}
