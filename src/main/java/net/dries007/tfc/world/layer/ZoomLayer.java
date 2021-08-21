/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.Area;
import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.TransformLayer;

public enum ZoomLayer implements TransformLayer
{
    NORMAL
        {
            @Override
            public int choose(AreaContext context, int first, int second, int third, int fourth)
            {
                if (first == second)
                {
                    return first == third || third != fourth ? first : context.choose(first, third);
                }
                else if (first == third)
                {
                    return second != fourth ? first : context.choose(first, second);
                }
                else if (first == fourth)
                {
                    return second != third ? first : context.choose(first, second);
                }
                else if (second == third || second == fourth)
                {
                    return second;
                }
                else if (third == fourth)
                {
                    return third;
                }
                return context.choose(first, second, third, fourth);
            }
        },
    FUZZY
        {
            @Override
            public int choose(AreaContext context, int first, int second, int third, int fourth)
            {
                // Random
                return context.choose(first, second, third, fourth);
            }
        };

    @Override
    public int apply(AreaContext context, Area area, int x, int z)
    {
        final int parentX = x >> 1, parentZ = z >> 1;
        final int offsetX = x & 1, offsetZ = z & 1;
        final int northWest = area.get(parentX, parentZ);

        context.setSeed(parentX, parentZ);
        if (offsetX == 0 && offsetZ == 0)
        {
            return northWest;
        }
        else if (offsetX == 0)
        {
            return context.choose(northWest, area.get(parentX, parentZ + 1));
        }
        else if (offsetZ == 0)
        {
            return context.choose(northWest, area.get(parentX + 1, parentZ));
        }
        return choose(context, northWest, area.get(parentX, parentZ + 1), area.get(parentX + 1, parentZ), area.get(parentX + 1, parentZ + 1));
    }

    public abstract int choose(AreaContext context, int first, int second, int third, int fourth);
}
