/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.AreaFactory;
import net.dries007.tfc.world.layer.framework.TypedArea;
import net.dries007.tfc.world.layer.framework.TypedAreaFactory;

import static net.dries007.tfc.world.layer.TFCLayerUtil.*;

public enum PlateBoundaryLayer
{
    INSTANCE;

    public static final float SHEAR_THRESHOLD = 0.9f;

    public static final float HIGH_ELEVATION = 0.66f;
    public static final float MID_ELEVATION = 0.33f;

    public AreaFactory run(AreaContext context, TypedAreaFactory<Plate> plateLayer)
    {
        return () -> {
            final TypedArea<Plate> area = plateLayer.get();
            return context.createArea((x, z) -> {
                context.initSeed(x, z);
                return apply(context, area, x, z);
            });
        };
    }

    private int apply(AreaContext context, TypedArea<Plate> area, int x, int z)
    {
        return apply(context, area.get(x, z - 1), area.get(x + 1, z), area.get(x, z + 1), area.get(x - 1, z), area.get(x, z));
    }

    private int apply(AreaContext context, Plate north, Plate west, Plate south, Plate east, Plate center)
    {
        Plate boundary = null;
        int boundaryCount = 0;
        if (!north.equals(center))
        {
            boundaryCount++;
            boundary = north;
        }
        if (!west.equals(center))
        {
            boundaryCount++;
            if (boundary == null || context.nextInt(boundaryCount) == 0)
            {
                boundary = west;
            }
        }
        if (!south.equals(center))
        {
            boundaryCount++;
            if (boundary != null || context.nextInt(boundaryCount) == 0)
            {
                boundary = south;
            }
        }
        if (!east.equals(center))
        {
            boundaryCount++;
            if (boundary != null || context.nextInt(boundaryCount) == 0)
            {
                boundary = east;
            }
        }
        if (boundary != null)
        {
            return boundary(center, boundary);
        }
        return plate(center);
    }

    private int boundary(Plate center, Plate other)
    {
        float distX = center.x() - other.x(), distZ = center.z() - other.z();
        float vX = center.driftX() - other.driftX(), vZ = center.driftZ() - other.driftZ();
        float delta = distX * vX + distZ * vZ;
        if (delta > SHEAR_THRESHOLD)
        {
            // Converging
            if (center.oceanic() && other.oceanic())
            {
                return center.elevation() > other.elevation() ? OCEAN_OCEAN_CONVERGING_UPPER : OCEAN_OCEAN_CONVERGING_LOWER;
            }
            else if (center.oceanic())
            {
                return OCEAN_CONTINENT_CONVERGING_LOWER;
            }
            else if (other.oceanic())
            {
                return OCEAN_CONTINENT_CONVERGING_UPPER;
            }
            return CONTINENT_CONTINENT_CONVERGING;
        }
        else if (delta < -SHEAR_THRESHOLD)
        {
            // Diverging
            if (center.oceanic() && other.oceanic())
            {
                return OCEAN_OCEAN_DIVERGING;
            }
            else if (center.oceanic() || other.oceanic())
            {
                return OCEAN_CONTINENT_DIVERGING;
            }
            return CONTINENT_CONTINENT_DIVERGING;
        }
        return plate(center);
    }

    private int plate(Plate center)
    {
        if (center.oceanic())
        {
            return OCEANIC;
        }
        if (center.elevation() > HIGH_ELEVATION)
        {
            return CONTINENTAL_HIGH;
        }
        if (center.elevation() > MID_ELEVATION)
        {
            return CONTINENTAL_MID;
        }
        return CONTINENTAL_LOW;
    }
}
