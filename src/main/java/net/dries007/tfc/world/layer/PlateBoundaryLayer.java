/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.traits.IDimOffset1Transformer;

import net.dries007.tfc.world.layer.traits.ITypedAreaFactory;
import net.dries007.tfc.world.layer.traits.LazyTypedArea;

import static net.dries007.tfc.world.layer.TFCLayerUtil.*;

public enum PlateBoundaryLayer implements IDimOffset1Transformer
{
    INSTANCE;

    public static final float SHEAR_THRESHOLD = 0.7f;

    public static final float HIGH_ELEVATION = 0.66f;
    public static final float MID_ELEVATION = 0.33f;

    public IAreaFactory<LazyArea> run(IExtendedNoiseRandom<LazyArea> context, ITypedAreaFactory<Plate> plateLayer)
    {
        return () -> {
            LazyTypedArea<Plate> area = plateLayer.make();
            return context.createResult((x, z) -> {
                context.initRandom(x, z);
                return apply(context, area, x, z);
            }, area.asLazyArea());
        };
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private int apply(IExtendedNoiseRandom<?> context, LazyTypedArea<Plate> area, int x, int z)
    {
        return apply(context,
            area.get(getParentX(x + 1), getParentY(z + 0)),
            area.get(getParentX(x + 2), getParentY(z + 1)),
            area.get(getParentX(x + 1), getParentY(z + 2)),
            area.get(getParentX(x + 0), getParentY(z + 1)),
            area.get(getParentX(x + 1), getParentY(z + 1))
        );
    }

    private int apply(IExtendedNoiseRandom<?> context, Plate north, Plate west, Plate south, Plate east, Plate center)
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
            if (boundary == null || context.nextRandom(boundaryCount) == 0)
            {
                boundary = west;
            }
        }
        if (!south.equals(center))
        {
            boundaryCount++;
            if (boundary != null || context.nextRandom(boundaryCount) == 0)
            {
                boundary = south;
            }
        }
        if (!east.equals(center))
        {
            boundaryCount++;
            if (boundary != null || context.nextRandom(boundaryCount) == 0)
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
        float distX = center.getX() - other.getX(), distZ = center.getZ() - other.getZ();
        float vX = center.getDriftX() - other.getDriftX(), vZ = center.getDriftZ() - other.getDriftZ();
        float delta = distX * vX + distZ * vZ;
        if (delta > SHEAR_THRESHOLD)
        {
            // Converging
            if (center.isOceanic() && other.isOceanic())
            {
                return OCEAN_OCEAN_CONVERGING;
            }
            else if (center.isOceanic() || other.isOceanic())
            {
                return OCEAN_CONTINENT_CONVERGING;
            }
            return CONTINENT_CONTINENT_CONVERGING;
        }
        else if (delta < -SHEAR_THRESHOLD)
        {
            // Diverging
            if (center.isOceanic() && other.isOceanic())
            {
                return OCEAN_OCEAN_DIVERGING;
            }
            else if (center.isOceanic() || other.isOceanic())
            {
                return OCEAN_CONTINENT_DIVERGING;
            }
            return CONTINENT_CONTINENT_DIVERGING;
        }
        return plate(center);
    }

    private int plate(Plate center)
    {
        if (center.isOceanic())
        {
            return OCEANIC;
        }
        if (center.getElevation() > HIGH_ELEVATION)
        {
            return CONTINENTAL_HIGH;
        }
        if (center.getElevation() > MID_ELEVATION)
        {
            return CONTINENTAL_MID;
        }
        return CONTINENTAL_LOW;
    }
}
