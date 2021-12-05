/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.TypedArea;
import net.dries007.tfc.world.layer.framework.TypedTransformLayer;

public enum BiasedLandPlateZoomLayer implements TypedTransformLayer<Plate>
{
    INSTANCE;

    @Override
    public Plate apply(AreaContext context, TypedArea<Plate> area, int x, int z)
    {
        final int parentX = x >> 1, parentZ = z >> 1;
        final int offsetX = x & 1, offsetZ = z & 1;
        final Plate northWest = area.get(parentX, parentZ);

        context.setSeed(parentX, parentZ);
        if (offsetX == 0 && offsetZ == 0)
        {
            return northWest;
        }
        else if (offsetX == 0)
        {
            return choose(context, northWest, area.get(parentX, parentZ + 1));
        }
        else if (offsetZ == 0)
        {
            return choose(context, northWest, area.get(parentX + 1, parentZ));
        }
        else
        {
            return choose(context, northWest, area.get(parentX, parentZ + 1), area.get(parentX + 1, parentZ), area.get(parentX + 1, parentZ + 1));
        }
    }

    private Plate choose(AreaContext context, Plate first, Plate second)
    {
        if (first.oceanic())
        {
            return second;
        }
        if (second.oceanic())
        {
            return first;
        }
        return context.choose(first, second);
    }

    private Plate choose(AreaContext context, Plate first, Plate second, Plate third, Plate fourth)
    {
        Plate choice = null;
        int count = 0;
        if (!first.oceanic())
        {
            choice = first;
            count++;
        }
        if (!second.oceanic() && (choice == null || context.random().nextInt(1 + count) == 0))
        {
            choice = second;
            count++;
        }
        if (!third.oceanic() && (choice == null || context.random().nextInt(1 + count) == 0))
        {
            choice = third;
            count++;
        }
        if (!fourth.oceanic() && (choice == null || context.random().nextInt(1 + count) == 0))
        {
            choice = fourth;
        }
        if (choice != null)
        {
            return choice;
        }
        return context.choose(first, second, third, fourth); // Pick randomly. Because we don't care about ocean plates at this point.
    }
}
