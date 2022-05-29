/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.TypedArea;
import net.dries007.tfc.world.layer.framework.TypedTransformLayer;

public abstract class TypedZoomLayer<A> implements TypedTransformLayer<A>
{
    @Override
    public A apply(AreaContext context, TypedArea<A> area, int x, int z)
    {
        final int parentX = x >> 1, parentZ = z >> 1;
        final int offsetX = x & 1, offsetZ = z & 1;
        final A northWest = area.get(parentX, parentZ);

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
        else
        {
            return choose(context, northWest, area.get(parentX, parentZ + 1), area.get(parentX + 1, parentZ), area.get(parentX + 1, parentZ + 1));
        }
    }

    public abstract A choose(AreaContext context, A first, A second, A third, A fourth);

    public static class Normal<A> extends TypedZoomLayer<A>
    {
        @Override
        public A choose(AreaContext context, A first, A second, A third, A fourth)
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
    }

    public static class Fuzzy<A> extends TypedZoomLayer<A>
    {
        @Override
        public A choose(AreaContext context, A first, A second, A third, A fourth)
        {
            return context.choose(first, second, third, fourth);
        }
    }
}
