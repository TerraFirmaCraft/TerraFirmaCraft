package net.dries007.tfc.world.region;

import java.util.BitSet;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.util.Mth;

public enum AnnotateDistanceToWestCoast implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;

        for (int dx = 0; dx < region.sizeX(); dx++)
        {
            for (int dz = 0; dz < region.sizeZ(); dz++)
            {
                final int index = dx + region.sizeX() * dz;
                final Region.Point point = region.data()[index];
                if (point != null)
                {
                    if (!point.land() || dx == 0)
                    {
                        point.distanceToWestCoast = -1;
                    }
                    else
                    {
                        final Region.Point lastCenterPoint = region.data()[index - 1];
                        if (lastCenterPoint == null) {
                            //Sets a little starting bonus when a point in on the eastern border of a cell. The cusp will be smoothed out in the actual rain map
                            point.distanceToWestCoast = (byte) (25 + point.distanceToOcean);
                        }
                        else
                        {
                            final int lastCenterVal = lastCenterPoint.distanceToWestCoast;
                            int sum = 0;
                            //Can adjust the start and end point of this loop arbitrarily, just change the denominator of the average function too
                            for (int dz2 = -2; dz2 <= 2; dz2++)
                            {
                                if (dz < 1 - dz2 || dz >= region.sizeZ() - dz2 - 1)
                                {
                                    sum = sum + lastCenterVal;
                                }
                                else
                                {
                                    final int lastIndex = region.offset(index, -1, dz2);
                                    final Region.Point lastPoint = region.data()[lastIndex];
                                    if (lastPoint != null)
                                    {
                                        sum = sum + lastPoint.distanceToWestCoast;
                                    }
                                }
                            }
                            point.distanceToWestCoast = (byte) (Mth.ceil(sum / 5f) + 1);
                        }
                    }
                }
            }
        }
    }
}
