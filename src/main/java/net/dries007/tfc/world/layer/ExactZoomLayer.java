/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

public enum ExactZoomLayer implements IAreaTransformer1
{
    INSTANCE;

    @Override
    public int applyPixel(IExtendedNoiseRandom<?> context, IArea area, int x, int z)
    {
        return area.get(getParentX(x), getParentY(z));
    }

    @Override
    public int getParentX(int x)
    {
        return x >> 1;
    }

    @Override
    public int getParentY(int z)
    {
        return z >> 1;
    }
}