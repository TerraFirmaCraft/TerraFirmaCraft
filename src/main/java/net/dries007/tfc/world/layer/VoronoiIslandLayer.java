package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

/**
 * Generates land mass or oceans similar to a voronoi diagram
 */
public enum VoronoiIslandLayer
{
    INSTANCE;

    public static final int GRID_BITS = 3;
    public static final int GRID_SMOOTHING = 1;
    public static final int GRID_SIZE = (1 << GRID_BITS) - (2 * GRID_SMOOTHING);

    public <R extends IArea> IAreaFactory<R> apply(IExtendedNoiseRandom<R> context)
    {
        return () -> context.func_212861_a_((x, z) -> {
            long xCell = x >> GRID_BITS; // Grid point of the original
            long zCell = z >> GRID_BITS;
            context.setPosition(xCell, zCell);
            double xCenter = (xCell << GRID_BITS) + GRID_SMOOTHING + context.random(GRID_SIZE);
            double zCenter = (zCell << GRID_BITS) + GRID_SMOOTHING + context.random(GRID_SIZE);
            double closestDistSquared = (xCenter - x) * (xCenter - x) + (zCenter - z) * (zCenter - z);
            long xClosest = xCell;
            long zClosest = zCell;
            for (long i = xCell - 2; i <= xCell + 2; i++)
            {
                for (long j = zCell - 2; j <= zCell + 2; j++)
                {
                    context.setPosition(xCell + i, zCell + j);
                    double xOther = (i << GRID_BITS) + context.random(4);
                    double zOther = (j << GRID_BITS) + context.random(4);
                    double newClosestDistSquared = (xOther - x) * (xOther - x) + (zOther - z) * (zOther - z);
                    if (newClosestDistSquared < closestDistSquared)
                    {
                        closestDistSquared = newClosestDistSquared;
                        xClosest = i;
                        zClosest = j;
                    }
                }
            }
            context.setPosition(xClosest, zClosest);
            if ((xClosest == 0 && Math.abs(zClosest) <= 1) || (zClosest == 0 && Math.abs(xClosest) <= 1))
            {
                return TFCLayerUtil.PLAINS;
            }
            return context.random(12) == 0 ? TFCLayerUtil.PLAINS : TFCLayerUtil.DEEP_OCEAN;
        });
    }
}
