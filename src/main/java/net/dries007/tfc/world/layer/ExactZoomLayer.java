/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

public enum ExactZoomLayer implements IAreaTransformer1
{
    INSTANCE;

    @Override
    public int apply(IExtendedNoiseRandom<?> context, IArea area, int x, int z)
    {
        return area.getValue(getOffsetX(x), getOffsetZ(z));
    }

    @Override
    public int getOffsetX(int x)
    {
        return x >> 1;
    }

    @Override
    public int getOffsetZ(int z)
    {
        return z >> 1;
    }
}